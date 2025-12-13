package com.example.cookpilot.repository

import android.content.Context
import android.net.Uri
import APPWRITE_BUCKET_ID
import APPWRITE_DATABASE_ID
import APPWRITE_RECIPE_COLLECTION_ID
import com.example.cookpilot.model.Recipe
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.models.InputFile
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepository(
    private val appContext: Context,
    private val databases: Databases,
    private val storage: Storage
) {
    private val databaseId = APPWRITE_DATABASE_ID
    private val collectionId = APPWRITE_RECIPE_COLLECTION_ID
    private val bucketId = APPWRITE_BUCKET_ID

    suspend fun getAllRecipes(): List<Recipe> = withContext(Dispatchers.IO) {
        val result = databases.listDocuments(databaseId, collectionId)
        result.documents.map { Recipe.fromMap(it.id, it.data) }
    }

    suspend fun getRecipesByCreator(userId: String): List<Recipe> = withContext(Dispatchers.IO) {
        val result = databases.listDocuments(
            databaseId,
            collectionId,
            queries = listOf(Query.equal("creator", userId), Query.orderDesc("\$createdAt"))
        )
        result.documents.map { Recipe.fromMap(it.id, it.data) }
    }

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
    ) = withContext(Dispatchers.IO) {
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

        databases.createDocument(
            databaseId = databaseId,
            collectionId = collectionId,
            documentId = ID.unique(),
            data = data
        )
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
    ) = withContext(Dispatchers.IO) {
        val currentDoc = databases.getDocument(databaseId, collectionId, recipeId)
        val oldFileId = currentDoc.data["fileId"] as? String
        val fileId = if (newImageUri != null) {
            val newFileId = uploadImageAndGetFileId(newImageUri)
            oldFileId?.let {
                try { storage.deleteFile(bucketId, it) } catch (_: Exception) {}
            }
            newFileId
        } else oldFileId

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

        databases.updateDocument(databaseId, collectionId, recipeId, data)
    }

    suspend fun deleteRecipe(recipeId: String): Boolean = withContext(Dispatchers.IO) {
        val doc = databases.getDocument(databaseId, collectionId, recipeId)
        val fileId = doc.data["fileId"] as? String

        fileId?.let {
            try { storage.deleteFile(bucketId = bucketId, fileId = it) }
            catch (_: Exception) { }
        }

        databases.deleteDocument(databaseId, collectionId, recipeId)
        true
    }

    private suspend fun uploadImageAndGetFileId(fileUri: Uri?): String? = withContext(Dispatchers.IO) {
        if (fileUri == null) return@withContext null
        val inputStream = appContext.contentResolver.openInputStream(fileUri) ?: return@withContext null
        val bytes = inputStream.readBytes()
        inputStream.close()
        val mimeType = appContext.contentResolver.getType(fileUri) ?: "image/jpeg"
        val inputFile = InputFile.fromBytes(bytes, "recipe_${System.currentTimeMillis()}.jpg", mimeType)
        val file = storage.createFile(bucketId, ID.unique(), inputFile)
        file.id
    }
}
