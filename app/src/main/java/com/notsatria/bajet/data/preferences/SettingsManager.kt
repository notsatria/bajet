package com.notsatria.bajet.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "app_settings"

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class SettingsManager(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        val THEME_MODE =
            stringPreferencesKey("theme_mode")
        val CURRENCY = stringPreferencesKey("currency")
        val LANGUAGE = stringPreferencesKey("language")
        val PASSCODE = stringPreferencesKey("passcode")
        val REMINDER_ENABLED = stringPreferencesKey("reminder_enabled")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[THEME_MODE] = mode }
    }

    suspend fun setCurrency(currency: String) {
        dataStore.edit { it[CURRENCY] = currency }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[LANGUAGE] = language }
    }

    suspend fun setPasscode(passcode: String) {
        dataStore.edit { it[PASSCODE] = passcode }
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[REMINDER_ENABLED] = enabled.toString() }
    }

    suspend fun setReminderTime(time: String) {
        dataStore.edit { it[REMINDER_TIME] = time }
    }

    val themeMode: Flow<String?> = dataStore.data.map { it[THEME_MODE] }

    val currency: Flow<String> = dataStore.data.map { it[CURRENCY] ?: "IDR" }

    val language: Flow<String> = dataStore.data.map { it[LANGUAGE] ?: "id" }

    val passcode: Flow<String> = dataStore.data.map { it[PASSCODE] ?: "" }

    val reminderEnabled: Flow<Boolean> = dataStore.data.map { it[REMINDER_ENABLED]?.toBoolean() ?: false }

    val reminderTime: Flow<String> = dataStore.data.map { it[REMINDER_TIME] ?: "09:00" }
}
