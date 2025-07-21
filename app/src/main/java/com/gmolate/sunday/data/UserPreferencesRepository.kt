package com.gmolate.sunday.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme")
        private val UV_NOTIFICATIONS_KEY = booleanPreferencesKey("uv_notifications")
        private val LOCATION_ENABLED_KEY = booleanPreferencesKey("location_enabled")
        private val VITAMIN_D_GOAL_KEY = intPreferencesKey("vitamin_d_goal")
    }

    val theme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME] ?: "system"
        }

    val uvNotificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UV_NOTIFICATIONS_KEY] ?: true
    }

    val locationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.LOCATION_ENABLED_KEY] ?: false
    }

    val vitaminDGoal: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.VITAMIN_D_GOAL_KEY] ?: 1000
    }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }

    suspend fun setUVNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.UV_NOTIFICATIONS_KEY] = enabled
        }
    }

    suspend fun setLocationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.LOCATION_ENABLED_KEY] = enabled
        }
    }

    suspend fun setVitaminDGoal(goal: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VITAMIN_D_GOAL_KEY] = goal
        }
    }
}
