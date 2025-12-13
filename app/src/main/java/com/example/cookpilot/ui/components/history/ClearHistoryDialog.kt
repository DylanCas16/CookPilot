package com.example.cookpilot.ui.components.history

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.cookpilot.ui.theme.CustomColors

@Composable
fun ClearHistoryDialog(
    onDismiss: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Clear History") },
        text = { Text("Are you sure you want to delete all your history? This action cannot be undone.") },
        confirmButton = {
            OutlinedButton(
                onClick = onConfirmDelete,
                colors = CustomColors.customSecondaryButtonColor()
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = CustomColors.customPrimaryButtonColor()
            ) {
                Text("Cancel")
            }
        }
    )
}
