package com.example.cookpilot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookpilot.repository.AuthRepository
import com.example.cookpilot.ui.components.RegisterUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val _uiState = MutableStateFlow(UserUiState())

    fun register(user: RegisterUser) {
        viewModelScope.launch {
            _uiState.value = UserUiState(isLoading = true)
            try {
                authRepository.registerUser(user)
                _uiState.value = UserUiState(success = true)
            } catch (e: Exception) {
                _uiState.value = UserUiState(
                    success = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UserUiState(isLoading = true)
            try {
                authRepository.loginUser(email, password)
                _uiState.value = UserUiState(success = true)
            } catch (e: Exception) {
                _uiState.value = UserUiState(
                    success = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _uiState.value = UserUiState(success = true)
            } catch (e: Exception) { }
        }
    }
}
