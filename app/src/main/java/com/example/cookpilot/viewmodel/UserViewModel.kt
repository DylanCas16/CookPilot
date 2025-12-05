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
    val showRegisterDialog: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val userName: String? = null,
    val success: Boolean = false,
    val userId: String? = null,
    val error: String? = null
)

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            val loggedIn = authRepository.hasActiveSession()
            if (loggedIn) {
                val user = authRepository.getCurrentUser()
                val userName = user?.id?.let { authRepository.getUsernameByUserId(it) }
                _uiState.update {
                    it.copy(
                        isLoggedIn = true,
                        userId = user?.id,
                        userName = userName
                    )
                }
            }
        }
    }

    fun openLoginDialog() {
        _uiState.update { it.copy(showLoginDialog = true) }
    }

    fun closeLoginDialog() {
        _uiState.update { it.copy(showLoginDialog = false) }
    }

    fun openRegisterDialog() {
        _uiState.update { it.copy(showRegisterDialog = true) }
    }

    fun closeRegisterDialog() {
            _uiState.update { it.copy(showRegisterDialog = false) }
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
                val session = authRepository.loginUser(email, password)
                val user = authRepository.getCurrentUser()
                val userName = user?.id?.let { authRepository.getUsernameByUserId(it) }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        userId = user?.id,
                        userName = userName,
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
