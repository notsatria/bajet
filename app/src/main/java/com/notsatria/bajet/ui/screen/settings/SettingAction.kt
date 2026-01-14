package com.notsatria.bajet.ui.screen.settings

sealed class SettingAction {
    object OpenThemeDialog : SettingAction()
    object OpenLanguageDialog : SettingAction()
    object OpenReminderTimeDialog : SettingAction()
    object SendFeedback : SettingAction()
    object FeatureNotImplemented : SettingAction()
    data class OnCheckReminder(val isEnabled: Boolean) : SettingAction()
}