package com.utsav.kinetiq.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.utsav.kinetiq.domain.model.WorkoutExercise
import com.utsav.kinetiq.domain.model.WorkoutSession

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutId: String,
    val date: Long,
    val totalVolume: Float,
    val durationSeconds: Long,
    val completedExercises: List<WorkoutExercise>
)

fun WorkoutSessionEntity.toDomain() = WorkoutSession(
    id = id.toString(),
    workoutId = workoutId,
    date = date,
    totalVolume = totalVolume,
    durationSeconds = durationSeconds,
    completedExercises = completedExercises
)

fun WorkoutSession.toEntity() = WorkoutSessionEntity(
    id = if (id.isEmpty()) 0 else id.toInt(),
    workoutId = workoutId,
    date = date,
    totalVolume = totalVolume,
    durationSeconds = durationSeconds,
    completedExercises = completedExercises
)
