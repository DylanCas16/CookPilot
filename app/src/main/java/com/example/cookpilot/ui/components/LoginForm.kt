package com.example.cookpilot.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.cookpilot.R

data class LogUser(
    val email: String,
    val password: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLoginForm(
    onLoggingUser: (LogUser) -> Unit,
    modifier: Modifier = Modifier
) {
    var user by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    FormBase(
        formTitle = "LOG IN",
        buttonText = "LOG IN",
        modifier = modifier,
        onConfirmClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                onLoggingUser(LogUser(email, password))
            }
        }
    ) {
        // ================== EMAIL ==================
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // ================== PASSWORD ==================
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    if (passwordVisible) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eye_open),
                            contentDescription = "Hide password"
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_eye_closed),
                            contentDescription = "Show password"
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
    }
}
