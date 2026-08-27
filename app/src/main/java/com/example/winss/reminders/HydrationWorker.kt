package com.example.winss.reminders

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.winss.R
import java.util.concurrent.TimeUnit

class HydrationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return try {
            showHydrationNotification()

            // Check if this is a repeating notification for short intervals
            val isRepeating = inputData.getBoolean("is_repeating", false)
            val intervalMinutes = inputData.getInt("interval_minutes", 0)

            if (isRepeating && intervalMinutes > 0) {
                scheduleNextReminder(intervalMinutes)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun scheduleNextReminder(intervalMinutes: Int) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()

        val inputData = Data.Builder()
            .putInt("interval_minutes", intervalMinutes)
            .putBoolean("is_repeating", true)
            .build()

        val nextRequest = OneTimeWorkRequestBuilder<HydrationWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .setInitialDelay(intervalMinutes.toLong(), TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "hydration_reminder_immediate",
            ExistingWorkPolicy.REPLACE,
            nextRequest
        )
    }

    private fun showHydrationNotification() {
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Hydration Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you to stay hydrated"
                setSound(
                    android.provider.Settings.System.DEFAULT_NOTIFICATION_URI,
                    null
                )
                enableVibration(false)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle("💧 Drink Water")
            .setContentText("Stay hydrated!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Time for some water! Stay healthy 💪"))
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "hydration_reminder_channel"
        const val NOTIFICATION_ID = 1
    }
}
