package com.utsav.kinetiq.ui.workout

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utsav.kinetiq.data.local.GeminiPrefs
import com.utsav.kinetiq.data.local.LocalExerciseRepositoryImpl
import com.utsav.kinetiq.data.remote.FirestoreWorkoutRepositoryImpl
import com.utsav.kinetiq.data.remote.GeminiRepository
import com.utsav.kinetiq.domain.model.*
import com.utsav.kinetiq.domain.repository.ExerciseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WorkoutViewModel(
    private val exerciseRepository: ExerciseRepository,
    private val geminiRepository: GeminiRepository? = null,
    private val geminiPrefs: GeminiPrefs? = null
) : ViewModel() {

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()

    private val _activeWorkout = MutableStateFlow<Workout?>(null)
    val activeWorkout: StateFlow<Workout?> = _activeWorkout.asStateFlow()

    private val _generatedWorkout = MutableStateFlow<Workout?>(null)
    val generatedWorkout: StateFlow<Workout?> = _generatedWorkout.asStateFlow()

    private val _timerSeconds = MutableStateFlow(0L)
    val timerSeconds: StateFlow<Long> = _timerSeconds.asStateFlow()

    private val _sessions = MutableStateFlow<List<WorkoutSession>>(emptyList())
    val sessions: StateFlow<List<WorkoutSession>> = _sessions.asStateFlow()

    init {
        loadWorkouts()
        loadSessions()
        seedData()
        observeApiKey()
    }

    private fun observeApiKey() {
        if (geminiPrefs != null && geminiRepository != null) {
            viewModelScope.launch {
                geminiPrefs.apiKey.collectLatest { key ->
                    if (!key.isNullOrBlank()) {
                        geminiRepository.updateApiKey(key)
                    }
                }
            }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            exerciseRepository.getSessions().collect {
                _sessions.value = it
            }
        }
    }

    private fun seedData() {
        viewModelScope.launch {
            (exerciseRepository as? LocalExerciseRepositoryImpl)?.seedDatabase()
        }
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            exerciseRepository.getWorkouts().collect {
                _workouts.value = it
            }
        }
    }

    companion object {
        fun provideFactory(
            repository: ExerciseRepository,
            geminiRepository: GeminiRepository? = null,
            geminiPrefs: GeminiPrefs? = null
        ): androidx.lifecycle.ViewModelProvider.Factory = 
            object : androidx.lifecycle.ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return WorkoutViewModel(repository, geminiRepository, geminiPrefs) as T
                }
            }
    }

    private var timerJob: kotlinx.coroutines.Job? = null
    private var restTimerJob: kotlinx.coroutines.Job? = null

    private val _restTimerSeconds = MutableStateFlow(0)
    val restTimerSeconds: StateFlow<Int> = _restTimerSeconds.asStateFlow()

    private val _isRestTimerActive = MutableStateFlow(false)
    val isRestTimerActive: StateFlow<Boolean> = _isRestTimerActive.asStateFlow()

    fun startRestTimer(seconds: Int = 60) {
        restTimerJob?.cancel()
        _restTimerSeconds.value = seconds
        _isRestTimerActive.value = true

        restTimerJob = viewModelScope.launch {
            while (_restTimerSeconds.value > 0) {
                kotlinx.coroutines.delay(1000)
                _restTimerSeconds.value -= 1
            }
            _isRestTimerActive.value = false
        }
    }

    fun skipRestTimer() {
        restTimerJob?.cancel()
        _restTimerSeconds.value = 0
        _isRestTimerActive.value = false
    }

    fun addRestTime(seconds: Int = 30) {
        _restTimerSeconds.value += seconds
    }

    fun startWorkout(workout: Workout) {
        _activeWorkout.value = workout
        _timerSeconds.value = 0
        startTimer()
    }

    fun clearGeneratedWorkout() {
        _generatedWorkout.value = null
    }

    fun startGeneratedWorkout() {
        _generatedWorkout.value?.let { workout ->
            startWorkout(workout)
            _generatedWorkout.value = null
        }
    }

    fun saveGeneratedWorkoutToLibrary() {
        val workout = _generatedWorkout.value ?: return
        viewModelScope.launch {
            exerciseRepository.saveWorkout(workout.copy(id = "saved_${System.currentTimeMillis()}"))
            loadWorkouts()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                _timerSeconds.value++
            }
        }
    }

    fun finishWorkout() {
        val currentWorkout = _activeWorkout.value ?: return
        val duration = _timerSeconds.value
        
        // Calculate total volume (Weight * Reps) for completed sets
        var totalVolume = 0f
        val completedExercises = currentWorkout.exercises.map { exercise ->
            val completedSets = exercise.sets.filter { it.isCompleted }
            completedSets.forEach { totalVolume += it.weight * it.reps }
            exercise.copy(sets = completedSets)
        }.filter { it.sets.isNotEmpty() }

        if (completedExercises.isNotEmpty()) {
            val session = WorkoutSession(
                workoutId = currentWorkout.id,
                date = System.currentTimeMillis(),
                totalVolume = totalVolume,
                durationSeconds = duration,
                completedExercises = completedExercises
            )
            
            viewModelScope.launch {
                exerciseRepository.saveSession(session)
                
                // Cloud Sync
                FirebaseAuth.getInstance().currentUser?.uid?.let { userId ->
                    FirestoreWorkoutRepositoryImpl().syncSession(userId, session)
                }
                
                _activeWorkout.value = null
                timerJob?.cancel()
            }
        } else {
            _activeWorkout.value = null
            timerJob?.cancel()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun toggleSetCompletion(exerciseIndex: Int, setIndex: Int) {
        val currentWorkout = _activeWorkout.value ?: return
        val updatedExercises = currentWorkout.exercises.toMutableList()
        val currentExercise = updatedExercises[exerciseIndex]
        val updatedSets = currentExercise.sets.toMutableList()
        val currentSet = updatedSets[setIndex]
        val newlyCompleted = !currentSet.isCompleted

        var isPr = false
        if (newlyCompleted) {
            // Check PR against historical sessions
            val exName = currentExercise.exercise.name
            val pastMaxWeight = _sessions.value.flatMap { session ->
                session.completedExercises
                    .filter { it.exercise.name.equals(exName, ignoreCase = true) }
                    .flatMap { it.sets }
            }.maxOfOrNull { it.weight } ?: 0f

            if (currentSet.weight > pastMaxWeight && currentSet.weight > 0f) {
                isPr = true
            }

            startRestTimer(currentWorkout.restSeconds)
        }
        
        updatedSets[setIndex] = currentSet.copy(
            isCompleted = newlyCompleted,
            isPersonalRecord = if (newlyCompleted) isPr else false
        )
        updatedExercises[exerciseIndex] = currentExercise.copy(sets = updatedSets)
        
        _activeWorkout.value = currentWorkout.copy(exercises = updatedExercises)
    }

    fun updateSetType(exerciseIndex: Int, setIndex: Int, setType: SetType) {
        val currentWorkout = _activeWorkout.value ?: return
        val updatedExercises = currentWorkout.exercises.toMutableList()
        val currentExercise = updatedExercises[exerciseIndex]
        val updatedSets = currentExercise.sets.toMutableList()
        val currentSet = updatedSets[setIndex]

        updatedSets[setIndex] = currentSet.copy(setType = setType)
        updatedExercises[exerciseIndex] = currentExercise.copy(sets = updatedSets)

        _activeWorkout.value = currentWorkout.copy(exercises = updatedExercises)
    }

    private val _isGenerating = mutableStateOf(false)
    val isGenerating: State<Boolean> = _isGenerating

    fun generateWorkout(
        goal: String, 
        muscleGroup: String, 
        time: Int, 
        equipment: String, 
        fitnessLevel: String = "Intermediate",
        workoutStyle: String = "Straight Sets",
        limitations: String = "None",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isGenerating.value = true
            val aiWorkout = try {
                geminiRepository?.generateWorkout(
                    goal = goal,
                    muscleGroup = muscleGroup,
                    durationMinutes = time,
                    equipment = equipment,
                    fitnessLevel = fitnessLevel,
                    workoutStyle = workoutStyle,
                    limitations = limitations
                )
            } catch (e: Exception) {
                null
            }
            
            val finalWorkout = if (aiWorkout != null && aiWorkout.exercises.isNotEmpty()) {
                aiWorkout
            } else {
                // Fallback to local database logic if Gemini API key is missing or network fails
                var allExercises = try {
                    exerciseRepository.getExercises().first()
                } catch (e: Exception) {
                    emptyList()
                }

                if (allExercises.isEmpty()) {
                    (exerciseRepository as? LocalExerciseRepositoryImpl)?.seedDatabase()
                    allExercises = try {
                        exerciseRepository.getExercises().first()
                    } catch (e: Exception) {
                        emptyList()
                    }
                }

                val filtered = if (allExercises.isNotEmpty()) {
                    val matching = allExercises.filter { 
                        it.muscleGroup.equals(muscleGroup, ignoreCase = true) || muscleGroup == "Full Body" 
                    }
                    if (matching.isNotEmpty()) matching.take(5) else allExercises.take(5)
                } else {
                    listOf(
                        Exercise("1", "Push-up", "Chest", "Bodyweight", "Keep body straight and core tight."),
                        Exercise("2", "Squat", "Legs", "Bodyweight", "Lower hips until thighs are parallel to ground."),
                        Exercise("3", "Bicep Curl", "Arms", "Dumbbells", "Curl weights upwards while keeping elbows tucked."),
                        Exercise("4", "Shoulder Press", "Shoulders", "Dumbbells", "Press weights overhead with full range of motion.")
                    )
                }

                val workoutExercises = filtered.map { exercise ->
                    WorkoutExercise(
                        exercise = exercise,
                        sets = listOf(
                            WorkoutSet(12, 15f),
                            WorkoutSet(12, 15f),
                            WorkoutSet(12, 15f)
                        )
                    )
                }

                Workout(
                    id = "gen_${System.currentTimeMillis()}",
                    title = "$muscleGroup $goal Plan",
                    durationMinutes = time,
                    difficulty = fitnessLevel,
                    aiRationale = "Selected optimal mechanical tension compound exercises targeting $muscleGroup for $fitnessLevel level.",
                    exercises = workoutExercises
                )
            }

            _generatedWorkout.value = finalWorkout
            _isGenerating.value = false
            onSuccess()
        }
    }
}
