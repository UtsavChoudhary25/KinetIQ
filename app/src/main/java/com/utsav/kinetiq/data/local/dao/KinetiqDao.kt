package com.utsav.kinetiq.data.local.dao

import androidx.room.*
import com.utsav.kinetiq.data.local.entities.ExerciseEntity
import com.utsav.kinetiq.data.local.entities.WorkoutEntity
import com.utsav.kinetiq.data.local.entities.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KinetiqDao {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseById(id: String): Flow<ExerciseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Query("SELECT * FROM workouts")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutById(id: String): Flow<WorkoutEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity)

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity)
}
