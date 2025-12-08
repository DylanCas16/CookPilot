package com.example.cookpilot.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
    onLogout: () -> Unit
) {
    val uiState by userViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val mealPreferences by preferencesManager.mealPreferencesFlow.collectAsState(initial = MealPreferences())
    val isDarkMode by preferencesManager.isDarkModeFlow.collectAsState(initial = false)  // ← NUEVO
    val notificationScheduler = remember { NotificationScheduler(context) }
    val scope = rememberCoroutineScope()  // ← NUEVO
    var showSettingsDialog by remember { mutableStateOf(false) }

    ModalDrawerSheet {
        Text(
            text = "Main menu",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        NavigationDrawerItem(
            label = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isDarkMode) "Dark Mode" else "Light Mode")
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { enabled ->
                            scope.launch {
                                preferencesManager.setDarkMode(enabled)
                            }
                        }
                    )
                }
            },
            selected = false,
            onClick = {
                scope.launch {
                    preferencesManager.setDarkMode(!isDarkMode)
                }
            },
            icon = {
                Icon(
                    imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                    contentDescription = "Theme toggle"
                )
            },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

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
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            NavigationDrawerItem(
                label = { Text("Logout") },
                selected = false,
                onClick = { onLogout() },
                icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout") },
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }

    if (showSettingsDialog) {
        SettingsDialog(
            currentPreferences = mealPreferences,
            onDismiss = { showSettingsDialog = false },
            onSave = { newPreferences ->
                scope.launch {
                    preferencesManager.saveMealPreferences(newPreferences)
                }
                notificationScheduler.scheduleMealNotifications(newPreferences)
            }
        )
    }
}
