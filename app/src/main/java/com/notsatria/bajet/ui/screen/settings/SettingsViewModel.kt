package com.notsatria.bajet.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.R
import com.notsatria.bajet.data.preferences.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsManager: SettingsManager) :
    ViewModel() {

    val theme: Flow<String?> = settingsManager.themeMode
    val language: Flow<String> = settingsManager.language
    val reminderEnabled: Flow<Boolean> = settingsManager.reminderEnabled
    val reminderTime: Flow<String> = settingsManager.reminderTime

    val languageCode: Map<String, Int> = mapOf(
        "id" to R.string.indonesian,
        "en" to R.string.english
    )

    fun setThemeMode(theme: String) {
        viewModelScope.launch {
            settingsManager.setThemeMode(theme)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsManager.setLanguage(language)
        }
    }

    fun setReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsManager.setReminderEnabled(enabled)
        }
    }

    fun setReminderTime(time: String) {
        viewModelScope.launch {
            settingsManager.setReminderTime(time)
        }
    }
}