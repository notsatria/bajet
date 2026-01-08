package com.notsatria.bajet.ui.screen.settings

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AppShortcut
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.MonetizationOn
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.bajet.R
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.ThemeMode

enum class SettingAction {
    OpenThemeDialog,
    OpenLanguageDialog,
    SendFeedback,
    FeatureNotImplemented,
}

data class SettingItem(
    @StringRes val title: Int,
    val icon: ImageVector,
    val action: SettingAction? = null,
    val description: String? = null
)

data class SettingGroup(
    @StringRes val title: Int,
    val settings: List<SettingItem>
)

private fun getSettings(appVersion: String): List<SettingGroup> {
    return listOf(
        SettingGroup(
            title = R.string.configuration,
            settings = listOf(
                SettingItem(
                    title = R.string.theme,
                    icon = Icons.Outlined.ColorLens,
                    action = SettingAction.OpenThemeDialog
                ),
                SettingItem(
                    title = R.string.language,
                    icon = Icons.Outlined.Language,
                    action = SettingAction.OpenLanguageDialog
                ),
                SettingItem(
                    title = R.string.currency,
                    icon = Icons.Outlined.MonetizationOn,
                    action = SettingAction.FeatureNotImplemented
                ),
                SettingItem(
                    title = R.string.passcode,
                    icon = Icons.Outlined.Password,
                    action = SettingAction.FeatureNotImplemented
                ),
                SettingItem(
                    title = R.string.backup,
                    icon = Icons.Outlined.Backup,
                    action = SettingAction.FeatureNotImplemented
                ),
            )
        ),
        SettingGroup(
            title = R.string.more,
            settings = listOf(
                SettingItem(
                    title = R.string.send_feedback,
                    icon = Icons.Outlined.Feedback,
                    action = SettingAction.SendFeedback
                ),
                SettingItem(
                    title = R.string.app_version,
                    icon = Icons.Outlined.AppShortcut,
                    description = appVersion
                )
            )
        )
    )
}

private fun openFeedbackEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "message/rfc822"
        putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.developer_email)))
        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.bajet_app_feedback))
        putExtra(Intent.EXTRA_TEXT, context.getString(R.string.bajet_app_feedback_extra))
    }
    try {
        context.startActivity(Intent.createChooser(intent, "Send Feedback"))
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "No email client found",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun SettingRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    context: Context = LocalContext.current
) {
    val theme by viewModel.theme.collectAsState(initial = "")
    val language by viewModel.language.collectAsState(initial = "")
    val showThemeDialog = remember { mutableStateOf(false) }
    val showLanguageDialog = remember { mutableStateOf(false) }
    val appVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "N/A"
        } catch (e: Exception) {
            "N/A"
        }
    }
    val settings = remember(appVersion) {
        getSettings(appVersion)
    }

    if (showThemeDialog.value) {
        ThemeListDialog(
            showDialog = showThemeDialog,
            selectedTheme = theme ?: ThemeMode.SYSTEM.value,
            onThemeSelected = { themeMode ->
                viewModel.setThemeMode(themeMode.value)
                showThemeDialog.value = false
            }
        )
    }

    if (showLanguageDialog.value) {
        LanguageListDialog(
            showDialog = showLanguageDialog,
            selectedLanguage = language,
            onLanguageSelected = {
                viewModel.setLanguage(it)
                showLanguageDialog.value = false
            },
            languageCodes = viewModel.languageCode
        )
    }

    SettingScreen(
        modifier = modifier,
        settings = settings,
        onAction = { action ->
            when (action) {
                SettingAction.OpenThemeDialog -> {
                    showThemeDialog.value = true
                }

                SettingAction.SendFeedback -> {
                    openFeedbackEmail(context)
                }

                SettingAction.FeatureNotImplemented -> {
                    Toast
                        .makeText(
                            context,
                            "This feature is not implemented yet.",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }

                SettingAction.OpenLanguageDialog -> {
                    showLanguageDialog.value = true
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    settings: List<SettingGroup>,
    onAction: (SettingAction) -> Unit = {}
) {
    Scaffold(modifier = modifier, topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    stringResource(R.string.settings),
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        )
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(settings) { group ->
                Text(
                    text = stringResource(group.title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                SettingsCard(
                    settings = group.settings,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    settings: List<SettingItem>,
    onAction: (SettingAction) -> Unit
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            settings.forEachIndexed { index, item ->
                ListItem(
                    headlineContent = { Text(stringResource(item.title)) },
                    leadingContent = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = stringResource(item.title)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            item.action?.let { onAction(it) }
                        },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    trailingContent = {
                        if (item.description != null) {
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
                if (index < settings.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun ThemeListDialog(
    showDialog: MutableState<Boolean> = mutableStateOf(false),
    selectedTheme: String = "",
    onThemeSelected: (ThemeMode) -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(stringResource(R.string.theme)) },
        text = {
            Column(Modifier.selectableGroup()) {
                ThemeMode.entries.forEach { theme ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (theme.value == selectedTheme.lowercase()),
                                onClick = { onThemeSelected(theme) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (theme.value == selectedTheme.lowercase()),
                            onClick = null // null recommended for accessibility with screenreaders
                        )
                        Text(
                            text = stringResource(theme.resId),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

@Composable
fun LanguageListDialog(
    showDialog: MutableState<Boolean> = mutableStateOf(false),
    selectedLanguage: String = "",
    onLanguageSelected: (String) -> Unit = {},
    languageCodes: Map<String, Int>
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(stringResource(R.string.language)) },
        text = {
            Column(Modifier.selectableGroup()) {
                languageCodes.forEach { (code, language) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (code == selectedLanguage),
                                onClick = { onLanguageSelected(code) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (code == selectedLanguage),
                            onClick = null // null recommended for accessibility with screenreaders
                        )
                        Text(
                            text = stringResource(language),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}

@PreviewScreenSizes
@Preview
@Composable
fun SettingScreenPreview() {
    BajetTheme {
        SettingScreen(settings = getSettings("1.0.0-preview"))
    }
}