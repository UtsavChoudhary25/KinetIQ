package com.utsav.kinetiq.data.local.db

import androidx.room.TypeConverter
import com.utsav.kinetiq.domain.model.WorkoutExercise
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KinetiqConverters {
    @TypeConverter
    fun fromWorkoutExerciseList(value: List<WorkoutExercise>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toWorkoutExerciseList(value: String): List<WorkoutExercise> {
        return Json.decodeFromString(value)
    }
}
