// Sidebar.kt - CORREGIDO
package com.example.cookpilot.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cookpilot.viewmodel.UserViewModel

@Composable
fun Sidebar(
    onOptionSelected: (String) -> Unit,
    userViewModel: UserViewModel,
    drawerState: DrawerState,
    onLogout: () -> Unit
) {
    val uiState by userViewModel.uiState.collectAsState()

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
            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        NavigationDrawerItem(
            label = { Text("About us") },
            selected = false,
            onClick = { onOptionSelected("About us") },
            icon = { Icon(Icons.Filled.Info, contentDescription = "About us") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        if (uiState.isLoggedIn) {
            NavigationDrawerItem(
                label = { Text("Logout") },
                selected = false,
                onClick = {
                    onLogout()
                },
                icon = {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout"
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}
