package com.example.cookpilot.utils

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val type: ErrorType = ErrorType.GENERIC) : UiState<Nothing>()
}

enum class ErrorType {
    NETWORK,
    SERVER,
    AUTHENTICATION,
    GENERIC
}
