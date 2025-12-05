package com.example.cookpilot.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookpilot.repository.AuthRepository
import com.example.cookpilot.repository.UserRepository
import com.example.cookpilot.ui.components.RegisterUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserUiState(
    val showRegisterDialog: Boolean = false,
    val profilePictureId: String? = null,
    val showLoginDialog: Boolean = false,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val userName: String? = null,
    val success: Boolean = false,
    val userId: String? = null,
    val error: String? = null
)

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository(application)
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            val loggedIn = authRepository.hasActiveSession()
            if (loggedIn) {
                val user = authRepository.getCurrentUser()
                user?.id?.let { userId ->
                    val userData = authRepository.getUserData(userId)
                    _uiState.update {
                        it.copy(
                            isLoggedIn = true,
                            userId = userId,
                            userName = userData?.get("username") as? String,
                            profilePictureId = userData?.get("profilePictureId") as? String  // ← CARGAR
                        )
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                authRepository.loginUser(email, password)
                val user = authRepository.getCurrentUser()

                user?.id?.let { userId ->
                    val userData = authRepository.getUserData(userId)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            userId = userId,
                            userName = userData?.get("username") as? String,
                            profilePictureId = userData?.get("profilePictureId") as? String,
                            success = true,
                            error = null,
                            showLoginDialog = false
                        )
                    }
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

    fun uploadProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val userId = _uiState.value.userId
                if (userId == null) {
                    _uiState.update { it.copy(isLoading = false, error = "No user logged in") }
                    return@launch
                }

                println("🔵 Uploading profile picture...")

                // 1. Subir imagen a Storage
                val fileId = userRepository.uploadProfilePicture(imageUri)
                if (fileId == null) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Failed to upload image")
                    }
                    return@launch
                }

                // 2. Eliminar imagen anterior si existe
                val oldFileId = _uiState.value.profilePictureId
                if (oldFileId != null) {
                    userRepository.deleteProfilePicture(oldFileId)
                }

                // 3. Actualizar documento del usuario
                val success = userRepository.updateProfilePicture(userId, fileId)

                if (success) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            profilePictureId = fileId,
                            error = null
                        )
                    }
                    println("✅ Profile picture updated successfully")
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Failed to update profile")
                    }
                }
            } catch (e: Exception) {
                println("❌ Error: ${e.message}")
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
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
            } catch (_: Exception) { }
        }
    }
}
