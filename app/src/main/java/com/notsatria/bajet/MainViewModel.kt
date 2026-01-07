package com.notsatria.bajet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.bajet.data.preferences.AppDataStore
import com.notsatria.bajet.data.preferences.SettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val appDataStore: AppDataStore
) :
    ViewModel() {

    val theme: Flow<String> = settingsManager.themeMode
    val language: Flow<String> = settingsManager.language

    val isFirstLaunch: Flow<Boolean> = appDataStore.isFirstLaunch

    fun setFirstLaunch(isFirstLaunch: Boolean) {
        viewModelScope.launch {
            appDataStore.setFirstLaunch(isFirstLaunch)
        }
    }
}