package com.example.cookpilot.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.cookpilot.MainActivity
import com.example.cookpilot.R

class MealNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val mealType = inputData.getString("meal_type") ?: "Meal"
        showNotification(mealType)
        return Result.success()
    }

    private fun showNotification(mealType: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear canal de notificación (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Meal Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for meal times"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent para abrir la app al hacer click
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Mensajes personalizados según el tipo de comida
        val (title, message) = when (mealType) {
            "Breakfast" -> "Time for breakfast! 🌅" to "Start your day with a delicious recipe"
            "Lunch" -> "Lunch time! 🍽️" to "Discover what to cook for lunch"
            "Dinner" -> "Dinner time! 🌙" to "Find the perfect recipe for dinner"
            else -> "Meal time! 🍴" to "Check out our recipes"
        }

        // Construir notificación
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Cambia por tu icono
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(mealType.hashCode(), notification)
    }

    companion object {
        const val CHANNEL_ID = "meal_reminders_channel"
    }
}
