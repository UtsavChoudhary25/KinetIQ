package com.utsav.kinetiq.data.local

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class MealPlanEntry(
    val id: String,
    val dayOfWeek: String, // e.g., "Monday", "Tuesday", etc.
    val mealType: String, // e.g., "Breakfast", "Lunch", "Dinner", "Snack"
    val recipeId: String,
    val recipeTitle: String,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int
)

class MealPlannerPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("kinetiq_meal_planner_prefs", Context.MODE_PRIVATE)

    private val _mealPlans = MutableStateFlow<List<MealPlanEntry>>(emptyList())
    val mealPlans: StateFlow<List<MealPlanEntry>> = _mealPlans.asStateFlow()

    init {
        loadMealPlans()
    }

    private fun loadMealPlans() {
        val json = prefs.getString("meal_plans_json", "[]") ?: "[]"
        try {
            val items = Json.decodeFromString<List<MealPlanEntry>>(json)
            _mealPlans.value = items
        } catch (e: Exception) {
            _mealPlans.value = emptyList()
        }
    }

    private fun saveMealPlans(items: List<MealPlanEntry>) {
        _mealPlans.value = items
        val json = Json.encodeToString(items)
        prefs.edit().putString("meal_plans_json", json).apply()
    }

    fun addMealPlan(
        dayOfWeek: String,
        mealType: String,
        recipeId: String,
        recipeTitle: String,
        calories: Int,
        proteinGrams: Int,
        carbsGrams: Int,
        fatGrams: Int
    ) {
        val current = _mealPlans.value.toMutableList()
        val entry = MealPlanEntry(
            id = System.currentTimeMillis().toString(),
            dayOfWeek = dayOfWeek,
            mealType = mealType,
            recipeId = recipeId,
            recipeTitle = recipeTitle,
            calories = calories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams
        )
        current.add(entry)
        saveMealPlans(current)
    }

    fun removeMealPlan(id: String) {
        val current = _mealPlans.value.filter { it.id != id }
        saveMealPlans(current)
    }
}
