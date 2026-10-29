package com.utsav.kinetiq.ui.home

data class HomeUiState(
    val userName: String = "Utsav",
    val dailyCalories: Int = 0,
    val targetCalories: Int = 2400,
    val dailySteps: Int = 0,
    val targetSteps: Int = 10000,
    val dailyDistanceKm: Float = 0f,
    val targetDistanceKm: Float = 8.0f,
    val activeMinutes: Int = 0,
    val heartRate: Int = 0,
    val dailyWater: Float = 0f,
    val targetWater: Float = 3.0f,
    val waterLoggedMl: Int = 1250,
    val targetWaterMl: Int = 3000,
    val readinessScore: Int = 88,
    val readinessLabel: String = "Optimal",
    val readinessRecommendation: String = "CNS and muscles are fully recovered. High intensity session recommended today!",
    val todayWorkoutTitle: String = "Chest & Triceps",
    val todayWorkoutDuration: Int = 45,
    val aiInsight: String = "",
    val workoutStreak: Int = 12,
    val isGoogleFitConnected: Boolean = false,
    val lastSyncTimestamp: Long = 0L
)
