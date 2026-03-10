package com.inntemp.inningstempotracker.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val THEME_SELECTION = stringPreferencesKey("theme_selection")
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map {
        it[ONBOARDING_COMPLETED] ?: false
    }

    val themeSelection: Flow<String> = dataStore.data.map {
        it[THEME_SELECTION] ?: "light"
    }

    suspend fun setOnboardingCompleted() {
        dataStore.edit { it[ONBOARDING_COMPLETED] = true }
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { it[THEME_SELECTION] = theme }
    }

    suspend fun resetAll() {
        dataStore.edit { it.clear() }
    }
}
