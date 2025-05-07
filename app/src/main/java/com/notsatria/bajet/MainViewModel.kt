package com.notsatria.bajet

import androidx.lifecycle.ViewModel
import com.notsatria.bajet.data.preferences.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val settingsManager: SettingsManager) :
    ViewModel() {

    val theme = settingsManager.themeMode
}