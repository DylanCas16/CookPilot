package com.example.cookpilot.data

import android.content.Context
import com.example.cookpilot.AppwriteClient
import com.example.cookpilot.notifications.NotificationScheduler
import com.example.cookpilot.repository.AuthRepository
import com.example.cookpilot.repository.HistoryRepository
import com.example.cookpilot.repository.RecipeRepository
import com.example.cookpilot.repository.UserRepository
import io.appwrite.services.Databases
import io.appwrite.services.Storage

class AppContainer(context: Context) {
    val databases = Databases(AppwriteClient.client)
    val storage = Storage(AppwriteClient.client)

    val preferencesManager = PreferencesManager(context)
    val notificationScheduler = NotificationScheduler(context)

    val authRepository = AuthRepository(databases)
    val userRepository = UserRepository(context, databases, storage)
    val recipeRepository = RecipeRepository(context, databases, storage)
    val historyRepository = HistoryRepository(databases)
}
