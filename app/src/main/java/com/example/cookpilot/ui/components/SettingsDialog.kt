package com.example.cookpilot.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.cookpilot.model.MealPreferences
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(
    currentPreferences: MealPreferences,
    onDismiss: () -> Unit,
    onSave: (MealPreferences) -> Unit
) {
    val context = LocalContext.current
    var notificationsEnabled by remember { mutableStateOf(currentPreferences.notificationsEnabled) }
    var breakfastTime by remember { mutableStateOf(currentPreferences.breakfastTime) }
    var lunchTime by remember { mutableStateOf(currentPreferences.lunchTime) }
    var dinnerTime by remember { mutableStateOf(currentPreferences.dinnerTime) }

    var showBreakfastPicker by remember { mutableStateOf(false) }
    var showLunchPicker by remember { mutableStateOf(false) }
    var showDinnerPicker by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationsEnabled = isGranted
    }

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Meal Notifications")
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Enable reminders",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                val hasPermission = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.POST_NOTIFICATIONS
                                ) == PackageManager.PERMISSION_GRANTED

                                if (hasPermission) notificationsEnabled = true
                                else
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else notificationsEnabled = enabled
                        }
                    )
                }

                if (notificationsEnabled) {
                    HorizontalDivider()

                    // Breakfast
                    MealTimeRow(
                        icon = Icons.Default.WbSunny,
                        label = "Breakfast",
                        time = breakfastTime.format(timeFormatter),
                        onClick = { showBreakfastPicker = true }
                    )

                    // Lunch
                    MealTimeRow(
                        icon = Icons.Default.LunchDining,
                        label = "Lunch",
                        time = lunchTime.format(timeFormatter),
                        onClick = { showLunchPicker = true }
                    )

                    // Dinner
                    MealTimeRow(
                        icon = Icons.Default.DinnerDining,
                        label = "Dinner",
                        time = dinnerTime.format(timeFormatter),
                        onClick = { showDinnerPicker = true }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newPreferences = MealPreferences(
                        breakfastTime = breakfastTime,
                        lunchTime = lunchTime,
                        dinnerTime = dinnerTime,
                        notificationsEnabled = notificationsEnabled
                    )
                    onSave(newPreferences)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Time Pickers
    if (showBreakfastPicker) {
        TimePickerDialog(
            initialTime = breakfastTime,
            onDismiss = { showBreakfastPicker = false },
            onConfirm = { time ->
                breakfastTime = time
                showBreakfastPicker = false
            }
        )
    }

    if (showLunchPicker) {
        TimePickerDialog(
            initialTime = lunchTime,
            onDismiss = { showLunchPicker = false },
            onConfirm = { time ->
                lunchTime = time
                showLunchPicker = false
            }
        )
    }

    if (showDinnerPicker) {
        TimePickerDialog(
            initialTime = dinnerTime,
            onDismiss = { showDinnerPicker = false },
            onConfirm = { time ->
                dinnerTime = time
                showDinnerPicker = false
            }
        )
    }
}

@Composable
private fun MealTimeRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    time: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }

        OutlinedButton(onClick = onClick) {
            Text(time)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedTime = LocalTime.of(
                    timePickerState.hour,
                    timePickerState.minute
                )
                onConfirm(selectedTime)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
