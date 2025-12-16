package com.example.cookpilot.ui.components.recipe

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.cookpilot.data.PreferencesManager
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.FormBase
import com.example.cookpilot.ui.components.showCustomMessage
import com.example.cookpilot.ui.theme.CustomColors
import com.example.cookpilot.utils.DIFFICULTY_BEGINNER
import com.example.cookpilot.utils.DIFFICULTY_EASY
import com.example.cookpilot.utils.DIFFICULTY_HARD
import com.example.cookpilot.utils.DIFFICULTY_MASTER
import com.example.cookpilot.utils.DIFFICULTY_MEDIUM
import com.example.cookpilot.utils.MAX_DIFFICULTY
import com.example.cookpilot.utils.MIN_DIFFICULTY
import com.example.cookpilot.utils.PermissionUtils
import java.io.File

@Composable
fun RecipeForm(
    modifier: Modifier = Modifier,
    onSaveRecipe: (Recipe, Uri?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val isCameraEnabled by preferencesManager.isCameraEnabledFlow.collectAsState(initial = true)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf("") }
    var difficulty by remember { mutableIntStateOf(1) }
    var showRubricDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var cookingTimeText by remember { mutableStateOf("") }
    val selectedDietaryTags = remember { mutableStateListOf<String>() }

    var showPermissionDialog by remember { mutableStateOf(false) }
    var showCameraDisabledDialog by remember { mutableStateOf(false) }

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImageUri = uri }
    )

    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) selectedImageUri = tempPhotoUri else tempPhotoUri = null
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val uri = context.createImageUri()
                tempPhotoUri = uri
                cameraLauncher.launch(uri)
            } else {
                showPermissionDialog = true
            }
        }
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
        modifier = modifier,
        formTitle = "New recipe",
        buttonText = "Create",
        snackbarHostState = snackbarHostState,
        onConfirmClick = {
            if (title.isBlank() || ingredients.isEmpty()) {
                showCustomMessage(
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
                resetFormStates()
            }
        },
    ) {
        Text(
            text = "Recipe photo:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    Text("Image selected", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    singlePhotoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.weight(1f),
                colors = CustomColors.customPrimaryButtonColor()
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_gallery),
                    contentDescription = "Gallery icon"
                )
                Spacer(Modifier.width(4.dp))
                Text("Go Gallery")
            }

            Button(
                onClick = {
                    if (!isCameraEnabled) {
                        showCameraDisabledDialog = true
                    } else if (PermissionUtils.hasCameraPermission(context)) {
                        val uri = context.createImageUri()
                        tempPhotoUri = uri
                        cameraLauncher.launch(uri)
                    } else permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.weight(1f),
                colors = CustomColors.customSecondaryButtonColor()
            ) {
                Icon(
                    painter = painterResource(android.R.drawable.ic_menu_camera),
                    contentDescription = "Take Photo"
                )
                Spacer(Modifier.width(4.dp))
                Text("Take Photo")
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                .padding(vertical = 8.dp),
            colors = CustomColors.customTextFieldColors()
        )

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
                .padding(vertical = 8.dp),
            colors = CustomColors.customTextFieldColors()
        )

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
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp),
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
                        TextButton(
                            onClick = { showInfoDialog = false },
                            colors = CustomColors.customSecondaryButtonColor()
                        ) {
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
                    value = ingredient.lowercase(),
                    onValueChange = { newValue ->
                        ingredients[index] = newValue.lowercase()
                        if (index == ingredients.lastIndex && newValue.isNotEmpty())
                            ingredients.add("")
                    },
                    label = { Text("Ingredient ${index + 1}") },
                    placeholder = {
                        Text(
                            text = "e.g., ${exampleIngredients[index % exampleIngredients.size]}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = CustomColors.customTextFieldColors()
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
                .padding(vertical = 8.dp),
            colors = CustomColors.customTextFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        DietaryTagSelector(
            selectedTags = selectedDietaryTags,
            onTagToggle = { tag ->
                if (selectedDietaryTags.contains(tag)) selectedDietaryTags.remove(tag) else selectedDietaryTags.add(tag)
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
                .padding(vertical = 8.dp),
            colors = CustomColors.customTextFieldColors()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Difficulty:",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            for (i in MIN_DIFFICULTY..MAX_DIFFICULTY) {
                Icon(
                    imageVector = if (i <= difficulty) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Difficulty $i",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { difficulty = i }
                        .padding(4.dp),
                    tint = if (i <= difficulty) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        if (showRubricDialog) {
            AlertDialog(
                onDismissRequest = { showRubricDialog = false },
                title = { Text("Difficulty guide") },
                text = {
                    Column {
                        RubricItem(DIFFICULTY_BEGINNER, "Beginner: Simple steps without cooking time.")
                        RubricItem(DIFFICULTY_EASY, "Easy: Few ingredients and cooking time.")
                        RubricItem(DIFFICULTY_MEDIUM, "Medium: Steps more elaborated.")
                        RubricItem(DIFFICULTY_HARD, "Hard: Lots of ingredients and long cooking time.")
                        RubricItem(DIFFICULTY_MASTER, "CP master: May require days or advanced skills.")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showRubricDialog = false },
                        colors = CustomColors.customSecondaryButtonColor()
                    ) {
                        Text("Understood")
                    }
                }
            )
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Camera Permission Required") },
            text = {
                Text("Camera access is needed to take photos. Please grant permission in app settings.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCameraDisabledDialog) {
        AlertDialog(
            onDismissRequest = { showCameraDisabledDialog = false },
            title = { Text("Camera Disabled") },
            text = {
                Text("Camera access is disabled in settings. Enable it to take photos.")
            },
            confirmButton = {
                TextButton(onClick = { showCameraDisabledDialog = false }) {
                    Text("OK")
                }
            }
        )
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
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

fun difficultyText(diff: Int): String =
    when (diff) {
        DIFFICULTY_BEGINNER -> "Beginner"
        DIFFICULTY_EASY -> "Easy"
        DIFFICULTY_MEDIUM -> "Medium"
        DIFFICULTY_HARD -> "Hard"
        DIFFICULTY_MASTER -> "CP master"
        else -> ""
    }

fun Context.createImageUri(): Uri {
    val tempImagesDir = File(cacheDir, "images")
    tempImagesDir.mkdirs()
    val file = File(tempImagesDir, "temp_photo_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(
        this,
        "${packageName}.provider",
        file
    )
}
