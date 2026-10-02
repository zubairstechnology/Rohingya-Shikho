package com.zubtech.rohingyashikho.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    private val KEY_USER_NAME = stringPreferencesKey("user_name")
    private val KEY_USER_AGE = stringPreferencesKey("user_age")
    private val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // "light", "dark", "system"
    private val KEY_LANGUAGE = stringPreferencesKey("app_language") // "en", "rhg"

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] ?: false
        }

    val userName: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_USER_NAME]
        }
        
    val themeMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_THEME_MODE] ?: "system"
        }

    val appLanguage: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_LANGUAGE] ?: "en"
        }

    suspend fun saveUserProfile(name: String, age: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = name
            preferences[KEY_USER_AGE] = age
            preferences[KEY_ONBOARDING_COMPLETED] = true
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = mode
        }
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LANGUAGE] = language
        }
    }

    suspend fun clearUserProfile() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
