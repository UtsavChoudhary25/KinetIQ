package com.utsav.kinetiq.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.healthDataStore: DataStore<Preferences> by preferencesDataStore(name = "health_prefs")

data class ImportedHealthData(
    val isConnected: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
    val steps: Int = 0,
    val calories: Int = 0,
    val distanceKm: Float = 0f,
    val activeMinutes: Int = 0,
    val heartRate: Int = 0,
    val workoutsCount: Int = 0
)

class HealthPrefs(private val context: Context) {

    companion object {
        private val IS_GOOGLE_FIT_CONNECTED = booleanPreferencesKey("is_google_fit_connected")
        private val LAST_SYNC_TIMESTAMP = longPreferencesKey("last_sync_timestamp")
        private val HEALTH_STEPS = intPreferencesKey("health_steps")
        private val HEALTH_CALORIES = intPreferencesKey("health_calories")
        private val HEALTH_DISTANCE_KM = floatPreferencesKey("health_distance_km")
        private val HEALTH_ACTIVE_MINUTES = intPreferencesKey("health_active_minutes")
        private val HEALTH_HEART_RATE = intPreferencesKey("health_heart_rate")
        private val HEALTH_WORKOUTS_COUNT = intPreferencesKey("health_workouts_count")
    }

    val healthData: Flow<ImportedHealthData> = context.healthDataStore.data.map { prefs ->
        ImportedHealthData(
            isConnected = prefs[IS_GOOGLE_FIT_CONNECTED] ?: false,
            lastSyncTimestamp = prefs[LAST_SYNC_TIMESTAMP] ?: 0L,
            steps = prefs[HEALTH_STEPS] ?: 0,
            calories = prefs[HEALTH_CALORIES] ?: 0,
            distanceKm = prefs[HEALTH_DISTANCE_KM] ?: 0f,
            activeMinutes = prefs[HEALTH_ACTIVE_MINUTES] ?: 0,
            heartRate = prefs[HEALTH_HEART_RATE] ?: 0,
            workoutsCount = prefs[HEALTH_WORKOUTS_COUNT] ?: 0
        )
    }

    suspend fun setConnected(connected: Boolean) {
        context.healthDataStore.edit { prefs ->
            prefs[IS_GOOGLE_FIT_CONNECTED] = connected
            if (!connected) {
                prefs[HEALTH_STEPS] = 0
                prefs[HEALTH_CALORIES] = 0
                prefs[HEALTH_DISTANCE_KM] = 0f
                prefs[HEALTH_ACTIVE_MINUTES] = 0
                prefs[HEALTH_HEART_RATE] = 0
                prefs[HEALTH_WORKOUTS_COUNT] = 0
                prefs[LAST_SYNC_TIMESTAMP] = 0L
            }
        }
    }

    suspend fun saveHealthData(
        steps: Int,
        calories: Int,
        distanceKm: Float,
        activeMinutes: Int,
        heartRate: Int,
        workoutsCount: Int
    ) {
        context.healthDataStore.edit { prefs ->
            prefs[IS_GOOGLE_FIT_CONNECTED] = true
            prefs[LAST_SYNC_TIMESTAMP] = System.currentTimeMillis()
            prefs[HEALTH_STEPS] = steps
            prefs[HEALTH_CALORIES] = calories
            prefs[HEALTH_DISTANCE_KM] = distanceKm
            prefs[HEALTH_ACTIVE_MINUTES] = activeMinutes
            prefs[HEALTH_HEART_RATE] = heartRate
            prefs[HEALTH_WORKOUTS_COUNT] = workoutsCount
        }
    }
}
