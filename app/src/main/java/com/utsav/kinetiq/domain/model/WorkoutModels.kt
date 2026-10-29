package com.utsav.kinetiq.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class SetType {
    WARMUP,
    WORKING,
    DROPSET,
    SUPERSET
}

@Serializable
data class Exercise(
    val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val instructions: String = "",
    val thumbnailUrl: String = ""
)

@Serializable
data class WorkoutSet(
    val reps: Int,
    val weight: Float,
    val isCompleted: Boolean = false,
    val setType: SetType = SetType.WORKING,
    val isPersonalRecord: Boolean = false
)

@Serializable
data class WorkoutExercise(
    val exercise: Exercise,
    val sets: List<WorkoutSet> = emptyList()
)

@Serializable
data class Workout(
    val id: String = "",
    val title: String,
    val durationMinutes: Int,
    val difficulty: String,
    val exercises: List<WorkoutExercise> = emptyList(),
    val aiRationale: String = "",
    val restSeconds: Int = 60
)

@Serializable
data class WorkoutSession(
    val id: String = "",
    val workoutId: String,
    val date: Long,
    val totalVolume: Float,
    val durationSeconds: Long,
    val completedExercises: List<WorkoutExercise> = emptyList(),
    val personalRecordsCount: Int = 0
)
