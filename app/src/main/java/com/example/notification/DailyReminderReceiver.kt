package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DailyReminderReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_CHECK_DAILY_REMINDER = "com.example.notification.ACTION_CHECK_DAILY_REMINDER"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action

        if (Intent.ACTION_BOOT_COMPLETED == action) {
            // Re-schedule alarm after device reboot if enabled
            if (DailyReminderScheduler.isReminderEnabled(context)) {
                val (hour, minute) = DailyReminderScheduler.getReminderTime(context)
                DailyReminderScheduler.scheduleDailyAlarm(context, hour, minute)
            }
            return
        }

        // Daily alarm trigger
        if (DailyReminderScheduler.isReminderEnabled(context)) {
            // Reschedule next occurrence for tomorrow
            val (hour, minute) = DailyReminderScheduler.getReminderTime(context)
            DailyReminderScheduler.scheduleDailyAlarm(context, hour, minute)

            // Check if user has not completed check-in or daily missions, and notify if pending
            DailyReminderScheduler.checkAndSendReminderIfPending(context, isTest = false)
        }
    }
}
