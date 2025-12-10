package com.example.cookpilot.ui.components.auth

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import com.example.cookpilot.viewmodel.UserUiState

@Composable
fun LoginDialog(
    uiState: UserUiState,
    onLogin: (String, String) -> Unit,
    onDismiss: () -> Unit,
    onRegisterClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { },
        text = {
            UserLoginForm(
                onLoggingUser = { logUser ->
                    onLogin(logUser.email, logUser.password)
                },
                onRegisterClick = onRegisterClick            )
        }
    )
}
