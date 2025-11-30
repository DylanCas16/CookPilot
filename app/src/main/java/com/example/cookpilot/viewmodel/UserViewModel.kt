package com.example.cookpilot.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookpilot.repository.AuthRepository
import com.example.cookpilot.ui.components.RegisterUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserUiState(
    val showLoginDialog: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            val loggedIn = authRepository.hasActiveSession()
            _uiState.update { it.copy(isLoggedIn = loggedIn) }
        }
    }

    fun openLoginDialog() {
        _uiState.update { it.copy(showLoginDialog = true) }
    }

    fun closeLoginDialog() {
        _uiState.update { it.copy(showLoginDialog = false) }
    }

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
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.loginUser(email, password)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        success = true,
                        error = null,
                        showLoginDialog = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
                _uiState.update {
                    it.copy(
                        isLoggedIn = false,
                        success = true,
                        error = null
                    )
                }
            } catch (e: Exception) { }
        }
    }
}
