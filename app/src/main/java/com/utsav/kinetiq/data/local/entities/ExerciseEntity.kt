package com.utsav.kinetiq.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.utsav.kinetiq.domain.model.Exercise

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val muscleGroup: String,
    val equipment: String,
    val instructions: String,
    val thumbnailUrl: String
)

fun ExerciseEntity.toDomain() = Exercise(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    equipment = equipment,
    instructions = instructions,
    thumbnailUrl = thumbnailUrl
)

fun Exercise.toEntity() = ExerciseEntity(
    id = id,
    name = name,
    muscleGroup = muscleGroup,
    equipment = equipment,
    instructions = instructions,
    thumbnailUrl = thumbnailUrl
)
