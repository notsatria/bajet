package com.notsatria.bajet.ui.screen.settings

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AppShortcut
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.ui.graphics.vector.ImageVector
import com.notsatria.bajet.R

enum class SettingTitle(@StringRes val resId: Int) {
    THEME(R.string.theme),
    LANGUAGE(R.string.language),
    DAILY_REMINDER(R.string.daily_reminder),
    CURRENCY(R.string.currency),
    PASSCODE(R.string.passcode),
    BACKUP(R.string.backup),
    SEND_FEEDBACK(R.string.send_feedback),
    APP_VERSION(R.string.app_version),
}

data class SettingItem(
    @StringRes val titleId: Int,
    val icon: ImageVector,
    val action: SettingAction? = null,
    val description: String? = null,
    val subtitle: String? = null,
    val hasSwitch: Boolean = false
)

data class SettingGroup(
    @StringRes val titleId: Int,
    val settings: List<SettingItem>
)

fun getSettings(appVersion: String): List<SettingGroup> {
    return listOf(
        SettingGroup(
            titleId = R.string.configuration,
            settings = listOf(
                SettingItem(
                    titleId = SettingTitle.DAILY_REMINDER.resId,
                    icon = Icons.Outlined.Timer,
                    action = SettingAction.OpenReminderTimeDialog,
                    subtitle = "Select reminder time",
                    hasSwitch = true
                ),
                SettingItem(
                    titleId = SettingTitle.THEME.resId,
                    icon = Icons.Outlined.ColorLens,
                    action = SettingAction.OpenThemeDialog
                ),
                SettingItem(
                    titleId = SettingTitle.LANGUAGE.resId,
                    icon = Icons.Outlined.Language,
                    action = SettingAction.OpenLanguageDialog
                ),
                SettingItem(
                    titleId = SettingTitle.CURRENCY.resId,
                    icon = Icons.Outlined.MonetizationOn,
                    action = SettingAction.FeatureNotImplemented
                ),
                SettingItem(
                    titleId = SettingTitle.PASSCODE.resId,
                    icon = Icons.Outlined.Password,
                    action = SettingAction.FeatureNotImplemented
                ),
                SettingItem(
                    titleId = SettingTitle.BACKUP.resId,
                    icon = Icons.Outlined.Backup,
                    action = SettingAction.FeatureNotImplemented
                ),
            )
        ),
        SettingGroup(
            titleId = R.string.more,
            settings = listOf(
                SettingItem(
                    titleId = SettingTitle.SEND_FEEDBACK.resId,
                    icon = Icons.Outlined.Feedback,
                    action = SettingAction.SendFeedback
                ),
                SettingItem(
                    titleId = SettingTitle.APP_VERSION.resId,
                    icon = Icons.Outlined.AppShortcut,
                    description = appVersion
                )
            )
        )
    )
}