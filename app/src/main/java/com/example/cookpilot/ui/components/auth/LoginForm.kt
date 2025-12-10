package com.example.cookpilot.ui.components.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.cookpilot.R
import com.example.cookpilot.ui.components.FormBase
import com.example.cookpilot.ui.components.showCustomMessage
import com.example.cookpilot.ui.theme.CustomColors

data class LogUser(
    val email: String,
    val password: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserLoginForm(
    onLoggingUser: (LogUser) -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val registerText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.onSurface
            )
        ) {
            append("Don't have an account? ")
        }

        pushStringAnnotation(
            tag = "register",
            annotation = "register_route"
        )
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Register")
        }
        pop()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    FormBase(
        formTitle = "LOG IN",
        buttonText = "LOG IN",
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        onConfirmClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                onLoggingUser(LogUser(email, password))
            } else {
                showCustomMessage(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = "Fill all fields, please",
                    actionLabel = "I got it",
                    duration = SnackbarDuration.Long
                )
            }
        }
    ) {
        // ================== EMAIL ==================
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CustomColors.customTextFieldColors()
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CustomColors.customTextFieldColors()
        )

        // ================== REGISTER LINK ==================
        ClickableText(
            text = registerText,
            onClick = { offset ->
                registerText.getStringAnnotations(
                    tag = "register",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    onRegisterClick()
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }
}
