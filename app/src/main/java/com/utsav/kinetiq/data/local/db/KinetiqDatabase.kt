package com.utsav.kinetiq.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.utsav.kinetiq.data.local.dao.KinetiqDao
import com.utsav.kinetiq.data.local.entities.ExerciseEntity
import com.utsav.kinetiq.data.local.entities.WorkoutEntity
import com.utsav.kinetiq.data.local.entities.WorkoutSessionEntity

@Database(entities = [ExerciseEntity::class, WorkoutEntity::class, WorkoutSessionEntity::class], version = 2, exportSchema = false)
@TypeConverters(KinetiqConverters::class)
abstract class KinetiqDatabase : RoomDatabase() {
    abstract fun dao(): KinetiqDao

    companion object {
        @Volatile
        private var INSTANCE: KinetiqDatabase? = null

        fun getDatabase(context: android.content.Context): KinetiqDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    KinetiqDatabase::class.java,
                    "kinetiq_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
