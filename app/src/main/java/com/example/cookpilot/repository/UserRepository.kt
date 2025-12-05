package com.example.cookpilot.repository

import APPWRITE_BUCKET_ID
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
    private val databaseId = "691f3585001c7edb5dd2"
    private val usersCollectionId = "users"
    private val bucketId = APPWRITE_BUCKET_ID

    // Subir imagen de perfil y devolver el fileId
    suspend fun uploadProfilePicture(imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val inputStream = appContext.contentResolver.openInputStream(imageUri)
                ?: return@withContext null

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

            println("✅ Profile picture uploaded: ${file.id}")
            file.id
        } catch (e: Exception) {
            println("❌ Error uploading profile picture: ${e.message}")
            null
        }
    }

    // Actualizar el profilePictureId en la colección users
    suspend fun updateProfilePicture(userId: String, fileId: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                // Buscar el documento del usuario por userId
                val userDocs = databases.listDocuments(
                    databaseId = databaseId,
                    collectionId = usersCollectionId,
                    queries = listOf(
                        Query.equal("userId", userId),
                        Query.limit(1)
                    )
                )

                if (userDocs.documents.isEmpty()) {
                    println("❌ User document not found")
                    return@withContext false
                }

                val documentId = userDocs.documents[0].id

                // Actualizar con el nuevo fileId
                databases.updateDocument(
                    databaseId = databaseId,
                    collectionId = usersCollectionId,
                    documentId = documentId,
                    data = mapOf("profilePictureId" to fileId)
                )

                println("✅ Profile picture updated in DB")
                true
            } catch (e: Exception) {
                println("❌ Error updating profile picture: ${e.message}")
                false
            }
        }

    // Obtener profilePictureId del usuario
    suspend fun getProfilePictureId(userId: String): String? = withContext(Dispatchers.IO) {
        try {
            val userDocs = databases.listDocuments(
                databaseId = databaseId,
                collectionId = usersCollectionId,
                queries = listOf(
                    Query.equal("userId", userId),
                    Query.limit(1)
                )
            )

            if (userDocs.documents.isEmpty()) return@withContext null

            userDocs.documents[0].data["profilePictureId"] as? String
        } catch (e: Exception) {
            println("❌ Error getting profile picture: ${e.message}")
            null
        }
    }

    // Eliminar imagen anterior de Storage (opcional pero recomendado)
    suspend fun deleteProfilePicture(fileId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            storage.deleteFile(
                bucketId = bucketId,
                fileId = fileId
            )
            println("✅ Old profile picture deleted")
            true
        } catch (e: Exception) {
            println("⚠️ Error deleting old picture: ${e.message}")
            false
        }
    }
}
