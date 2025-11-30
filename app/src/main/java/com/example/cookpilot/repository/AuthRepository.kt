package com.example.cookpilot.repository

import com.example.cookpilot.AppwriteClient
import com.example.cookpilot.ui.components.RegisterUser
import io.appwrite.ID
import io.appwrite.services.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset

class AuthRepository {
    private val account by lazy { Account(AppwriteClient.client) }
    private val databases by lazy { AppwriteClient.databases }
    private val databaseId = "691f3585001c7edb5dd2"
    private val usersCollectionId = "users"

    suspend fun registerUser(userForm: RegisterUser) = withContext(Dispatchers.IO) {
        val createdUser = account.create(
            userId = ID.unique(),
            email = userForm.email,
            password = userForm.password,
            name = userForm.user
        )

        delay(1000)

        val profileData = mutableMapOf<String, Any>(
            "userId" to createdUser.id,
            "username" to userForm.user,
            "email" to userForm.email
        )

        userForm.birthdate?.let { millis ->
            val iso = Instant.ofEpochMilli(millis)
                .atOffset(ZoneOffset.UTC)
                .toString() // ISO 8601
            profileData["birthdate"] = iso
        }

        databases.createDocument(
            databaseId = databaseId,
            collectionId = usersCollectionId,
            documentId = ID.unique(),
            data = profileData
        )
    }


    suspend fun loginUser(email: String, password: String) = withContext(Dispatchers.IO) {
        account.createEmailPasswordSession(
            email = email,
            password = password
        )
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        account.deleteSession(sessionId = "current")
    }
}
