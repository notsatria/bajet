package com.notsatria.bajet.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "app_data_store"

val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

class AppDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        val FIRST_LAUNCH_KEY = booleanPreferencesKey("first_launch")
    }

    suspend fun setFirstLaunch(isFirstLaunch: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH_KEY] = isFirstLaunch
        }
    }

    val isFirstLaunch = dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH_KEY] ?: true
    }
}