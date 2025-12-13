package com.example.cookpilot.ui.components.profile

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun UsernameHeader(
    userName: String?,
    showDialog: Boolean,
    onEditClick: () -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 10.dp)
    ) {
        Text(
            text = userName ?: "Chef CookPilot",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onEditClick) {
            Icon(Icons.Default.Edit, contentDescription = "Edit username")
        }
    }

    if (showDialog) {
        EditUsernameDialog(
            currentUsername = userName ?: "",
            onDismiss = onDismiss,
            onConfirm = onConfirm
        )
    }
}
