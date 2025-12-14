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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cookpilot.ui.theme.CustomColors
import com.example.cookpilot.utils.ErrorType
import com.example.cookpilot.utils.UiState
import com.example.cookpilot.viewmodel.UserViewModel

@Composable
fun AuthMenu(
    onDismiss: () -> Unit,
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

                OutlinedButton(
                    onClick = { currentView = "login" },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CustomColors.customPrimaryButtonColor()
                ) {
                    Text("Log in")
                }
                Spacer(Modifier.height(12.dp))
                Button(
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
                        UiState.Error("Could not register: ${e.message}", ErrorType.AUTHENTICATION)
                    }
                },
                onLoginClick = {
                    currentView = "login"
                }
            )
        }
    }
}