package com.example.cookpilot.ui.components.header

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.cookpilot.data.PreferencesManager
import com.example.cookpilot.model.MealPreferences
import com.example.cookpilot.notifications.NotificationScheduler
import com.example.cookpilot.ui.components.CustomDivider
import com.example.cookpilot.ui.theme.CustomColors
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
    val isDarkMode by preferencesManager.isDarkModeFlow.collectAsState(initial = false)
    val notificationScheduler = remember { NotificationScheduler(context) }
    val scope = rememberCoroutineScope()
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
                        },
                        colors = CustomColors.customSwitchColors()
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

        CustomDivider(modifier = Modifier.padding(vertical = 8.dp))

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

        var showAboutUsDialog by remember { mutableStateOf(false) }

        NavigationDrawerItem(
            label = { Text("About us") },
            selected = false,
            onClick = {
                showAboutUsDialog = true
                onOptionSelected("About us")
            },
            icon = { Icon(Icons.Filled.Info, contentDescription = "About us") },
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        if (showAboutUsDialog) {
            AlertDialog(
                onDismissRequest = { showAboutUsDialog = false },
                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                title = { Text("About CookPilot") },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "CookPilot is an academic project developed to help people discover " +
                                    "new recipes with just a few clicks and avoid repeating the same meals over and over.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        CustomDivider()

                        Text(
                            text = "Developed by:",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "• Juan Carlos Rodríguez\n• Dylan Alexander Castrillón",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "Computer Science Engineering Students\nUniversidad de Las Palmas de Gran Canaria",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Version 1.0 • 2025",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Academic Year 2025-2026",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "GitHub: github.com/DylanCas16/CookPilot/tree/main",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://github.com/DylanCas16/CookPilot/tree/main")
                                )
                                context.startActivity(intent)
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showAboutUsDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }


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
