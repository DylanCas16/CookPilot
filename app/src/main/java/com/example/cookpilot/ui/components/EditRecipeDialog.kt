package com.example.cookpilot.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cookpilot.model.Recipe

@Composable
fun EditRecipeDialog(
    recipe: Recipe,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        steps: String,
        difficulty: Int,
        ingredients: List<String>,
        cookingTime: Int,
        dietaryTags: List<String>,
        newImageUri: Uri?
    ) -> Unit
) {
    var title by remember { mutableStateOf(recipe.title) }
    var description by remember { mutableStateOf(recipe.description) }
    var steps by remember { mutableStateOf(recipe.steps) }
    var difficulty by remember { mutableIntStateOf(recipe.difficulty) }
    var cookingTimeText by remember { mutableStateOf(recipe.cookingTime.toString()) }
    val ingredients = remember { mutableStateListOf<String>().apply { addAll(recipe.ingredients) } }
    val selectedDietaryTags = remember { mutableStateListOf<String>().apply { addAll(recipe.dietaryTags) } }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Recipe",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, "Close")
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Image Selector
                    Text(
                        text = "Recipe photo:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray.copy(alpha = 0.3f))
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (selectedImageUri != null) Icons.Default.Check else Icons.Default.Add,
                                contentDescription = "Upload photo",
                                modifier = Modifier.size(40.dp),
                                tint = if (selectedImageUri != null) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                            Text(
                                text = if (selectedImageUri != null) "New image selected" else "Change photo (optional)",
                                color = if (selectedImageUri != null) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Recipe name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ingredients
                    Text(
                        text = "Ingredients:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    ingredients.forEachIndexed { index, ingredient ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            OutlinedTextField(
                                value = ingredient,
                                onValueChange = { newValue ->
                                    ingredients[index] = newValue
                                    if (index == ingredients.lastIndex && newValue.isNotEmpty()) {
                                        ingredients.add("")
                                    }
                                },
                                label = { Text("Ingredient ${index + 1}") },
                                modifier = Modifier.weight(1f)
                            )

                            if (index != ingredients.lastIndex || ingredient.isNotEmpty()) {
                                IconButton(onClick = {
                                    ingredients.removeAt(index)
                                    if (ingredients.isEmpty()) ingredients.add("")
                                }) {
                                    Icon(Icons.Default.Clear, "Delete", tint = Color.Gray)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Steps
                    OutlinedTextField(
                        value = steps,
                        onValueChange = { steps = it },
                        label = { Text("Steps") },
                        minLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cooking Time
                    OutlinedTextField(
                        value = cookingTimeText,
                        onValueChange = { new ->
                            if (new.all { it.isDigit() } || new.isEmpty()) {
                                cookingTimeText = new
                            }
                        },
                        label = { Text("Cooking time (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DietaryTagSelector(
                        selectedTags = selectedDietaryTags,
                        onTagToggle = { tag ->
                            if (selectedDietaryTags.contains(tag)) {
                                selectedDietaryTags.remove(tag)
                            } else {
                                selectedDietaryTags.add(tag)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Difficulty
                    Text(
                        text = "Difficulty:",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= difficulty) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Difficulty $i",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clickable { difficulty = i }
                                    .padding(4.dp),
                                tint = if (i <= difficulty) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            onSave(
                                title,
                                description,
                                steps,
                                difficulty,
                                ingredients.filter { it.isNotBlank() },
                                cookingTimeText.toIntOrNull() ?: 0,
                                selectedDietaryTags.toList(),
                                selectedImageUri
                            )
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    }
}
