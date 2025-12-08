package com.example.cookpilot.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.cookpilot.model.MealPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalTime

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "meal_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val BREAKFAST_HOUR = intPreferencesKey("breakfast_hour")
        private val BREAKFAST_MINUTE = intPreferencesKey("breakfast_minute")
        private val LUNCH_HOUR = intPreferencesKey("lunch_hour")
        private val LUNCH_MINUTE = intPreferencesKey("lunch_minute")
        private val DINNER_HOUR = intPreferencesKey("dinner_hour")
        private val DINNER_MINUTE = intPreferencesKey("dinner_minute")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val DARK_MODE_ENABLED = booleanPreferencesKey("dark_mode_enabled")
    }

    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_ENABLED] ?: false
    }

    val mealPreferencesFlow: Flow<MealPreferences> = context.dataStore.data.map { preferences ->
        MealPreferences(
            breakfastTime = LocalTime.of(
                preferences[BREAKFAST_HOUR] ?: 8,
                preferences[BREAKFAST_MINUTE] ?: 0
            ),
            lunchTime = LocalTime.of(
                preferences[LUNCH_HOUR] ?: 14,
                preferences[LUNCH_MINUTE] ?: 0
            ),
            dinnerTime = LocalTime.of(
                preferences[DINNER_HOUR] ?: 21,
                preferences[DINNER_MINUTE] ?: 0
            ),
            notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: false
        )
    }

    suspend fun saveMealPreferences(mealPreferences: MealPreferences) {
        context.dataStore.edit { preferences ->
            preferences[BREAKFAST_HOUR] = mealPreferences.breakfastTime.hour
            preferences[BREAKFAST_MINUTE] = mealPreferences.breakfastTime.minute
            preferences[LUNCH_HOUR] = mealPreferences.lunchTime.hour
            preferences[LUNCH_MINUTE] = mealPreferences.lunchTime.minute
            preferences[DINNER_HOUR] = mealPreferences.dinnerTime.hour
            preferences[DINNER_MINUTE] = mealPreferences.dinnerTime.minute
            preferences[NOTIFICATIONS_ENABLED] = mealPreferences.notificationsEnabled
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_ENABLED] = enabled
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
