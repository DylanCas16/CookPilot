package com.example.cookpilot.ui.components.header

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DinnerDining
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.cookpilot.model.MealPreferences
import com.example.cookpilot.ui.components.CustomDivider
import com.example.cookpilot.ui.theme.CustomColors
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
                        },
                        colors = CustomColors.customSwitchColors()
                    )
                }

                if (notificationsEnabled) {
                    CustomDivider()

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
            OutlinedButton(
                onClick = {
                    val newPreferences = MealPreferences(
                        breakfastTime = breakfastTime,
                        lunchTime = lunchTime,
                        dinnerTime = dinnerTime,
                        notificationsEnabled = notificationsEnabled
                    )
                    onSave(newPreferences)
                    onDismiss()
                },
                colors = CustomColors.customSecondaryButtonColor()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = CustomColors.customPrimaryButtonColor())
            { Text("Cancel") }
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
    icon: ImageVector,
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
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }

        OutlinedButton(onClick = onClick, colors = CustomColors.customPrimaryButtonColor()) {
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
            TimePicker(state = timePickerState, colors = CustomColors.customTimePickerColors())
        },
        confirmButton = {
            TextButton(onClick = {
                val selectedTime = LocalTime.of(
                    timePickerState.hour,
                    timePickerState.minute
                )
                onConfirm(selectedTime)
                                 },
                colors = CustomColors.customSecondaryButtonColor()
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss,
                colors = CustomColors.customPrimaryButtonColor()
            ) { Text("Cancel") }
        }
    )
}
