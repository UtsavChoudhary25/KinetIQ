package com.utsav.kinetiq.ui.profile

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.utsav.kinetiq.data.health.HealthConnectManager
import com.utsav.kinetiq.data.local.HealthPrefs
import com.utsav.kinetiq.data.local.ImportedHealthData
import com.utsav.kinetiq.data.local.LocalExerciseRepositoryImpl
import com.utsav.kinetiq.data.local.db.KinetiqDatabase
import com.utsav.kinetiq.data.remote.FirestoreUserRepositoryImpl
import com.utsav.kinetiq.domain.model.ActivityTimeFrame
import com.utsav.kinetiq.domain.model.DailyActivityLog
import com.utsav.kinetiq.domain.model.MonthlyActivityLog
import com.utsav.kinetiq.domain.model.UserProfile
import com.utsav.kinetiq.domain.model.WeeklyActivityLog
import com.utsav.kinetiq.domain.model.WorkoutSession
import com.utsav.kinetiq.domain.repository.ExerciseRepository
import com.utsav.kinetiq.domain.repository.UserRepository
import com.utsav.kinetiq.utils.ActivityLogCalculator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed class ProfileEvent {
    object Success : ProfileEvent()
    data class Error(val message: String) : ProfileEvent()
}

class ProfileViewModel(
    application: Application,
    private val userRepository: UserRepository = FirestoreUserRepositoryImpl(),
    private val exerciseRepository: ExerciseRepository = LocalExerciseRepositoryImpl(KinetiqDatabase.getDatabase(application).dao())
) : AndroidViewModel(application) {

    private val healthPrefs = HealthPrefs(application)
    val healthConnectManager = HealthConnectManager(application)

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _healthData = MutableStateFlow(ImportedHealthData())
    val healthData: StateFlow<ImportedHealthData> = _healthData.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _profileEvent = MutableSharedFlow<ProfileEvent>()
    val profileEvent = _profileEvent.asSharedFlow()

    // Activity Logs
    private val _selectedTimeFrame = MutableStateFlow(ActivityTimeFrame.PREVIOUS_DAY)
    val selectedTimeFrame: StateFlow<ActivityTimeFrame> = _selectedTimeFrame.asStateFlow()

    private val _previousDayLog = MutableStateFlow(DailyActivityLog())
    val previousDayLog: StateFlow<DailyActivityLog> = _previousDayLog.asStateFlow()

    private val _previousWeekLog = MutableStateFlow(WeeklyActivityLog())
    val previousWeekLog: StateFlow<WeeklyActivityLog> = _previousWeekLog.asStateFlow()

    private val _previousMonthLog = MutableStateFlow(MonthlyActivityLog())
    val previousMonthLog: StateFlow<MonthlyActivityLog> = _previousMonthLog.asStateFlow()

    private var cachedSessions: List<WorkoutSession> = emptyList()

    init {
        loadUserProfile()
        observeHealthPrefs()
        observeWorkoutSessions()
    }

    private fun observeHealthPrefs() {
        viewModelScope.launch {
            healthPrefs.healthData.collect { data ->
                _healthData.value = data
                recalculateActivityLogs()
            }
        }
    }

    private fun observeWorkoutSessions() {
        viewModelScope.launch {
            exerciseRepository.getSessions().collectLatest { sessions ->
                cachedSessions = sessions
                recalculateActivityLogs()
            }
        }
    }

    private fun recalculateActivityLogs() {
        val health = _healthData.value
        _previousDayLog.value = ActivityLogCalculator.calculatePreviousDayLog(cachedSessions, health)
        _previousWeekLog.value = ActivityLogCalculator.calculatePreviousWeekLog(cachedSessions, health)
        _previousMonthLog.value = ActivityLogCalculator.calculatePreviousMonthLog(cachedSessions, health)
    }

    fun selectTimeFrame(timeFrame: ActivityTimeFrame) {
        _selectedTimeFrame.value = timeFrame
    }

    private fun loadUserProfile() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = userRepository.getUserProfile(userId)
            _isLoading.value = false
            result.onSuccess { profile ->
                _userProfile.value = profile ?: UserProfile(id = userId)
            }
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val updatedProfile = profile.copy(id = userId, onboardingCompleted = true)
            val result = userRepository.saveUserProfile(updatedProfile)
            _isLoading.value = false
            result.onSuccess {
                _userProfile.value = updatedProfile
                _profileEvent.emit(ProfileEvent.Success)
            }.onFailure {
                _profileEvent.emit(ProfileEvent.Error(it.message ?: "Failed to save profile"))
            }
        }
    }

    fun updateProfilePicture(imageUri: String) {
        val current = _userProfile.value ?: UserProfile()
        val updated = current.copy(profilePictureUri = imageUri)
        saveProfile(updated)
    }

    fun connectAndSyncGoogleFit() {
        viewModelScope.launch {
            _isSyncing.value = true
            healthConnectManager.fetchTodayHealthData()
            _isSyncing.value = false
            _profileEvent.emit(ProfileEvent.Success)
        }
    }

    fun saveCustomHealthData(
        steps: Int,
        calories: Int,
        distanceKm: Float,
        activeMinutes: Int,
        heartRate: Int
    ) {
        viewModelScope.launch {
            healthPrefs.saveHealthData(
                steps = steps,
                calories = calories,
                distanceKm = distanceKm,
                activeMinutes = activeMinutes,
                heartRate = heartRate,
                workoutsCount = if (steps > 0) 1 else 0
            )
            _profileEvent.emit(ProfileEvent.Success)
        }
    }

    fun disconnectGoogleFit() {
        viewModelScope.launch {
            healthPrefs.setConnected(false)
        }
    }

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    companion object {
        fun provideFactory(
            context: Context,
            userRepository: UserRepository = FirestoreUserRepositoryImpl(),
            exerciseRepository: ExerciseRepository? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val app = context.applicationContext as Application
                val repo = exerciseRepository ?: LocalExerciseRepositoryImpl(KinetiqDatabase.getDatabase(app).dao())
                return ProfileViewModel(app, userRepository, repo) as T
            }
        }
    }
}
