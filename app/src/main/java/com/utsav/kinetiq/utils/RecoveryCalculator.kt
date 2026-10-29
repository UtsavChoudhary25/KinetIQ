package com.utsav.kinetiq.utils

data class RecoveryScore(
    val score: Int, // 0 - 100
    val label: String, // e.g. "Optimal", "Good", "Moderate", "Needs Rest"
    val recommendation: String
)

object RecoveryCalculator {

    fun calculateReadiness(
        sleepHours: Float = 7.5f,
        activeCalories: Int = 450,
        targetCalories: Int = 500,
        stepsCount: Int = 8000
    ): RecoveryScore {
        var points = 0

        // Sleep score (Max 40 points)
        val sleepPoints = when {
            sleepHours >= 7.5f -> 40
            sleepHours >= 6.5f -> 32
            sleepHours >= 5.5f -> 24
            else -> 15
        }
        points += sleepPoints

        // Active Load score (Max 30 points)
        val calorieRatio = if (targetCalories > 0) activeCalories.toFloat() / targetCalories else 0.8f
        val loadPoints = when {
            calorieRatio in 0.7f..1.1f -> 30
            calorieRatio in 0.5f..1.3f -> 22
            else -> 15
        }
        points += loadPoints

        // Steps / Activity Balance (Max 30 points)
        val stepPoints = when {
            stepsCount >= 8000 -> 30
            stepsCount >= 5000 -> 22
            stepsCount >= 2000 -> 15
            else -> 10
        }
        points += stepPoints

        val finalScore = points.coerceIn(0, 100)

        val (label, rec) = when {
            finalScore >= 85 -> Pair("Optimal", "Your CNS and muscles are fully recovered. High intensity session recommended today!")
            finalScore >= 70 -> Pair("Good", "Good readiness level. Proceed with your planned workout program.")
            finalScore >= 50 -> Pair("Moderate", "Moderate fatigue detected. Focus on light cardio or mobility work.")
            else -> Pair("Needs Rest", "High strain accumulated. Prioritize hydration, sleep, and active recovery.")
        }

        return RecoveryScore(finalScore, label, rec)
    }
}
