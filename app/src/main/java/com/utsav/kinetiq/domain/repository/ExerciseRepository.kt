package com.utsav.kinetiq.domain.repository

import com.utsav.kinetiq.domain.model.Exercise
import com.utsav.kinetiq.domain.model.Workout
import com.utsav.kinetiq.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun getExercises(): Flow<List<Exercise>>
    fun getExerciseById(id: String): Flow<Exercise?>
    fun getWorkouts(): Flow<List<Workout>>
    fun getWorkoutById(id: String): Flow<Workout?>
    suspend fun saveWorkout(workout: Workout)
    fun getSessions(): Flow<List<WorkoutSession>>
    suspend fun saveSession(session: WorkoutSession)
}
