package com.example.cookpilot.model

import android.net.Uri

data class Recipe(
    val id: String? = null,
    val title: String,
    val description: String,
    val cookingTime: Int,
    val ingredients: List<String>,
    val steps: String,
    val difficulty: Int,
    val creator: String,
    val fileId: String? = null
) {
    companion object {
        fun fromMap(id: String, data: Map<String, Any?>): Recipe {
            return Recipe(
                id = id,
                title = data["title"] as? String ?: "",
                difficulty = (data["difficulty"] as? Number)?.toInt() ?: 0,
                cookingTime = (data["cooking_time"] as? Number)?.toInt() ?: 0,
                description = data["description"] as? String ?: "",
                steps = data["steps"] as? String ?: "",
                ingredients = (data["ingredients"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                creator = data["creator"] as? String ?: "",
                fileId = data["fileId"] as? String
            )
        }
    }
}
