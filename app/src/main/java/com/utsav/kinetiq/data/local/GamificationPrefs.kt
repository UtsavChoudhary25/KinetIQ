package com.utsav.kinetiq.data.local

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false
)

class GamificationPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("kinetiq_gamification_prefs", Context.MODE_PRIVATE)

    private val _waterLoggedMl = MutableStateFlow(1250)
    val waterLoggedMl: StateFlow<Int> = _waterLoggedMl.asStateFlow()

    private val _badges = MutableStateFlow<List<Badge>>(emptyList())
    val badges: StateFlow<List<Badge>> = _badges.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val water = prefs.getInt("water_logged_ml", 1250)
        _waterLoggedMl.value = water

        val initialBadges = listOf(
            Badge("b_1", "Early Bird", "Logged workout before 8 AM", "🌅", true),
            Badge("b_2", "Hydration Hero", "Hit 3,000 ml water goal", "💧", water >= 3000),
            Badge("b_3", "7-Day Streak", "7 consecutive workout days", "🔥", true),
            Badge("b_4", "Chef Master", "Generated 5 AI recipes", "👨‍🍳", true),
            Badge("b_5", "PR Crusher", "Hit a new Personal Record", "🏋️", true)
        )
        _badges.value = initialBadges
    }

    fun addWater(ml: Int) {
        val newAmount = _waterLoggedMl.value + ml
        _waterLoggedMl.value = newAmount
        prefs.edit().putInt("water_logged_ml", newAmount).apply()

        if (newAmount >= 3000) {
            unlockBadge("b_2")
        }
    }

    fun unlockBadge(id: String) {
        val updated = _badges.value.map {
            if (it.id == id) it.copy(isUnlocked = true) else it
        }
        _badges.value = updated
        prefs.edit().putString("badges_json", Json.encodeToString(updated)).apply()
    }
}
