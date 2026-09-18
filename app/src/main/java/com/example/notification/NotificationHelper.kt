package com.example.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    const val CHANNEL_ID = "game_cuan_daily_reminder"
    const val CHANNEL_NAME = "Pengingat Misi & Check-in Harian"
    const val CHANNEL_DESCRIPTION = "Notifikasi pengingat agar streak check-in koin dan misi harian tidak terlewat."

    const val NOTIFICATION_ID_DAILY_REMINDER = 1001
    const val NOTIFICATION_ID_TEST = 1002
    const val NOTIFICATION_ID_DANA_KAGET = 1003

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = NOTIFICATION_ID_DAILY_REMINDER
    ) {
        createNotificationChannel(context)

        if (!hasNotificationPermission(context)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("from_notification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val largeIconBitmap = try {
            BitmapFactory.decodeResource(context.resources, R.drawable.game_cuan_logo_1789625822440)
        } catch (_: Exception) {
            null
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_cuan_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setColor(0xFFF59E0B.toInt()) // Gold amber color
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)

        if (largeIconBitmap != null) {
            builder.setLargeIcon(largeIconBitmap)
        }

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission denied at runtime
        }
    }
}
