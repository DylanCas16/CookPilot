package com.example.cookpilot.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LateralMenu(onOptionSelected: (String) -> Unit) {
    ModalDrawerSheet {
        Text(
            text = "Main menu",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = false,
            onClick = { onOptionSelected("Settings") },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Ajustes") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            label = { Text("About us") },
            selected = false,
            onClick = { onOptionSelected("About us") },
            icon = { Icon(Icons.Filled.Info, contentDescription = "About us") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}