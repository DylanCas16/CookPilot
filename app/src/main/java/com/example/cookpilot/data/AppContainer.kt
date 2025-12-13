package com.example.cookpilot.data

import HistoryRepository
import android.content.Context
import com.example.cookpilot.notifications.NotificationScheduler
import com.example.cookpilot.repository.AuthRepository
import com.example.cookpilot.repository.RecipeRepository
import com.example.cookpilot.repository.UserRepository

class AppContainer(appContext: Context) {
    val authRepository = AuthRepository()
    val userRepository = UserRepository(appContext)
    val recipeRepository = RecipeRepository(appContext)
    val historyRepository = HistoryRepository()

    val preferencesManager = PreferencesManager(appContext)
    val notificationScheduler = NotificationScheduler(appContext)
}