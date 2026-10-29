package com.utsav.kinetiq.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.utsav.kinetiq.domain.model.Workout
import com.utsav.kinetiq.domain.model.WorkoutExercise

@Entity(tableName = "workouts")
data class WorkoutEntity(
    @PrimaryKey val id: String,
    val title: String,
    val durationMinutes: Int,
    val difficulty: String,
    val exercises: List<WorkoutExercise>
)

fun WorkoutEntity.toDomain() = Workout(
    id = id,
    title = title,
    durationMinutes = durationMinutes,
    difficulty = difficulty,
    exercises = exercises
)

fun Workout.toEntity() = WorkoutEntity(
    id = id,
    title = title,
    durationMinutes = durationMinutes,
    difficulty = difficulty,
    exercises = exercises
)
