package com.example.winss.utils

import android.content.Context
import android.os.Build
import androidx.work.*
import com.example.winss.reminders.HydrationWorker
import java.util.concurrent.TimeUnit
import java.util.*

class NotificationManager(private val context: Context) {
    
    private val workManager = WorkManager.getInstance(context)
    
    fun scheduleHydrationReminders(intervalMinutes: Int) {
        // Cancel existing reminders
        cancelAllReminders()

        if (intervalMinutes <= 0) return

        // For short intervals (like 1 minute), use AlarmManager for better reliability
        if (intervalMinutes <= 5) {
            scheduleAlarmBasedReminder(intervalMinutes)
        } else {
            // For longer intervals, try WorkManager first, then fallback to AlarmManager
            try {
                scheduleHydrationReminders(intervalMinutes, 9, 0, 21, 0)
            } catch (e: Exception) {
                // Fallback to AlarmManager if WorkManager fails
                scheduleAlarmBasedReminder(intervalMinutes)
            }
        }
    }

    private fun scheduleAlarmBasedReminder(intervalMinutes: Int) {
        // Schedule recurring reminders only (no immediate notification)
        com.example.winss.reminders.HydrationAlarmReceiver.scheduleAlarmReminder(context, intervalMinutes)
    }

    private fun showImmediateNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = android.app.NotificationChannel(
                "hydration_immediate",
                "Hydration Reminders",
                android.app.NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Hydration reminders"
                setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI, null)
                enableVibration(false)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = androidx.core.app.NotificationCompat.Builder(context, "hydration_immediate")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("💧 Drink Water")
            .setContentText("Stay hydrated!")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setDefaults(androidx.core.app.NotificationCompat.DEFAULT_SOUND)
            .setAutoCancel(true)
            .setStyle(androidx.core.app.NotificationCompat.BigTextStyle()
                .bigText("Stay hydrated and healthy! 💪"))
            .build()

        notificationManager.notify(998, notification)
    }

    private fun scheduleImmediateReminder(intervalMinutes: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()

        val inputData = Data.Builder()
            .putInt("interval_minutes", intervalMinutes)
            .putBoolean("is_repeating", true)
            .build()

        // Schedule first notification immediately
        val immediateRequest = OneTimeWorkRequestBuilder<HydrationWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(
            "hydration_reminder_immediate",
            ExistingWorkPolicy.REPLACE,
            immediateRequest
        )
    }

    fun scheduleHydrationReminders(
        intervalMinutes: Int,
        startHour: Int,
        startMinute: Int,
        endHour: Int,
        endMinute: Int
    ) {
        // Cancel existing reminders
        cancelAllReminders()

        if (intervalMinutes <= 0) return
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()

        // Calculate how many reminders fit in the time window
        val startTimeInMinutes = startHour * 60 + startMinute
        val endTimeInMinutes = endHour * 60 + endMinute
        val timeWindowMinutes = endTimeInMinutes - startTimeInMinutes

        if (timeWindowMinutes <= 0) return

        val numberOfReminders = (timeWindowMinutes / intervalMinutes).coerceAtLeast(1)

        // Schedule individual reminders
        for (i in 0 until numberOfReminders) {
            val reminderTimeInMinutes = startTimeInMinutes + (i * intervalMinutes)
            if (reminderTimeInMinutes >= endTimeInMinutes) break

            val reminderHour = reminderTimeInMinutes / 60
            val reminderMinute = reminderTimeInMinutes % 60

            scheduleReminderAt(reminderHour, reminderMinute, i)
        }
    }

    private fun scheduleReminderAt(hour: Int, minute: Int, index: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time has already passed today, schedule for tomorrow
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val currentTime = Calendar.getInstance().timeInMillis
        val scheduledTime = calendar.timeInMillis
        val initialDelayMinutes = (scheduledTime - currentTime) / (1000 * 60)

        val inputData = Data.Builder()
            .putInt("hour", hour)
            .putInt("minute", minute)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()
        
        val hydrationRequest = PeriodicWorkRequestBuilder<HydrationWorker>(
            1,
            TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .setInputData(inputData)
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            "hydration_reminder_$index",
            ExistingPeriodicWorkPolicy.REPLACE,
            hydrationRequest
        )
    }
    
    fun cancelHydrationReminders() {
        cancelAllReminders()
    }

    fun cancelAllReminders() {
        // Cancel WorkManager reminders
        workManager.cancelUniqueWork("hydration_reminders")
        workManager.cancelUniqueWork("hydration_reminder_short")
        workManager.cancelUniqueWork("hydration_reminder_immediate")

        // Cancel all individual reminders (assuming max 24 reminders per day)
        for (i in 0 until 24) {
            workManager.cancelUniqueWork("hydration_reminder_$i")
        }

        // Cancel AlarmManager-based reminders
        com.example.winss.reminders.HydrationAlarmReceiver.cancelAlarmReminder(context)
    }
}
