package com.example.cookpilot.notifications

import android.content.Context
import androidx.work.*
import com.example.cookpilot.model.MealPreferences
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class NotificationScheduler(private val context: Context) {

    fun scheduleMealNotifications(preferences: MealPreferences) {
        if (!preferences.notificationsEnabled) {
            cancelAllNotifications()
            return
        }

        scheduleMealNotification("Breakfast", preferences.breakfastTime)
        scheduleMealNotification("Lunch", preferences.lunchTime)
        scheduleMealNotification("Dinner", preferences.dinnerTime)
    }

    private fun scheduleMealNotification(mealType: String, mealTime: LocalTime) {
        val now = LocalDateTime.now()
        var scheduledTime = now.toLocalDate().atTime(mealTime)

        // Si la hora ya pasó hoy, programar para mañana
        if (scheduledTime.isBefore(now)) {
            scheduledTime = scheduledTime.plusDays(1)
        }

        val delay = Duration.between(now, scheduledTime).toMinutes()

        val data = Data.Builder()
            .putString("meal_type", mealType)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<MealNotificationWorker>(
            1, TimeUnit.DAYS  // Repetir cada día
        )
            .setInitialDelay(delay, TimeUnit.MINUTES)
            .setInputData(data)
            .addTag("meal_notification_$mealType")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "meal_notification_$mealType",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )

        println("✅ Scheduled $mealType notification for $scheduledTime")
    }

    fun cancelAllNotifications() {
        WorkManager.getInstance(context).cancelAllWorkByTag("meal_notification_Breakfast")
        WorkManager.getInstance(context).cancelAllWorkByTag("meal_notification_Lunch")
        WorkManager.getInstance(context).cancelAllWorkByTag("meal_notification_Dinner")
        println("❌ All meal notifications canceled")
    }
}
