package com.notsatria.bajet.ui.screen.settings

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.notsatria.bajet.R
import com.notsatria.bajet.ui.theme.BajetTheme
import com.notsatria.bajet.utils.ReminderScheduler
import com.notsatria.bajet.utils.ThemeMode
import com.notsatria.bajet.utils.getAppVersion
import com.notsatria.bajet.utils.openFeedbackEmail

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val theme by viewModel.theme.collectAsState(initial = "")
    val language by viewModel.language.collectAsState(initial = "")
    val reminderEnabledFlow by viewModel.reminderEnabled.collectAsState(initial = false)
    val reminderTimeFlow by viewModel.reminderTime.collectAsState(initial = "09:00")

    val showThemeDialog = remember { mutableStateOf(false) }
    val showLanguageDialog = remember { mutableStateOf(false) }
    val showReminderTimeDialog = remember { mutableStateOf(false) }
    val selectedReminderTime = remember(reminderTimeFlow) { mutableStateOf(reminderTimeFlow) }
    val reminderEnabled = remember(reminderEnabledFlow) { mutableStateOf(reminderEnabledFlow) }

    val notificationPermissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
    } else {
        null
    }
    val settings = getSettings(getAppVersion(context))

    // Effect to handle permission result - only update if permission was previously denied
    LaunchedEffect(notificationPermissionState?.status) {
        if (notificationPermissionState?.status?.isGranted == false && reminderEnabledFlow) {
            // Permission was denied, disable reminder only if it was enabled
            viewModel.setReminderEnabled(false)
        }
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

    if (showReminderTimeDialog.value) {
        ReminderTimePickerDialog(
            showDialog = showReminderTimeDialog,
            selectedTime = selectedReminderTime.value,
            onTimeSelected = { time ->
                selectedReminderTime.value = time
                viewModel.setReminderTime(time)
                showReminderTimeDialog.value = false
            }
        )
    }

    SettingScreen(
        modifier = modifier,
        settings = settings,
        reminderEnabled = reminderEnabled,
        selectedReminderTime = selectedReminderTime,
        onAction = { action ->
            when (action) {
                SettingAction.OpenThemeDialog -> {
                    showThemeDialog.value = true
                }

                SettingAction.OpenReminderTimeDialog -> {
                    showReminderTimeDialog.value = true
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

                is SettingAction.OnCheckReminder -> {
                    if (action.isEnabled) {
                        if (notificationPermissionState != null && !notificationPermissionState.status.isGranted) {
                            notificationPermissionState.launchPermissionRequest()
                        } else {
                            // Permission is already granted or not required
                            viewModel.setReminderEnabled(true)
                            val parts = selectedReminderTime.value.split(":")
                            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
                            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
                            ReminderScheduler.scheduleReminder(context, hour, minute)
                        }
                    } else {
                        // Cancel reminder
                        viewModel.setReminderEnabled(false)
                        ReminderScheduler.cancelReminder(context)
                    }
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
    reminderEnabled: MutableState<Boolean> = mutableStateOf(false),
    selectedReminderTime: MutableState<String> = mutableStateOf("09:00"),
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
                    text = stringResource(group.titleId),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                SettingsCard(
                    settings = group.settings,
                    reminderEnabled = reminderEnabled,
                    selectedReminderTime = selectedReminderTime,
                    onAction = onAction,
                    onCheckedChange = { isChecked ->
                        onAction(SettingAction.OnCheckReminder(isChecked))
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    settings: List<SettingItem>,
    reminderEnabled: MutableState<Boolean> = mutableStateOf(false),
    selectedReminderTime: MutableState<String> = mutableStateOf("09:00"),
    onAction: (SettingAction) -> Unit,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Card(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column {
            settings.forEachIndexed { index, item ->
                CustomSettingListItem(
                    item = item,
                    reminderEnabled = reminderEnabled,
                    selectedReminderTime = selectedReminderTime,
                    onAction = onAction,
                    onCheckedChange = onCheckedChange
                )
                if (index < settings.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun CustomSettingListItem(
    modifier: Modifier = Modifier,
    item: SettingItem,
    reminderEnabled: MutableState<Boolean> = mutableStateOf(false),
    selectedReminderTime: MutableState<String> = mutableStateOf("09:00"),
    onAction: (SettingAction) -> Unit,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    val isDailyReminder = item.titleId == SettingTitle.DAILY_REMINDER.resId

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = item.action != null && !isDailyReminder) {
                item.action?.let { onAction(it) }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Main row with icon, title, and trailing content
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading icon
            Icon(
                imageVector = item.icon,
                contentDescription = stringResource(item.titleId),
                modifier = Modifier.padding(end = 16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Title (with flex to push trailing content to the right)
            Text(
                text = stringResource(item.titleId),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            // Trailing content (description, switch, or nothing)
            when {
                item.hasSwitch -> {
                    Switch(
                        checked = reminderEnabled.value,
                        onCheckedChange = { isChecked ->
                            reminderEnabled.value = isChecked
                            onCheckedChange(isChecked)
                        },
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

                item.description != null -> {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        // Subtitle with time display (only shown if reminder is enabled and it's daily reminder)
        if (isDailyReminder && reminderEnabled.value) {
            Row(
                modifier = Modifier
                    .padding(start = 40.dp, top = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.subtitle ?: "Select reminder time",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                ReminderTimeDisplay(
                    time = selectedReminderTime.value,
                    onClick = {
                        if (reminderEnabled.value && item.action != null) {
                            onAction(item.action)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ReminderTimeDisplay(
    time: String = "09:00",
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontFamily = FontFamily.Monospace
            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTimePickerDialog(
    showDialog: MutableState<Boolean> = mutableStateOf(false),
    selectedTime: String = "09:00",
    onTimeSelected: (String) -> Unit = {}
) {
    // Parse the selected time to initialize the time picker
    val (initialHour, initialMinute) = selectedTime.split(":").let {
        Pair(it[0].toIntOrNull() ?: 9, it[1].toIntOrNull() ?: 0)
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text("Select Reminder Time") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(state = timePickerState)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val formattedTime = String.format(
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    onTimeSelected(formattedTime)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = { showDialog.value = false }) {
                Text("Cancel")
            }
        }
    )
}

@Preview
@Composable
fun SettingScreenPreview() {
    BajetTheme {
        SettingScreen(settings = getSettings("1.0.0-preview"))
    }
}

// preview for small device
@Preview(widthDp = 320, heightDp = 640)
@Composable
fun SettingScreenSmallPreview() {
    BajetTheme {
        SettingScreen(settings = getSettings("1.0.0-preview"))
    }
}

// preview for smaller device (320dp) (720x1280)
@Preview(widthDp = 320, heightDp = 720)
@Composable
fun SettingScreenSmallerPreview() {
    BajetTheme {
        SettingScreen(settings = getSettings("1.0.0-preview"))
    }
}