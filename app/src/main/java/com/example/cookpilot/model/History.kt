package com.example.cookpilot.model
data class History(
    val id: String? = null,
    val userId: String,
    val recipeId: String,
    val viewedAt: String  // ISO 8601
) {
    companion object {
        fun fromMap(id: String, data: Map<String, Any?>): History {
            return History(
                id = id,
                userId = data["userId"] as? String ?: "",
                recipeId = data["recipeId"] as? String ?: "",
                viewedAt = data["viewedAt"] as? String ?: ""
            )
        }
    }
}
