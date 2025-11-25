package com.example.cookpilot.repository

import android.content.Context
import android.net.Uri
import com.example.cookpilot.AppwriteClient
import com.example.cookpilot.model.Recipe
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepository(
    private val appContext: Context
) {
    private val databaseId = "691f3585001c7edb5dd2"
    private val collectionId = "recipes"
    private val bucketId = "6925e55b001dba9c68fc"

    private val storage by lazy { Storage(AppwriteClient.client) }

    suspend fun createRecipeFromForm(
        title: String,
        description: String,
        steps: String,
        difficulty: Int,
        ingredients: List<String>,
        cookingTime: Int,
        creator: String,
        imageUri: Uri?
    ): Recipe = withContext(Dispatchers.IO) {
        val imageUrl = uploadImageAndGetUrl(imageUri)

        val data = mapOf(
            "title" to title,
            "description" to description,
            "steps" to steps,
            "difficulty" to difficulty,
            "ingredients" to ingredients,
            "cooking_time" to cookingTime,
            "creator" to creator,
            "imageUrl" to imageUrl
        )

        val doc = AppwriteClient.databases.createDocument(
            databaseId = databaseId,
            collectionId = collectionId,
            documentId = ID.unique(),
            data = data
        )

        Recipe.fromMap(doc.id, doc.data)
    }

    suspend fun getAllRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        val result = AppwriteClient.databases.listDocuments(
            databaseId = databaseId,
            collectionId = collectionId
        )
        result.documents.map { doc -> Recipe.fromMap(doc.id, doc.data) }
    }

    private suspend fun uploadImageAndGetUrl(imageUri: Uri?): String? =
        withContext(Dispatchers.IO) {
            if (imageUri == null) return@withContext null
            val inputStream = appContext.contentResolver.openInputStream(imageUri)
                ?: return@withContext null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val inputFile = InputFile.fromBytes(
                bytes,
                "recipe_${System.currentTimeMillis()}.jpg"
            )

            val file = storage.createFile(
                bucketId = bucketId,
                fileId = ID.unique(),
                file = inputFile
            )

            val endpoint = "http://10.0.2.2/v1"
            "$endpoint/storage/buckets/$bucketId/files/${file.id}/view"
        }
}
