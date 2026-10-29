package com.utsav.kinetiq.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.geminiDataStore: DataStore<Preferences> by preferencesDataStore(name = "gemini_prefs")

class GeminiPrefs(private val context: Context) {
    companion object {
        private val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
    }

    val apiKey: Flow<String?> = context.geminiDataStore.data.map { preferences ->
        preferences[GEMINI_API_KEY]
    }

    suspend fun saveApiKey(key: String) {
        context.geminiDataStore.edit { preferences ->
            preferences[GEMINI_API_KEY] = key
        }
    }

    suspend fun clearApiKey() {
        context.geminiDataStore.edit { preferences ->
            preferences.remove(GEMINI_API_KEY)
        }
    }
}
