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
        Dialog(onDismissRequest = { currentView = null }) {
            UserLoginForm(
                onLoggingUser = {
                    userViewModel.login(it.email, it.password)
                    if (userViewModel.uiState.value.success) {
                        currentView = null
                        showCustomMessage(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            message = "Welcome back! CP chef",
                            actionLabel = "Let me cook",
                            duration = SnackbarDuration.Long
                        )
                    } else {
                        showCustomMessage(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            message = "Something went wrong, try again (error: ${userViewModel.uiState.value.error})",
                            actionLabel = "Alright",
                            duration = SnackbarDuration.Long
                        )
                    }
                },
                onRegisterClick = {
                    currentView = "register"
                }
            )
        }
    }

    if (currentView == "register") {
        Dialog(onDismissRequest = { currentView = null }) {
            UserRegistrationForm(
                onRegisterUser = {
                    try {
                        userViewModel.register(it)
                        if (userViewModel.uiState.value.success) {
                            currentView = null
                            showCustomMessage(
                                scope = scope,
                                snackbarHostState = snackbarHostState,
                                message = "Welcome back! CP chef",
                                actionLabel = "Let me cook",
                                duration = SnackbarDuration.Long
                            )
                        } else {
                            showCustomMessage(
                                scope = scope,
                                snackbarHostState = snackbarHostState,
                                message = "Something went wrong, try again (error: ${userViewModel.uiState.value.error})",
                                actionLabel = "Alright",
                                duration = SnackbarDuration.Long
                            )
                        }
                    } catch (e: Exception) {

                    }
                },
                onLoginClick = {
                    currentView = "login"
                }
            )
        }
    }
}
