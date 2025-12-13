package com.example.cookpilot.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookpilot.data.PreferencesManager
import com.example.cookpilot.notifications.NotificationScheduler
import com.example.cookpilot.repository.AuthRepository
import com.example.cookpilot.repository.UserRepository
import com.example.cookpilot.ui.components.auth.RegisterUser
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

private const val USE_FAKE_LOGIN = false

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository()
    private val userRepository = UserRepository(application)
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()
    private val preferencesManager = PreferencesManager(application)
    private val notificationScheduler = NotificationScheduler(application)

    fun checkSession() {
        if (USE_FAKE_LOGIN) {
            _uiState.update {
                it.copy(
                    isLoggedIn = true,
                    userId = "dev-user-id",
                    userName = "Developer User",
                    profilePictureId = null,
                    error = null
                )
            }
            return
        }
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
                            profilePictureId = userData?.get("profilePictureId") as? String
                        )
                    }
                }
            }
        }
    }

    fun login(email: String, password: String) {
        if (USE_FAKE_LOGIN) {
            _uiState.update {
                it.copy(
                    isLoggedIn = true,
                    isLoading = false,
                    success = true,
                    userId = "dev-user-id",
                    userName = "Developer User",
                    profilePictureId = null,
                    showLoginDialog = false,
                    error = null
                )
            }
            return
        }
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

                val fileId = userRepository.uploadProfilePicture(imageUri)
                val oldFileId = _uiState.value.profilePictureId

                if (oldFileId != null) {
                    userRepository.deleteProfilePicture(oldFileId)
                }

                userRepository.updateProfilePicture(userId, fileId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        profilePictureId = fileId,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Failed to upload profile picture")
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
                login(user.email, user.password)
            } catch (e: Exception) {
                _uiState.value = UserUiState(
                    success = false,
                    error = e.message ?: "Unknown error"
                )
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.logout()
                notificationScheduler.cancelAllNotifications()
                preferencesManager.clearPreferences()
                _uiState.value = UserUiState(
                    isLoggedIn = false,
                    success = false,
                    error = null,
                    userId = null,
                    userName = null,
                    profilePictureId = null
                    )
                onLogoutComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Failed to logout: ${e.message}")
                }
            }
        }
    }

    fun clearAuthStatus() {
        _uiState.update {
            it.copy(
                success = false,
                error = null,
                isLoading = false
                // Mantener userId, userName, etc. si el login/register fue exitoso
            )
        }
    }
}
