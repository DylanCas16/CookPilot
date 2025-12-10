package com.example.cookpilot.ui.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cookpilot.ui.components.showCustomMessage
import com.example.cookpilot.ui.theme.CustomColors
import com.example.cookpilot.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun AuthMenu(
    onDismiss: () -> Unit,
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    userViewModel: UserViewModel
) {
    var currentView: String? by remember { mutableStateOf(null) }
    val uiState by userViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.success, uiState.error, uiState.isLoading) {
        if (currentView != null && !uiState.isLoading) {
            if (uiState.success) {
                currentView = null
                onDismiss()
                showCustomMessage(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = "Welcome back! CP chef",
                    actionLabel = "Let me cook",
                    duration = SnackbarDuration.Long
                )
                userViewModel.clearAuthStatus()
            } else if (uiState.error != null) {
                showCustomMessage(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = "Something went wrong, try again (error: ${uiState.error})",
                    actionLabel = "Alright",
                    duration = SnackbarDuration.Long
                )
                userViewModel.clearAuthStatus()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            colors = CustomColors.customCardColors()) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Welcome to CookPilot", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { currentView = "login" },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CustomColors.customPrimaryButtonColor()
                ) {
                    Text("Log in")
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { currentView = "register" },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CustomColors.customSecondaryButtonColor()
                ) {
                    Text("Register")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (currentView == "login") {
        Dialog(onDismissRequest = { currentView = null; userViewModel.clearAuthStatus() }) {
            UserLoginForm(
                onLoggingUser = {
                    userViewModel.login(it.email, it.password)
                },
                onRegisterClick = {
                    currentView = "register"
                }
            )
        }
    }

    if (currentView == "register") {
        Dialog(onDismissRequest = { currentView = null; userViewModel.clearAuthStatus() }) {
            UserRegistrationForm(
                onRegisterUser = {
                    try {
                        userViewModel.register(it)
                    } catch (e: Exception) {
                        showCustomMessage(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            message = "Client Error: ${e.message}",
                            actionLabel = "Ouch",
                            duration = SnackbarDuration.Long
                        )
                    }
                },
                onLoginClick = {
                    currentView = "login"
                }
            )
        }
    }
}