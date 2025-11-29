package com.example.cookpilot.repository

import com.example.cookpilot.AppwriteClient
import com.example.cookpilot.ui.components.User
import io.appwrite.ID
import io.appwrite.services.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AuthRepository {
    private val account by lazy { Account(AppwriteClient.client) }
    private val databases by lazy { AppwriteClient.databases }
    private val databaseId = "691f3585001c7edb5dd2"
    private val usersCollectionId = "users"

    suspend fun registerUser(userForm: User) = withContext(Dispatchers.IO) {
        val createdUser = account.create(
            userId = ID.unique(),
            email = userForm.email,
            password = userForm.password,
            name = userForm.user      // nombre visible
        )

        delay(1000)

        val profileData = mapOf(
            "userId" to createdUser.id,
            "username" to userForm.user,
            "birthdate" to clampBirthdate(userForm.birthdate),
            "email" to userForm.email
        )

        databases.createDocument(
            databaseId = databaseId,
            collectionId = usersCollectionId,
            documentId = ID.unique(),
            data = profileData
        )
    }

    private fun clampBirthdate(millis: Long): Long {
        val minDate = 1000L * 365L * 24L * 60L * 60L * 1000L
        val maxDate = 9999L * 365L * 24L * 60L * 60L * 1000L

        return millis.coerceIn(minDate, maxDate)
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        account.createEmailPasswordSession(
            email = email,
            password = password
        ) // [web:333][web:336]
    }
}
