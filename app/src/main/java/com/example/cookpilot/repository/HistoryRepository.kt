package com.example.cookpilot.repository

import APPWRITE_DATABASE_ID
import APPWRITE_HISTORY_COLLECTION_ID
import APPWRITE_RECIPE_COLLECTION_ID
import com.example.cookpilot.AppwriteClient
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.utils.ErrorType
import com.example.cookpilot.utils.UiState
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.services.Databases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

class HistoryRepository(private val databases: Databases) {
    private val databaseId = APPWRITE_DATABASE_ID
    private val historyCollectionId = APPWRITE_HISTORY_COLLECTION_ID
    private val recipesCollectionId = APPWRITE_RECIPE_COLLECTION_ID

    suspend fun saveRecipeView(userId: String, recipeId: String) = withContext(Dispatchers.IO) {
        try {
            val now = Instant.now().toString()
            val existing = AppwriteClient.databases.listDocuments(
                databaseId = databaseId,
                collectionId = historyCollectionId,
                queries = listOf(
                    Query.equal("userId", userId),
                    Query.equal("recipeId", recipeId)
                )
            )

            if (existing.documents.isEmpty()) {
                AppwriteClient.databases.createDocument(
                    databaseId = databaseId,
                    collectionId = historyCollectionId,
                    documentId = ID.unique(),
                    data = mapOf(
                        "userId" to userId,
                        "recipeId" to recipeId,
                        "viewedAt" to now
                    )
                )
            } else {
                AppwriteClient.databases.updateDocument(
                    databaseId = databaseId,
                    collectionId = historyCollectionId,
                    documentId = existing.documents[0].id,
                    data = mapOf("viewedAt" to now)
                )
            }
        } catch (_: Exception) { }
    }

    suspend fun getUserHistory(userId: String): UiState<List<Recipe>> = withContext(Dispatchers.IO) {
        try {
            val historyDocs = AppwriteClient.databases.listDocuments(
                databaseId = databaseId,
                collectionId = historyCollectionId,
                queries = listOf(
                    Query.equal("userId", userId),
                    Query.orderDesc("viewedAt"),
                    Query.limit(20)
                )
            )

            val recipeIds = historyDocs.documents.map { it.data["recipeId"] as String }
            if (recipeIds.isEmpty()) return@withContext UiState.Success(emptyList())

            val recipesQueries = recipeIds.take(10).map { Query.equal("\$id", it) }
            val recipesDocs = AppwriteClient.databases.listDocuments(
                databaseId = databaseId,
                collectionId = recipesCollectionId,
                queries = recipesQueries
            )

            val recipesMap = recipesDocs.documents
                .map { Recipe.fromMap(it.id, it.data) }
                .associateBy { it.id }

            UiState.Success(recipeIds.mapNotNull { recipesMap[it] })
        } catch (e: Exception) {
            UiState.Error("Failed to load history: ${e.message}", ErrorType.NETWORK)
        }
    }

    suspend fun clearUserHistory(userId: String) = withContext(Dispatchers.IO) {
        try {
            val historyDocs = AppwriteClient.databases.listDocuments(
                databaseId = databaseId,
                collectionId = historyCollectionId,
                queries = listOf(Query.equal("userId", userId))
            )

            historyDocs.documents.forEach { doc ->
                AppwriteClient.databases.deleteDocument(
                    databaseId = databaseId,
                    collectionId = historyCollectionId,
                    documentId = doc.id
                )
            }
        } catch (_: Exception) { }
    }
}
