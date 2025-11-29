package com.example.cookpilot.ui.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AuthMenu(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Welcome to CookPilot", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(24.dp))

                Button(onClick = onLoginClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Log in")
                }
                Spacer(Modifier.height(12.dp))
                OutlinedButton(onClick = onRegisterClick, modifier = Modifier.fillMaxWidth()) {
                    Text("Register")
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
