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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


data class RecipeData(
    val recipeName: String,
    val description: String,
    val steps: String,
    val difficulty: Int,
    val ingredients: List<String>,
    val imageUri: Uri?
)

@Composable
fun RecipeForm(
    onSaveRecipe: (RecipeData) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var difficulty by remember { mutableIntStateOf(1) }
    var showRubricDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )
    val ingredients = remember { mutableStateListOf("") }
    if (ingredients.isEmpty()) ingredients.add("")

    FormBase (
        formTitle = "New recipe",
        buttonText = "Create",
        onConfirmClick = {
            val data = RecipeData(
                recipeName = title,
                description = description,
                steps = steps,
                difficulty = difficulty,
                ingredients = ingredients.filter { it.isNotBlank() },
                imageUri = selectedImageUri
            )
            onSaveRecipe(data)
        },
        modifier = modifier
    ) {
        // ================== 1. IMAGE SELECTOR ==================
        Text(
            text = "Recipe photo:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
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
                        PickVisualMediaRequest(ActivityResultContracts
                            .PickVisualMedia.ImageOnly)
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (selectedImageUri != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // AQUÍ: Si usas la librería 'Coil', usarías AsyncImage(model = selectedImageUri)
                    // Como no sé si la tienes, pongo un icono genérico de "Imagen Cargada"
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
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
            modifier = Modifier.fillMaxWidth().height(100.dp).padding(vertical = 8.dp)
        )

        // ================== INGREDIENTS ==================
        Text(
            text = "Ingredients:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

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
                    modifier = Modifier.weight(1f)
                )

                if (index != ingredients.lastIndex || ingredient.isNotEmpty()) {
                    IconButton(onClick = { ingredients.removeAt(index) }) {
                        Icon(Icons.Default.Clear, contentDescription = "Delete ingredient", tint = Color.Gray)
                    }
                }
            }
        }

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
            modifier = Modifier.fillMaxWidth().height(150.dp).padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ================== DIFFICULTY ==================
        Text(text = "Difficulty (Required):", style = MaterialTheme.typography.titleMedium)

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
    }

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
                    RubricItem(4, "Hard: Lots of ingredients with spices and large cooking time.")
                    RubricItem(5, "CP master: May require days or previous cooking skills/knowledge.")
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

@Composable
fun RubricItem(stars: Int, description: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Text(text = "★".repeat(stars), modifier = Modifier.width(60.dp), color = MaterialTheme.colorScheme.primary)
        Text(text = description, style = MaterialTheme.typography.bodySmall)
    }
}

fun difficultyText(diff: Int): String {
    return when(diff) {
        1 -> "Beginner"
        2 -> "Easy"
        3 -> "Medium"
        4 -> "Hard"
        5 -> "CP master"
        else -> ""
    }
}