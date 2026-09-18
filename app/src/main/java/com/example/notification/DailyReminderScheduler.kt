package com.example.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.data.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DailyReminderScheduler {
    private const val PREFS_NAME = "game_cuan_reminder_prefs"
    private const val KEY_REMINDER_ENABLED = "key_reminder_enabled"
    private const val KEY_REMINDER_HOUR = "key_reminder_hour"
    private const val KEY_REMINDER_MINUTE = "key_reminder_minute"

    const val DEFAULT_HOUR = 19 // 19:00 / 7:00 PM
    const val DEFAULT_MINUTE = 0
    private const val ALARM_REQUEST_CODE = 8801

    fun isReminderEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_REMINDER_ENABLED, true)
    }

    fun setReminderEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
        if (enabled) {
            val (hour, minute) = getReminderTime(context)
            scheduleDailyAlarm(context, hour, minute)
        } else {
            cancelDailyAlarm(context)
        }
    }

    fun getReminderTime(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hour = prefs.getInt(KEY_REMINDER_HOUR, DEFAULT_HOUR)
        val minute = prefs.getInt(KEY_REMINDER_MINUTE, DEFAULT_MINUTE)
        return Pair(hour, minute)
    }

    fun setReminderTime(context: Context, hour: Int, minute: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_REMINDER_HOUR, hour)
            .putInt(KEY_REMINDER_MINUTE, minute)
            .apply()
        if (isReminderEnabled(context)) {
            scheduleDailyAlarm(context, hour, minute)
        }
    }

    fun scheduleDailyAlarm(context: Context, hour: Int = DEFAULT_HOUR, minute: Int = DEFAULT_MINUTE) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            action = DailyReminderReceiver.ACTION_CHECK_DAILY_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the set time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (_: SecurityException) {
            // Fallback to standard set if restricted
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } catch (_: Exception) {}
        }
    }

    fun cancelDailyAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DailyReminderReceiver::class.java).apply {
            action = DailyReminderReceiver.ACTION_CHECK_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun checkAndSendReminderIfPending(
        context: Context,
        isTest: Boolean = false,
        onResult: ((Boolean, String) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getInstance(context)
                val wallet = db.userWalletDao().getUserWalletDirect()
                val missions = db.dailyMissionDao().getAllMissionsDirect()

                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val hasCheckedIn = wallet?.lastCheckInDate == today
                val pendingMissions = missions.filter { !it.isCompleted || !it.isClaimed }

                when {
                    !hasCheckedIn -> {
                        val streak = (wallet?.streakDays ?: 0) + 1
                        val title = "🔥 Jangan Lewatkan Check-in Hari Ini!"
                        val message = "Kamu belum klaim bonus check-in hari ini! Masuk sekarang dan lanjutkan streak koin $streak harimu."
                        NotificationHelper.showNotification(context, title, message)
                        onResult?.invoke(true, "Pengingat check-in berhasil dikirim")
                    }
                    pendingMissions.isNotEmpty() -> {
                        val title = "🎯 Ada Misi Cuan Belum Selesai!"
                        val message = "Masih ada ${pendingMissions.size} misi harian yang belum tuntas. Selesaikan misi dan kumpulkan bonus poin sekarang!"
                        NotificationHelper.showNotification(context, title, message)
                        onResult?.invoke(true, "Pengingat misi harian berhasil dikirim")
                    }
                    isTest -> {
                        val (hour, minute) = getReminderTime(context)
                        val title = "✅ Notifikasi Game Cuan Aktif!"
                        val message = "Pengingat harian dijadwalkan setiap pukul ${String.format(Locale.getDefault(), "%02d:%02d", hour, minute)} WIB. Kamu akan diingatkan otomatis jika belum check-in!"
                        NotificationHelper.showNotification(context, title, message, NotificationHelper.NOTIFICATION_ID_TEST)
                        onResult?.invoke(true, "Notifikasi uji coba berhasil dikirim")
                    }
                    else -> {
                        // All tasks completed for today, no nagging needed!
                        onResult?.invoke(false, "Semua misi dan check-in hari ini sudah selesai")
                    }
                }
            } catch (e: Exception) {
                onResult?.invoke(false, "Gagal memproses pengingat: ${e.localizedMessage}")
            }
        }
    }
}
