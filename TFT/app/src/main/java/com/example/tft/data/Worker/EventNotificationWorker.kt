package com.example.tft.data.Worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tft.MainActivity
import com.example.tft.R
import com.example.tft.data.Worker.NotificationUtils.scheduleEventNotifications
import com.example.tft.data.services.Authentication.AuthenticationServices
import com.example.tft.data.services.Event.EventServices
import com.example.tft.model.SavedEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class EventNotificationWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        Log.d("EventNotificationWorker", "Ejecutando el Worker para verificar eventos próximos")

        // Código del Worker...
        val userId = AuthenticationServices.getCurrentUserId() ?: return Result.failure()
        val events = EventServices.loadEvents(userId)
        events.filter { shouldNotifyUser(it.date) }.forEach { event ->
            notifyUser(event)
        }

        // Reprograma el `Worker` para ejecutarse nuevamente en un minuto
        scheduleEventNotifications(applicationContext)

        return Result.success()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun shouldNotifyUser(eventDate: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val now = LocalDate.now()
        val eventLocalDate = LocalDate.parse(eventDate, formatter)
        return ChronoUnit.DAYS.between(now, eventLocalDate) in 0..7
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun notifyUser(event: SavedEvent) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "event_notifications",
                "Event Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de eventos próximos"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Configura el Intent y PendingIntent
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // Configura y envía la notificación
        val builder = NotificationCompat.Builder(applicationContext, "event_notifications")
            .setSmallIcon(R.drawable.icons8_5_r) // Asegúrate de que este icono esté disponible
            .setContentTitle("Evento Próximo: ${event.title}")
            .setContentText("Tu evento ${event.title} es en menos de una semana: ${event.date}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(event.id.hashCode(), builder.build())
    }

}
