package com.example.cookpilot.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import com.example.cookpilot.viewmodel.UserUiState

@Composable
fun LoginDialog(
    uiState: UserUiState,
    onLogin: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { }, // vacío, el botón ya está en UserLoginForm
        text = {
            UserLoginForm(
                onLoggingUser = { logUser ->
                    onLogin(logUser.email, logUser.password)
                }
            )
        }
    )
}
