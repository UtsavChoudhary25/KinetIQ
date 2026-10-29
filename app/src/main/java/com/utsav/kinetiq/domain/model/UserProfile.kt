package com.utsav.kinetiq.domain.model

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val age: Int = 0,
    val height: Float = 0f,
    val weight: Float = 0f,
    val fitnessGoal: String = "",
    val experienceLevel: String = "",
    val activityLevel: String = "",
    val availableEquipment: List<String> = emptyList(),
    val workoutDaysPerWeek: Int = 0,
    val preferredWorkoutDuration: Int = 0,
    val profilePictureUri: String = "",
    val onboardingCompleted: Boolean = false
)
