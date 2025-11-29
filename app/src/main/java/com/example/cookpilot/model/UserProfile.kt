package com.example.cookpilot.model

data class UserProfile(
    val id: String,
    val userId: String,
    val username: String,
    val birthdate: Long,
    val email: String
) {
    companion object {
        fun fromMap(id: String, data: Map<String, Any?>): UserProfile {
            return UserProfile(
                id = id,
                userId = data["userId"] as? String ?: "",
                username = data["username"] as? String ?: "",
                birthdate = (data["birthdate"] as? Number)?.toLong() ?: 0L,
                email = data["email"] as? String ?: ""
            )
        }
    }
}
