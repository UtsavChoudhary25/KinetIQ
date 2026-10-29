package com.utsav.kinetiq.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.utsav.kinetiq.MainActivity
import com.utsav.kinetiq.R
import java.util.Calendar

class NotificationHelper(private val context: Context) {

    companion object {
        const val WORKOUT_CHANNEL_ID = "kinetiq_notifications"
        const val WATER_CHANNEL_ID = "kinetiq_water_notifications"
        const val SLEEP_CHANNEL_ID = "kinetiq_sleep_notifications"

        // For backward compatibility
        const val CHANNEL_ID = WORKOUT_CHANNEL_ID

        const val WORKOUT_NOTIFICATION_ID = 1001
        const val WATER_NOTIFICATION_ID = 2001
        const val SLEEP_NOTIFICATION_ID = 2002

        const val ACTION_SHOW_NOTIFICATION = "com.utsav.kinetiq.ACTION_SHOW_NOTIFICATION"
        const val ACTION_WATER_REMINDER = "com.utsav.kinetiq.ACTION_WATER_REMINDER"
        const val ACTION_SLEEP_REMINDER = "com.utsav.kinetiq.ACTION_SLEEP_REMINDER"

        const val REQUEST_CODE_WORKOUT = 100
        const val REQUEST_CODE_WATER = 200
        const val REQUEST_CODE_SLEEP = 300
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val workoutChannel = NotificationChannel(
                WORKOUT_CHANNEL_ID,
                "Workout Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily workout and fitness reminders"
            }

            val waterChannel = NotificationChannel(
                WATER_CHANNEL_ID,
                "Water Intake Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders every 2 hours to drink water and stay hydrated"
            }

            val sleepChannel = NotificationChannel(
                SLEEP_CHANNEL_ID,
                "Sleep Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Nightly reminder at 11 PM to wind down for sleep"
            }

            notificationManager.createNotificationChannels(
                listOf(workoutChannel, waterChannel, sleepChannel)
            )
        }
    }

    fun createNotificationChannel() {
        createNotificationChannels()
    }

    fun scheduleAllNotifications() {
        scheduleDailyNotification()
        scheduleWaterNotification()
        scheduleSleepNotification()
    }

    fun scheduleDailyNotification() {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SHOW_NOTIFICATION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_WORKOUT,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9) // 9 AM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        setAlarm(alarmManager, calendar.timeInMillis, pendingIntent)
    }

    fun scheduleWaterNotification() {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_WATER_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_WATER,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule for 2 hours from now
        val triggerAtMillis = System.currentTimeMillis() + (2 * 60 * 60 * 1000L)

        setAlarm(alarmManager, triggerAtMillis, pendingIntent)
    }

    fun scheduleSleepNotification() {
        val alarmManager = context.getSystemService(AlarmManager::class.java) ?: return
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = ACTION_SLEEP_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_SLEEP,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 23) // 11 PM
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        setAlarm(alarmManager, calendar.timeInMillis, pendingIntent)
    }

    private fun setAlarm(
        alarmManager: AlarmManager,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    fun showNotification(
        title: String,
        message: String,
        channelId: String = WORKOUT_CHANNEL_ID,
        notificationId: Int = WORKOUT_NOTIFICATION_ID
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_premium_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }
}
