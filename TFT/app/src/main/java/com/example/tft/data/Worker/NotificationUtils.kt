package com.example.tft.data.Worker



import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.tft.data.Worker.EventNotificationWorker
import java.util.concurrent.TimeUnit

object NotificationUtils {
    fun scheduleEventNotifications(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<EventNotificationWorker>()
            .setInitialDelay(4, TimeUnit.HOURS) // Ajusta según la necesidad
            .addTag("event_notifications")
            .build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun cancelEventNotifications(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("event_notifications")
    }
}
