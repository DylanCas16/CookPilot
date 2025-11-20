package com.example.cookpilot

import io.appwrite.ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun createRecipe() = withContext(Dispatchers.IO) {
    val data = mapOf(
        "title" to "Tortilla de patatas",
        "difficulty" to 2,
        "cooking_time" to 30,
        "description" to "Clásica tortilla española.",
        "steps" to "Batir huevos, freír patatas, mezclar y cuajar.",
        "ingredients" to listOf("huevo", "patata", "aceite_oliva"),
        "creator" to "anon"
    )

    AppwriteClient.databases.createDocument(
        databaseId = "691f3585001c7edb5dd2",
        collectionId = "recipes",
        documentId = ID.unique(),
        data = data
    )
}
