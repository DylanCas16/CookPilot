package com.example.cookpilot.model

data class Recipe(
    val title: String,
    val difficulty: Int,
    val cookingTime: Int,
    val description: String,
    val steps: String,
    val ingredients: List<String>,
    val creator: String,
    val imageUri: String? = null
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): Recipe {
            return Recipe(
                title = data["title"] as? String ?: "",
                difficulty = (data["difficulty"] as? Number)?.toInt() ?: 0,
                cookingTime = (data["cooking_time"] as? Number)?.toInt() ?: 0,
                description = data["description"] as? String ?: "",
                steps = data["steps"] as? String ?: "",
                ingredients = (data["ingredients"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                creator = data["creator"] as? String ?: "",
                imageUri = data["imageUri"] as? String
            )
        }
    }
}
