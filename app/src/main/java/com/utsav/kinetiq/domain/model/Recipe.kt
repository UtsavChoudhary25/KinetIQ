package com.utsav.kinetiq.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val category: String, // e.g. "High Protein", "Low Carb", "Pre-Workout", "Post-Workout", "Meal Prep", "Vegan"
    val fitnessGoal: String, // e.g. "Muscle Growth", "Fat Loss", "Energy Booster", "Recovery", "Lean Cut"
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val servings: Int,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val imageUrl: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val isFavorite: Boolean = false
)
