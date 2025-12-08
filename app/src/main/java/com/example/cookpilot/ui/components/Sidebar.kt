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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cookpilot.data.PreferencesManager
import com.example.cookpilot.model.MealPreferences
import com.example.cookpilot.notifications.NotificationScheduler
import com.example.cookpilot.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun Sidebar(
    onOptionSelected: (String) -> Unit,
    userViewModel: UserViewModel,
    drawerState: DrawerState,
    onLogout: () -> Unit
) {
    val uiState by userViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val mealPreferences by preferencesManager.mealPreferencesFlow.collectAsState(initial = MealPreferences())
    val notificationScheduler = remember { NotificationScheduler(context) }

    var showSettingsDialog by remember { mutableStateOf(false) }

    ModalDrawerSheet {
        Text(
            text = "Main menu",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        NavigationDrawerItem(
            label = { Text("Settings") },
            selected = false,
            onClick = {
                showSettingsDialog = true
                onOptionSelected("Settings")
            },
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
                onClick = { onLogout() },
                icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }

    // Settings Dialog
    if (showSettingsDialog) {
        SettingsDialog(
            currentPreferences = mealPreferences,
            onDismiss = { showSettingsDialog = false },
            onSave = { newPreferences ->
                // Guardar preferencias
                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                    preferencesManager.saveMealPreferences(newPreferences)
                }
                // Programar/cancelar notificaciones
                notificationScheduler.scheduleMealNotifications(newPreferences)
            }
        )
    }
}
