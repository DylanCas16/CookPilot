package com.example.cookpilot.repository

import com.example.cookpilot.AppwriteClient
import com.example.cookpilot.model.Recipe
import io.appwrite.ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeRepository {

    private val databaseId = "691f3585001c7edb5dd2"
    private val collectionId = "recipes"

    suspend fun createRecipe(): Recipe = withContext(Dispatchers.IO) {
        val data = mapOf(
            "title" to "Paella",
            "difficulty" to 5,
            "cooking_time" to 90,
            "description" to "",
            "steps" to "1. Comprar arroz",
            "ingredients" to listOf("arroz", "marisco", "pescado", "limon"),
            "creator" to "Diddy"
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

        result.documents.map { doc ->
            Recipe.fromMap(doc.id, doc.data)
        }
    }
}
