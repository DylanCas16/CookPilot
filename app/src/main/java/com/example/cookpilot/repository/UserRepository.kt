package com.example.cookpilot.repository

import APPWRITE_BUCKET_ID
import APPWRITE_DATABASE_ID
import APPWRITE_USER_COLLECTION_ID
import android.content.Context
import android.net.Uri
import com.example.cookpilot.AppwriteClient
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val appContext: Context) {
    private val databases by lazy { AppwriteClient.databases }
    private val storage by lazy { Storage(AppwriteClient.client) }
    private val databaseId = APPWRITE_DATABASE_ID
    private val usersCollectionId = APPWRITE_USER_COLLECTION_ID
    private val bucketId = APPWRITE_BUCKET_ID

    suspend fun uploadProfilePicture(imageUri: Uri): String = withContext(Dispatchers.IO) {
        val inputStream = appContext.contentResolver.openInputStream(imageUri)
            ?: throw Exception("Cannot open image file")

        val bytes = inputStream.readBytes()
        inputStream.close()

        val mimeType = appContext.contentResolver.getType(imageUri) ?: "image/jpeg"
        val inputFile = InputFile.fromBytes(
            bytes,
            "profile_${System.currentTimeMillis()}.jpg",
            mimeType
        )

        val file = storage.createFile(
            bucketId = bucketId,
            fileId = ID.unique(),
            file = inputFile
        )

        file.id
    }

    suspend fun updateProfilePicture(userId: String, fileId: String): Boolean =
        withContext(Dispatchers.IO) {
            val userDocs = databases.listDocuments(
                databaseId = databaseId,
                collectionId = usersCollectionId,
                queries = listOf(
                    Query.equal("userId", userId),
                    Query.limit(1)
                )
            )

            if (userDocs.documents.isEmpty()) throw Exception("User not found")

            val documentId = userDocs.documents[0].id
            databases.updateDocument(
                databaseId = databaseId,
                collectionId = usersCollectionId,
                documentId = documentId,
                data = mapOf("profilePictureId" to fileId)
            )

            true
        }

    suspend fun updateUsername(userId: String, newUsername: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val userDocs = databases.listDocuments(
                    databaseId = databaseId,
                    collectionId = usersCollectionId,
                    queries = listOf(
                        Query.equal("userId", userId),
                        Query.limit(1)
                    )
                )

                if (userDocs.documents.isEmpty()) throw Exception("User not found")

                val documentId = userDocs.documents[0].id
                databases.updateDocument(
                    databaseId = databaseId,
                    collectionId = usersCollectionId,
                    documentId = documentId,
                    data = mapOf("username" to newUsername)
                )

                true
            } catch (e: Exception) {
                throw Exception("Failed to update username: ${e.message}")
            }
        }

    suspend fun deleteProfilePicture(fileId: String): Boolean = withContext(Dispatchers.IO) {
        storage.deleteFile(
            bucketId = bucketId,
            fileId = fileId
        )
        true
    }
}
