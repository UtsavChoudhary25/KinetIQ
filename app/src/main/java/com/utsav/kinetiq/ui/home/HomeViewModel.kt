package com.utsav.kinetiq.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.utsav.kinetiq.data.health.HealthConnectManager
import com.utsav.kinetiq.data.local.HealthPrefs
import com.utsav.kinetiq.data.local.LocalExerciseRepositoryImpl
import com.utsav.kinetiq.data.local.db.KinetiqDatabase
import com.utsav.kinetiq.data.remote.FirestoreUserRepositoryImpl
import com.utsav.kinetiq.domain.model.WorkoutSession
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val healthPrefs = HealthPrefs(application)
    private val healthConnectManager = HealthConnectManager(application)
    private val exerciseRepository = LocalExerciseRepositoryImpl(KinetiqDatabase.getDatabase(application).dao())
    private val userRepository = FirestoreUserRepositoryImpl()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val getHealthConnectManager: HealthConnectManager get() = healthConnectManager

    init {
        observeHealthData()
        observeUserData()
        observeWorkoutSessions()
        autoSyncHealthData()
    }

    fun autoSyncHealthData() {
        viewModelScope.launch {
            try {
                if (healthConnectManager.isHealthConnectAvailable() && healthConnectManager.hasAllPermissions()) {
                    healthConnectManager.fetchTodayHealthData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private var userProfileJob: Job? = null

    private fun observeUserData() {
        viewModelScope.launch {
            val auth = FirebaseAuth.getInstance()
            
            val updateNameForUser = {
                val user = auth.currentUser
                if (user != null) {
                    val initialName = when {
                        !user.displayName.isNullOrBlank() -> user.displayName
                        !user.email.isNullOrBlank() -> user.email?.substringBefore("@")?.replace(".", " ")?.replace("-", " ")?.split(" ")?.joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
                        else -> "Athlete"
                    } ?: "Athlete"

                    _uiState.value = _uiState.value.copy(userName = initialName)

                    userProfileJob?.cancel()
                    userProfileJob = viewModelScope.launch {
                        try {
                            userRepository.observeUserProfile(user.uid)
                                .catch { e -> e.printStackTrace() }
                                .collect { profile ->
                                    if (profile != null && profile.name.isNotBlank()) {
                                        _uiState.value = _uiState.value.copy(userName = profile.name)
                                    }
                                }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    userProfileJob?.cancel()
                    _uiState.value = _uiState.value.copy(userName = "Athlete")
                }
            }

            updateNameForUser()

            auth.addAuthStateListener {
                updateNameForUser()
            }
        }
    }

    private fun observeWorkoutSessions() {
        viewModelScope.launch {
            exerciseRepository.getSessions().collectLatest { sessions ->
                val calculatedStreak = calculateStreak(sessions)
                _uiState.value = _uiState.value.copy(workoutStreak = calculatedStreak)
            }
        }
    }

    private fun calculateStreak(sessions: List<WorkoutSession>): Int {
        if (sessions.isEmpty()) return 0

        return try {
            val zoneId = ZoneId.systemDefault()
            val uniqueDays = sessions.map { session ->
                Instant.ofEpochMilli(session.date).atZone(zoneId).toLocalDate()
            }.distinct().sortedDescending()

            if (uniqueDays.isEmpty()) return 0

            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            val latestDay = uniqueDays.first()

            if (latestDay.isBefore(yesterday)) {
                return 0
            }

            var streak = 0
            var checkDay = if (latestDay == today) today else yesterday

            for (day in uniqueDays) {
                if (day == checkDay) {
                    streak++
                    checkDay = checkDay.minusDays(1)
                } else if (day.isBefore(checkDay)) {
                    break
                }
            }

            streak
        } catch (_: Exception) {
            sessions.size.coerceAtMost(30)
        }
    }

    private fun observeHealthData() {
        viewModelScope.launch {
            healthPrefs.healthData.collectLatest { healthData ->
                if (healthData.isConnected) {
                    val lastSyncStr = if (healthData.lastSyncTimestamp > 0) {
                        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
                        "Synced at ${sdf.format(Date(healthData.lastSyncTimestamp))}"
                    } else "Synced from Google Fit"

                    _uiState.value = _uiState.value.copy(
                        dailyCalories = healthData.calories,
                        dailySteps = healthData.steps,
                        dailyDistanceKm = healthData.distanceKm,
                        activeMinutes = healthData.activeMinutes,
                        heartRate = healthData.heartRate,
                        dailyWater = 2.4f,
                        isGoogleFitConnected = true,
                        lastSyncTimestamp = healthData.lastSyncTimestamp,
                        aiInsight = "Google Fit connected ($lastSyncStr). You have completed ${healthData.steps} steps and covered ${healthData.distanceKm} KM today!"
                    )
                } else {
                    val currentName = _uiState.value.userName.ifBlank { "Athlete" }
                    _uiState.value = HomeUiState(
                        userName = currentName,
                        dailyCalories = 1820,
                        targetCalories = 2400,
                        dailySteps = 7842,
                        targetSteps = 10000,
                        dailyDistanceKm = 5.4f,
                        targetDistanceKm = 8.0f,
                        activeMinutes = 35,
                        heartRate = 72,
                        dailyWater = 2.1f,
                        targetWater = 3.0f,
                        todayWorkoutTitle = "Chest & Triceps",
                        todayWorkoutDuration = 45,
                        aiInsight = "Connect Google Fit in Profile to import real-time steps, calories & distance.",
                        workoutStreak = _uiState.value.workoutStreak,
                        isGoogleFitConnected = false
                    )
                }
            }
        }
    }

    fun syncGoogleFit() {
        viewModelScope.launch {
            healthConnectManager.fetchTodayHealthData()
        }
    }

    fun addLoggedCalories(calories: Int) {
        val current = _uiState.value
        val updatedCalories = current.dailyCalories + calories
        _uiState.value = current.copy(
            dailyCalories = updatedCalories,
            aiInsight = "Logged $calories kcal meal! Today's total: $updatedCalories / ${current.targetCalories} kcal."
        )
    }

    fun addWater(ml: Int) {
        val current = _uiState.value
        val updatedMl = current.waterLoggedMl + ml
        _uiState.value = current.copy(
            waterLoggedMl = updatedMl,
            dailyWater = updatedMl / 1000f
        )
    }
}
