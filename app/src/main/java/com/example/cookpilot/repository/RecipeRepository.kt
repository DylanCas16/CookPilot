package com.example.cookpilot.repository

import APPWRITE_BUCKET_ID
import android.content.Context
import android.net.Uri
import com.example.cookpilot.AppwriteClient
import com.example.cookpilot.model.Recipe
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepository(
    private val appContext: Context
) {
    private val databaseId = "691f3585001c7edb5dd2"
    private val collectionId = "recipes"
    private val bucketId = APPWRITE_BUCKET_ID

    private val storage by lazy { Storage(AppwriteClient.client) }

    suspend fun createRecipeFromForm(
        title: String,
        description: String,
        steps: String,
        difficulty: Int,
        ingredients: List<String>,
        cookingTime: Int,
        creator: String,
        dietaryTags: List<String>,
        fileUri: Uri?
    ): Recipe = withContext(Dispatchers.IO) {
        val fileId = uploadImageAndGetFileId(fileUri)

        val data = mapOf(
            "title" to title,
            "description" to description,
            "steps" to steps,
            "difficulty" to difficulty,
            "ingredients" to ingredients,
            "cooking_time" to cookingTime,
            "creator" to creator,
            "dietaryTags" to dietaryTags,
            "fileId" to fileId
        )

        val doc = AppwriteClient.databases.createDocument(
            databaseId = databaseId,
            collectionId = collectionId,
            documentId = ID.unique(),
            data = data
        )

        Recipe.fromMap(doc.id, doc.data)
    }

    private suspend fun uploadImageAndGetFileId(fileUri: Uri?): String? =
        withContext(Dispatchers.IO) {
            if (fileUri == null) return@withContext null

            val inputStream = appContext.contentResolver.openInputStream(fileUri)
                ?: return@withContext null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val mimeType = appContext.contentResolver.getType(fileUri) ?: "image/jpeg"

            val inputFile = InputFile.fromBytes(
                bytes,
                "recipe_${System.currentTimeMillis()}.jpg",
                mimeType
            )

            val file = storage.createFile(
                bucketId = bucketId,
                fileId = ID.unique(),
                file = inputFile
            )

            file.id
        }

    suspend fun updateRecipe(
        recipeId: String,
        title: String,
        description: String,
        steps: String,
        difficulty: Int,
        ingredients: List<String>,
        cookingTime: Int,
        dietaryTags: List<String>,
        newImageUri: Uri?
    ): Recipe = withContext(Dispatchers.IO) {
        try {
            val currentDoc = AppwriteClient.databases.getDocument(
                databaseId = databaseId,
                collectionId = collectionId,
                documentId = recipeId
            )
            val oldFileId = currentDoc.data["fileId"] as? String
            val fileId = if (newImageUri != null) {
                val newFileId = uploadImageAndGetFileId(newImageUri)
                if (oldFileId != null && newFileId != null) {
                    try {
                        storage.deleteFile(bucketId = bucketId, fileId = oldFileId)
                    } catch (e: Exception) {
                    }
                }
                newFileId
            } else {
                oldFileId
            }

            val data = mapOf(
                "title" to title,
                "description" to description,
                "steps" to steps,
                "difficulty" to difficulty,
                "ingredients" to ingredients,
                "cooking_time" to cookingTime,
                "dietaryTags" to dietaryTags,
                "fileId" to fileId
            )

            val doc = AppwriteClient.databases.updateDocument(
                databaseId = databaseId,
                collectionId = collectionId,
                documentId = recipeId,
                data = data
            )

            Recipe.fromMap(doc.id, doc.data)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun deleteRecipe(recipeId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val doc = AppwriteClient.databases.getDocument(
                databaseId = databaseId,
                collectionId = collectionId,
                documentId = recipeId
            )

            val fileId = doc.data["fileId"] as? String

            if (fileId != null) {
                try {
                    storage.deleteFile(bucketId = bucketId, fileId = fileId)
                } catch (e: Exception) { }
            }

            AppwriteClient.databases.deleteDocument(
                databaseId = databaseId,
                collectionId = collectionId,
                documentId = recipeId
            )

            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getRecipesByCreator(userId: String): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val result = AppwriteClient.databases.listDocuments(
                databaseId = databaseId,
                collectionId = collectionId,
                queries = listOf(
                    Query.equal("creator", userId),
                    Query.orderDesc("\$createdAt")
                )
            )
            result.documents.map { doc -> Recipe.fromMap(doc.id, doc.data) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun getAllRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        try {
            val result = AppwriteClient.databases.listDocuments(
                databaseId = databaseId,
                collectionId = collectionId
            )
            result.documents.map { doc -> Recipe.fromMap(doc.id, doc.data) }
        } catch (_: Exception) {
            emptyList()
        }
    }
}
