package com.notsatria.bajet.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.notsatria.bajet.data.preferences.SettingsManager
import com.notsatria.bajet.data.preferences.settingsDataStore
import com.notsatria.bajet.utils.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val settingsManager = context.settingsDataStore

        val reminderEnabledFlow = settingsManager.data.map { it[SettingsManager.REMINDER_ENABLED]?.toBoolean() ?: false }
        val reminderTimeFlow = settingsManager.data.map { it[SettingsManager.REMINDER_TIME] ?: "09:00" }

        CoroutineScope(Dispatchers.IO).launch {
            val isReminderEnabled = reminderEnabledFlow.first()
            if (isReminderEnabled) {
                val reminderTime = reminderTimeFlow.first()
                val parts = reminderTime.split(":")
                val hourOfDay = parts[0].toIntOrNull() ?: 9
                val minute = parts[1].toIntOrNull() ?: 0

                ReminderScheduler.scheduleReminder(context, hourOfDay, minute)
            }
        }
    }
}