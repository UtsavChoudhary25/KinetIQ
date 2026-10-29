package com.utsav.kinetiq.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val helper = NotificationHelper(context)

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-schedule all alarms on boot
                helper.scheduleAllNotifications()
            }
            NotificationHelper.ACTION_SHOW_NOTIFICATION -> {
                helper.showNotification(
                    "Time to Move!",
                    "Your daily KINETIQ workout is waiting. Let's hit those goals!",
                    NotificationHelper.WORKOUT_CHANNEL_ID,
                    NotificationHelper.WORKOUT_NOTIFICATION_ID
                )
                helper.scheduleDailyNotification()
            }
            NotificationHelper.ACTION_WATER_REMINDER -> {
                helper.showNotification(
                    "Hydration Time! 💧",
                    "Time to drink a glass of water to stay hydrated and energized!",
                    NotificationHelper.WATER_CHANNEL_ID,
                    NotificationHelper.WATER_NOTIFICATION_ID
                )
                helper.scheduleWaterNotification()
            }
            NotificationHelper.ACTION_SLEEP_REMINDER -> {
                helper.showNotification(
                    "Time for Bed 🌙",
                    "It's 11 PM! Time to wind down and get a good night's rest.",
                    NotificationHelper.SLEEP_CHANNEL_ID,
                    NotificationHelper.SLEEP_NOTIFICATION_ID
                )
                helper.scheduleSleepNotification()
            }
        }
    }
}
