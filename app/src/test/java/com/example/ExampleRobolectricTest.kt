package com.example

import android.app.NotificationManager
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.notification.DailyReminderScheduler
import com.example.notification.NotificationHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Game Cuan", appName)
  }

  @Test
  fun `verify daily reminder scheduler preferences and alarm scheduling`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    DailyReminderScheduler.setReminderEnabled(context, true)
    assertTrue(DailyReminderScheduler.isReminderEnabled(context))

    DailyReminderScheduler.setReminderTime(context, 20, 0)
    val (hour, minute) = DailyReminderScheduler.getReminderTime(context)
    assertEquals(20, hour)
    assertEquals(0, minute)

    DailyReminderScheduler.scheduleDailyAlarm(context, hour, minute)
    DailyReminderScheduler.cancelDailyAlarm(context)
  }

  @Test
  fun `verify notification channel creation`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    NotificationHelper.createNotificationChannel(context)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = notificationManager.getNotificationChannel(NotificationHelper.CHANNEL_ID)
    assertNotNull(channel)
    assertEquals(NotificationHelper.CHANNEL_NAME, channel.name)
  }
}
