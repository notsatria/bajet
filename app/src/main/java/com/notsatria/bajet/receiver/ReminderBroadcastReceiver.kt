package com.notsatria.bajet.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.notsatria.bajet.utils.IntentAction
import com.notsatria.bajet.utils.NotificationHelper
import com.notsatria.bajet.utils.ReminderScheduler
import timber.log.Timber.Forest.i

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        i("ReminderBroadcastReceiver onReceive called, Intent action: ${intent.action}")
        if (intent.action == IntentAction.SEND_REMINDER) {
            NotificationHelper.sendReminderNotification(context)

            val hourOfDay = intent.getIntExtra("hourOfDay", 9)
            val minute = intent.getIntExtra("minute", 0)

            i("Reminder sent at scheduled time: %02d:%02d".format(hourOfDay, minute))
            ReminderScheduler.scheduleReminder(context, hourOfDay, minute)
        }
    }
}
