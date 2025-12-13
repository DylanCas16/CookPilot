package com.example.cookpilot.repository

import android.content.Context
import android.net.Uri
import APPWRITE_BUCKET_ID
import APPWRITE_DATABASE_ID
import APPWRITE_USER_COLLECTION_ID
import com.example.cookpilot.utils.ErrorType
import com.example.cookpilot.utils.UiState
import io.appwrite.Query
import io.appwrite.models.InputFile
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val appContext: Context,
    private val databases: Databases,
    private val storage: Storage
) {
    private val databaseId = APPWRITE_DATABASE_ID
    private val usersCollectionId = APPWRITE_USER_COLLECTION_ID
    private val bucketId = APPWRITE_BUCKET_ID

    private suspend fun getUserDocumentId(userId: String): UiState<String> = withContext(Dispatchers.IO) {
        try {
            val userDocs = databases.listDocuments(
                databaseId = databaseId,
                collectionId = usersCollectionId,
                queries = listOf(Query.equal("userId", userId), Query.limit(1))
            )
            if (userDocs.documents.isEmpty()) {
                return@withContext UiState.Error("User not found", ErrorType.GENERIC)
            }
            UiState.Success(userDocs.documents[0].id)
        } catch (e: Exception) {
            UiState.Error("Failed to find user document: ${e.message}", ErrorType.GENERIC)
        }
    }

    suspend fun deleteProfilePicture(fileId: String): UiState<Unit> = withContext(Dispatchers.IO) {
        try {
            storage.deleteFile(bucketId = bucketId, fileId = fileId)
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error("Failed to delete profile picture: ${e.message}", ErrorType.GENERIC)
        }
    }

    suspend fun uploadProfilePicture(imageUri: Uri): UiState<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = appContext.contentResolver.openInputStream(imageUri)
                ?: return@withContext UiState.Error("Cannot open image file", ErrorType.GENERIC)
            val bytes = inputStream.readBytes()
            inputStream.close()
            val mimeType = appContext.contentResolver.getType(imageUri) ?: "image/jpeg"
            val inputFile = InputFile.fromBytes(bytes, "profile_${System.currentTimeMillis()}.jpg", mimeType)
            val file = storage.createFile(bucketId = bucketId, fileId = io.appwrite.ID.unique(), file = inputFile)
            UiState.Success(file.id)
        } catch (e: Exception) {
            UiState.Error("Failed to upload profile picture: ${e.message}", ErrorType.GENERIC)
        }
    }

    suspend fun updateProfilePicture(userId: String, fileId: String): UiState<Unit> = withContext(Dispatchers.IO) {
        val docResult = getUserDocumentId(userId)
        if (docResult !is UiState.Success) return@withContext docResult as UiState<Unit>

        try {
            databases.updateDocument(
                databaseId = databaseId,
                collectionId = usersCollectionId,
                documentId = docResult.data,
                data = mapOf("profilePictureId" to fileId)
            )
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error("Failed to update profile picture: ${e.message}", ErrorType.GENERIC)
        }
    }

    suspend fun updateUsername(userId: String, newUsername: String): UiState<Unit> = withContext(Dispatchers.IO) {
        val docResult = getUserDocumentId(userId)
        if (docResult !is UiState.Success) return@withContext docResult as UiState<Unit>

        try {
            databases.updateDocument(
                databaseId = databaseId,
                collectionId = usersCollectionId,
                documentId = docResult.data,
                data = mapOf("username" to newUsername)
            )
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error("Failed to update username: ${e.message}", ErrorType.GENERIC)
        }
    }
}
