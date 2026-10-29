package com.utsav.kinetiq.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class MealAnalysisResult(
    val foodName: String = "Unknown Food Item",
    val estimatedCalories: Int = 0,
    val proteinGrams: Float = 0f,
    val carbsGrams: Float = 0f,
    val fatsGrams: Float = 0f,
    val healthRating: String = "Moderate",
    val breakdownItems: List<String> = emptyList(),
    val tips: String = ""
)
