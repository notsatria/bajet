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

    val themeMode: Flow<String> = dataStore.data.map { it[THEME_MODE] ?: "system" }

    val currency: Flow<String> = dataStore.data.map { it[CURRENCY] ?: "IDR" }

    val language: Flow<String> = dataStore.data.map { it[LANGUAGE] ?: "id" }

    val passcode: Flow<String> = dataStore.data.map { it[PASSCODE] ?: "" }
}
