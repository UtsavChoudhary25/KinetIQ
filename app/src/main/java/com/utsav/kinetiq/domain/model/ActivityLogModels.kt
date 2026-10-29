package com.utsav.kinetiq.domain.model

enum class ActivityTimeFrame(val label: String) {
    PREVIOUS_DAY("PREVIOUS DAY"),
    PREVIOUS_WEEK("PREVIOUS WEEK"),
    PREVIOUS_MONTH("PREVIOUS MONTH")
}

data class DailyActivityLog(
    val date: Long = 0L,
    val dateLabel: String = "",
    val workoutsCompleted: Int = 0,
    val totalWorkoutDurationMinutes: Long = 0L,
    val totalVolumeKg: Float = 0f,
    val steps: Int = 0,
    val calories: Int = 0,
    val activeMinutes: Int = 0,
    val distanceKm: Float = 0f,
    val status: String = "NO ACTIVITY",
    val statusDescription: String = ""
)

data class WeeklyActivityLog(
    val periodLabel: String = "",
    val totalWorkouts: Int = 0,
    val totalWorkoutDurationMinutes: Long = 0L,
    val totalVolumeKg: Float = 0f,
    val avgDailySteps: Int = 0,
    val totalCalories: Int = 0,
    val totalActiveMinutes: Int = 0,
    val activeDaysCount: Int = 0,
    val dailyBreakdown: List<DailyActivityLog> = emptyList(),
    val overallStatus: String = "LIGHT ACTIVITY",
    val summaryNotes: String = ""
)

data class MonthlyActivityLog(
    val periodLabel: String = "",
    val totalWorkouts: Int = 0,
    val totalWorkoutDurationMinutes: Long = 0L,
    val totalVolumeKg: Float = 0f,
    val avgDailySteps: Int = 0,
    val totalCalories: Int = 0,
    val totalActiveMinutes: Int = 0,
    val activeDaysCount: Int = 0,
    val weeklyBreakdown: List<WeeklyActivityLog> = emptyList(),
    val overallStatus: String = "CONSISTENT PROGRESS",
    val summaryNotes: String = ""
)
