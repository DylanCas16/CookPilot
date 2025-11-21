package com.example.cookpilot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import com.example.cookpilot.R

data class User(
    val user: String,
    val birthdate: Long,
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
    onRegisterUser: (User) -> Unit,
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

    FormBase(
        formTitle = "SIGN UP",
        buttonText = "REGISTER",
        modifier = modifier,
        onConfirmClick = {
            if (user.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword == password && birthdateMillis != null) {
                onRegisterUser(User(user, birthdateMillis!!, email, password))
            }
        }
    ) {
        // ================== USERNAME ==================
        OutlinedTextField(
            value = user,
            onValueChange = { user = it },
            label = { Text("Username") },
            trailingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
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
            }
        )

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

        // ================== CONFIRM PASSWORD ==================
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )
    }

    // ================== CALENDAR ==================
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthdateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}