package com.example.cookpilot.ui.components.recipe

import android.R
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.FormBase
import com.example.cookpilot.ui.components.showMessage


@Composable
fun RecipeForm(
    modifier: Modifier = Modifier,
    onSaveRecipe: (Recipe, Uri?) -> Unit = { _, _ -> }
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var difficulty by remember { mutableIntStateOf(1) }
    var showRubricDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var cookingTimeText by remember { mutableStateOf("") }
    val selectedDietaryTags = remember { mutableStateListOf<String>() }
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )
    val ingredients = remember { mutableStateListOf("") }
    if (ingredients.isEmpty()) ingredients.add("")

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val resetFormStates = {
        title = ""
        description = ""
        steps = ""
        difficulty = 1
        cookingTimeText = ""
        selectedImageUri = null
        ingredients.clear()
        ingredients.add("")
        selectedDietaryTags.clear()
    }

    FormBase(
        formTitle = "New recipe",
        buttonText = "Create",
        onConfirmClick = {
            if (title.isEmpty() || ingredients.isEmpty()) {
                showMessage(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = "Please fill in all required fields.",
                    actionLabel = "Sorry",
                    duration = SnackbarDuration.Short
                )
            } else {
                val data = Recipe(
                    title = title,
                    description = description,
                    steps = steps,
                    difficulty = difficulty,
                    ingredients = ingredients.filter { it.isNotBlank() },
                    cookingTime = cookingTimeText.toIntOrNull() ?: 0,
                    creator = "anon",
                    fileId = null,
                    dietaryTags = selectedDietaryTags.toList()
                )
                onSaveRecipe(data, selectedImageUri)
                showMessage(
                    scope = scope,
                    snackbarHostState = snackbarHostState,
                    message = "Recipe created successfully!",
                    actionLabel = "Perfect",
                    duration = SnackbarDuration.Long
                )
                resetFormStates()
            }
        },
        modifier = modifier
    ) {

        // ================== 1. IMAGE SELECTOR ==================
        Text(
            text = "Recipe photo:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray.copy(alpha = 0.3f))
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .clickable {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_menu_gallery),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text("Image selected", style = MaterialTheme.typography.bodySmall)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload photo",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Gray
                    )
                    Text("Select a photo", color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================== TITLE ==================
        Text(
            text = "Recipe name:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Required") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // ================== DESCRIPTION ==================
        Text(
            text = "Description:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("About the recipe") },
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(vertical = 8.dp)
        )

        // ================== INGREDIENTS ==================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ingredients:",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.width(8.dp))

            var showInfoDialog by remember { mutableStateOf(false) }

            IconButton(
                onClick = { showInfoDialog = true },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Ingredients help",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            if (showInfoDialog) {
                AlertDialog(
                    onDismissRequest = { showInfoDialog = false },
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    title = { Text("Ingredient Guidelines") },
                    text = {
                        Column {
                            Text("Always use singular form for consistency. Measurements must be described in steps dialog.")
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "✅ Correct:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Text("• tomato\n• egg\n• rice\n• garlic")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "❌ Incorrect:",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF44336)
                            )
                            Text("• tomatoes\n• eggs\n• cup of rice\n• garlic clove")
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showInfoDialog = false }) {
                            Text("Understood")
                        }
                    }
                )
            }
        }

        Text(
            text = "Required at least one, use singular form (e.g., \"tomato\" not \"tomatoes\")",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val exampleIngredients = listOf("tomato", "onion", "egg", "garlic", "chicken")

        ingredients.forEachIndexed { index, ingredient ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
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
                    placeholder = {
                        Text(
                            text = "e.g., ${exampleIngredients[index % exampleIngredients.size]}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    modifier = Modifier.weight(1f)
                )

                if (index != ingredients.lastIndex || ingredient.isNotEmpty()) {
                    IconButton(onClick = {
                        ingredients.removeAt(index)
                        if (ingredients.isEmpty()) ingredients.add("")
                    }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Delete ingredient",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ================== STEPS ==================
        Text(
            text = "Steps:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = steps,
            onValueChange = { steps = it },
            label = { Text("Recipe steps") },
            minLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ================== DIETARY TAGS ==================
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

        // ================== COOKING TIME ==================
        Text(
            text = "Cooking time:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        OutlinedTextField(
            value = cookingTimeText,
            onValueChange = { new ->
                if (new.all { it.isDigit() } || new.isEmpty()) {
                    cookingTimeText = new
                }
            },
            label = { Text("Minutes") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ================== DIFFICULTY ==================
        Text(
            text = "Difficulty:",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = { showRubricDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Show rubric",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Text(
            text = difficultyText(difficulty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // ================== RUBRIC DIALOGUE ==================
        if (showRubricDialog) {
            AlertDialog(
                onDismissRequest = { showRubricDialog = false },
                title = { Text("Difficulty guide") },
                text = {
                    Column {
                        RubricItem(1, "Beginner: Simple steps without cooking time.")
                        RubricItem(2, "Easy: Few ingredients and cooking time.")
                        RubricItem(3, "Medium: Steps more elaborated.")
                        RubricItem(4, "Hard: Lots of ingredients and long cooking time.")
                        RubricItem(5, "CP master: May require days or advanced skills.")
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showRubricDialog = false }) {
                        Text("Understood")
                    }
                }
            )
        }
    }
}

@Composable
fun RubricItem(stars: Int, description: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "★".repeat(stars),
            modifier = Modifier.width(60.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun difficultyText(diff: Int): String =
    when (diff) {
        1 -> "Beginner"
        2 -> "Easy"
        3 -> "Medium"
        4 -> "Hard"
        5 -> "CP master"
        else -> ""
    }
