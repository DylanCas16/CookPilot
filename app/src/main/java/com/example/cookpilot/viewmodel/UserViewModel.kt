package com.example.cookpilot.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cookpilot.data.AppContainer
import com.example.cookpilot.data.PreferencesManager
import com.example.cookpilot.notifications.NotificationScheduler
import com.example.cookpilot.repository.AuthRepository
import com.example.cookpilot.repository.UserRepository
import com.example.cookpilot.ui.components.auth.RegisterUser
import com.example.cookpilot.utils.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserUiState(
    val profilePictureId: String? = null,
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val userName: String? = null,
    val success: Boolean = false,
    val userId: String? = null,
    val error: String? = null
)

class UserViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val preferencesManager: PreferencesManager,
    private val notificationScheduler: NotificationScheduler,
) : ViewModel() {

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    UserViewModel(
                        authRepository = container.authRepository,
                        userRepository = container.userRepository,
                        preferencesManager = container.preferencesManager,
                        notificationScheduler = container.notificationScheduler
                    )
                }
            }
    }

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            val loggedIn = authRepository.hasActiveSession()
            if (loggedIn) {
                val user = authRepository.getCurrentUser()
                user?.id?.let { userId ->
                    when (val result = authRepository.getUserData(userId)) {
                        is UiState.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoggedIn = true,
                                    userId = userId,
                                    userName = result.data?.get("username") as? String,
                                    profilePictureId = result.data?.get("profilePictureId") as? String
                                )
                            }
                        }
                        is UiState.Error -> {
                            _uiState.update { it.copy(error = result.message) }
                        }
                        UiState.Idle -> {}
                        UiState.Loading -> {}
                    }
                }
            }
        }
    }

    suspend fun getUsernameById(userId: String): String {
        return userRepository.getUsernameById(userId)
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            performLogin(email, password)
        }
    }

    private suspend fun performLogin(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }
        try {
            authRepository.loginUser(email, password)
            val user = authRepository.getCurrentUser()
            user?.id?.let { userId ->
                val result = authRepository.getUserData(userId)
                when (result) {
                    is UiState.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isLoggedIn = true,
                                userId = userId,
                                userName = result.data?.get("username") as? String,
                                profilePictureId = result.data?.get("profilePictureId") as? String,
                                success = true,
                                error = null
                            )
                        }
                    }
                    is UiState.Error -> {
                        _uiState.update {
                            it.copy(isLoading = false, success = false, error = result.message)
                        }
                    }
                    else -> { _uiState.update { it.copy(isLoading = false) } }
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(isLoading = false, success = false, error = e.message ?: "Login failed")
            }
        }
    }

    fun register(user: RegisterUser) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, success = false) }
            try {
                authRepository.registerUser(user)
                performLogin(user.email, user.password)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        success = false,
                        error = e.message ?: "Registration failed"
                    )
                }
            }
        }
    }

    fun uploadProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = _uiState.value.userId
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "No user logged in") }
                return@launch
            }

            when (val uploadResult = userRepository.uploadProfilePicture(imageUri)) {
                is UiState.Success -> {
                    val fileId = uploadResult.data
                    val oldFileId = _uiState.value.profilePictureId

                    oldFileId?.let { userRepository.deleteProfilePicture(it) }

                    when (val updateResult = userRepository.updateProfilePicture(userId, fileId)) {
                        is UiState.Success -> {
                            _uiState.update {
                                it.copy(isLoading = false, profilePictureId = fileId, error = null)
                            }
                        }
                        is UiState.Error -> {
                            _uiState.update {
                                it.copy(isLoading = false, error = updateResult.message)
                            }
                        }
                        UiState.Idle -> {}
                        UiState.Loading -> {}
                    }
                }
                is UiState.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = uploadResult.message)
                    }
                }
                UiState.Idle -> {}
                UiState.Loading -> {}
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
            it.copy(success = false, error = null, isLoading = false)
        }
    }

    fun updateUsername(newUsername: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = _uiState.value.userId
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "No user logged in") }
                return@launch
            }

            when (val result = userRepository.updateUsername(userId, newUsername)) {
                is UiState.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, userName = newUsername, error = null)
                    }
                }
                is UiState.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                UiState.Idle -> {}
                UiState.Loading -> {}
            }
        }
    }
}
