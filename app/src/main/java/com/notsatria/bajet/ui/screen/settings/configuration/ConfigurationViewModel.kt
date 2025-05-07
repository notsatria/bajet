package com.notsatria.bajet.ui.screen.settings.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.preferences.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(private val settingsManager: SettingsManager) :
    ViewModel() {

    val theme = settingsManager.themeMode

    fun setThemeMode(theme: String) {
        viewModelScope.launch {
            settingsManager.setThemeMode(theme)
        }
    }
}