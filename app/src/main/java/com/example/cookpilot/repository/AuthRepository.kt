package com.example.cookpilot.repository

import APPWRITE_DATABASE_ID
import APPWRITE_USER_COLLECTION_ID
import com.example.cookpilot.AppwriteClient
import com.example.cookpilot.ui.components.auth.RegisterUser
import com.example.cookpilot.utils.ErrorType
import com.example.cookpilot.utils.UiState
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.services.Account
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset

class AuthRepository(private val databases: io.appwrite.services.Databases) {
    private val account by lazy { Account(AppwriteClient.client) }
    private val databaseId = APPWRITE_DATABASE_ID
    private val usersCollectionId = APPWRITE_USER_COLLECTION_ID

    suspend fun getCurrentUser() = withContext(Dispatchers.IO) {
        return@withContext try {
            account.get()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getUserData(userId: String): UiState<Map<String, Any>?> = withContext(Dispatchers.IO) {
        try {
            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = usersCollectionId,
                queries = listOf(
                    Query.equal("userId", userId),
                    Query.limit(1)
                )
            )

            if (result.documents.isNotEmpty()) UiState.Success(result.documents[0].data)
            else UiState.Success(null)
        } catch (e: Exception) {
            UiState.Error("Failed to get user data: ${e.message}", ErrorType.GENERIC)
        }
    }

    suspend fun hasActiveSession(): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            account.get()
            true
        } catch (_: Exception) {
            false
        }
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

    suspend fun registerUser(userForm: RegisterUser) = withContext(Dispatchers.IO) {
        val createdUser = account.create(
            userId = ID.unique(),
            email = userForm.email,
            password = userForm.password,
            name = userForm.user
        )

        delay(1000)

        val profileData = mutableMapOf(
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

        createdUser
    }
}
