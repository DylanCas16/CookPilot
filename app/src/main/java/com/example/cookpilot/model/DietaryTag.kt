package com.example.cookpilot.model

import androidx.compose.ui.graphics.Color

enum class DietaryTag(
    val displayName: String,
    val color: Color,
    val emoji: String
) {
    VEGAN("Vegan", Color(0xFF4CAF50), "🌱"),
    VEGETARIAN("Vegetarian", Color(0xFF8BC34A), "🥬"),
    GLUTEN_FREE("Gluten Free", Color(0xFFFFC107), "🌾"),
    LACTOSE_FREE("Lactose Free", Color(0xFF2196F3), "🥛"),
    HALAL("Halal", Color(0xFF009688), "☪️"),
    KOSHER("Kosher", Color(0xFF673AB7), "✡️"),
    NUTS("Contains Nuts", Color(0xFFFF9800), "🥜"),
    SEAFOOD("Contains Seafood", Color(0xFF00BCD4), "🐟"),
    SUGAR_FREE("Sugar Free", Color(0xFFE91E63), "🍬"),
    KETO("Keto", Color(0xFF795548), "🥑"),
    PALEO("Paleo", Color(0xFF9E9E9E), "🦴");

    companion object {
        fun fromString(tag: String): DietaryTag? {
            return entries.find { it.name == tag }
        }
    }
}