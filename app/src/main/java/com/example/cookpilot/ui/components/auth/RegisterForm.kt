package com.example.cookpilot.ui.components.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class RegisterUser(
    val user: String,
    val birthdate: Long? = null,
    val email: String,
    val password: String
)

fun Long.toDate(): String {
    val date = Instant.ofEpochMilli(this).atZone(ZoneId.of("UTC")).toLocalDate()
    return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegistrationForm(
    onRegisterUser: (RegisterUser) -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var user by remember { mutableStateOf("") }
    var birthdateMillis by remember { mutableStateOf<Long?>(null) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val loginText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.onSurface
            )
        ) {
            append("Already have an account? ")
        }

        pushStringAnnotation(
            tag = "login",
            annotation = "login_route"
        )
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Log in")
        }
        pop()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun isValid(): Boolean =
        user.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() &&
                confirmPassword == password && birthdateMillis != null

    FormBase(
        formTitle = "SIGN UP",
        buttonText = "REGISTER",
        modifier = modifier,
        snackbarHostState = snackbarHostState,
        color = MaterialTheme.colorScheme.surface.copy(1f),
        onConfirmClick = {
            if (isValid()) onRegisterUser(RegisterUser(user, birthdateMillis!!, email, password))
            else showCustomMessage(
                scope = scope,
                snackbarHostState = snackbarHostState,
                message = "Fill, all fields please",
                actionLabel = "I got it",
                duration = SnackbarDuration.Long
            )
        }
    ) {
        // ================== USERNAME ==================
        OutlinedTextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("Username") },
            trailingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CustomColors.customTextFieldColors()
        )

        // ================== BIRTHDATE ==================
        OutlinedTextField(
            value = birthdateMillis?.toDate() ?: "",
            onValueChange = {},
            label = { Text("Birthdate") },
            readOnly = true,

            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),

            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Open date picker")
                }
            },
            colors = CustomColors.customTextFieldColors()
        )

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
        val isPasswordValid = password.length >= 8

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(
                            id = if (passwordVisible) R.drawable.ic_eye_closed else R.drawable.ic_eye_open
                        ),
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            isError = !isPasswordValid && password.isNotEmpty(),
            supportingText = {
                if (password.isEmpty()) {
                    Text("At least 8 characters")
                } else if (!isPasswordValid) {
                    Text("Password must be 8 characters at least.", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CustomColors.customTextFieldColors()
        )


        // ================== CONFIRM PASSWORD ==================
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            colors = CustomColors.customTextFieldColors()
        )

        ClickableText(
            text = loginText,
            onClick = { offset ->
                loginText.getStringAnnotations(
                    tag = "login",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    onLoginClick()
                }
            },
            modifier = Modifier.padding(16.dp)
        )
    }

    // ================== CALENDAR PICKER ==================
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthdateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                                     },
                    colors = CustomColors.customSecondaryButtonColor()
                    ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = CustomColors.customSecondaryButtonColor()
                ) { Text("Cancel") }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = CustomColors.customCalendarFieldsColors()
            )
        }
    }
}
