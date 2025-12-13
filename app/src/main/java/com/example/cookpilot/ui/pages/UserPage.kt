package com.example.cookpilot.ui.pages

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.CustomDivider
import com.example.cookpilot.ui.components.auth.LogInMessage
import com.example.cookpilot.ui.components.profile.ProfilePictureSection
import com.example.cookpilot.ui.components.profile.UserRecipesSection
import com.example.cookpilot.ui.components.profile.UsernameHeader
import com.example.cookpilot.ui.components.recipe.EditRecipeDialog
import com.example.cookpilot.ui.components.recipe.RecipeAction
import com.example.cookpilot.ui.components.recipe.RecipeDetailDialog
import com.example.cookpilot.ui.components.showCustomMessage
import com.example.cookpilot.ui.theme.CustomColors
import com.example.cookpilot.ui.utils.createImageUri
import com.example.cookpilot.viewmodel.RecipeViewModel
import com.example.cookpilot.viewmodel.UserViewModel

@Composable
fun UserPage(
    recipeViewModel: RecipeViewModel,
    userViewModel: UserViewModel,
    onGoToAuthMenu: () -> Unit
) {
    val uiState by userViewModel.uiState.collectAsState()
    val userRecipes by recipeViewModel.userRecipes.collectAsState()

    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    val isCameraEnabled by preferencesManager.isCameraEnabledFlow.collectAsState(initial = true)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // UI state (screen orchestration)
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var recipeToEdit by remember { mutableStateOf<Recipe?>(null) }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

    var showEditUsernameDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var showCameraDisabledDialog by remember { mutableStateOf(false) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Launchers
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempPhotoUri?.let(userViewModel::uploadProfilePicture)
            }
            tempPhotoUri = null
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

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let(userViewModel::uploadProfilePicture)
        }
    )

    LaunchedEffect(uiState.userId) {
        uiState.userId?.let(recipeViewModel::loadUserRecipes)
    }

    val userRecipeActions: (Recipe) -> List<RecipeAction> = { recipe ->
        listOf(
            RecipeAction("Edit") { recipeToEdit = recipe },
            RecipeAction("Delete") { recipeToDelete = recipe }
        )
    }

    if (!uiState.isLoggedIn) {
        LogInMessage(
            onGoToAuthMenu = onGoToAuthMenu,
            text = "You must be Logged in to view your profile"
        )
        return
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                UsernameHeader(
                    userName = uiState.userName,
                    showDialog = showEditUsernameDialog,
                    onEditClick = { showEditUsernameDialog = true },
                    onDismiss = { showEditUsernameDialog = false },
                    onConfirm = { newUsername ->
                        userViewModel.updateUsername(newUsername)
                        showEditUsernameDialog = false
                    }
                )

                ProfilePictureSection(
                    isLoading = uiState.isLoading,
                    profilePictureId = uiState.profilePictureId,
                    onClick = { showImageSourceDialog = true }
                )

                CustomDivider()

                UserRecipesSection(
                    recipes = userRecipes,
                    onRecipeClick = { selectedRecipe = it }
                )
            }
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Select Image Source") },
            text = { Text("How would you like to set your profile picture?") },
            confirmButton = {
                Button(
                    onClick = {
                        showImageSourceDialog = false
                        if (!isCameraEnabled) {
                            showCameraDisabledDialog = true
                            return@Button
                        }
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    colors = CustomColors.customPrimaryButtonColor()
                ) { Text("Take Photo") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showImageSourceDialog = false
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = CustomColors.customSecondaryButtonColor()
                ) { Text("Select from Gallery") }
            }
        )
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Camera Permission Required") },
            text = { Text("Camera access is needed to take photos. Please grant permission in app settings.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                }) { Text("Open Settings") }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showCameraDisabledDialog) {
        AlertDialog(
            onDismissRequest = { showCameraDisabledDialog = false },
            title = { Text("Camera Disabled") },
            text = { Text("Camera access is disabled in settings. Enable it to take photos.") },
            confirmButton = {
                TextButton(onClick = { showCameraDisabledDialog = false }) { Text("OK") }
            }
        )
    }

    selectedRecipe?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            actions = userRecipeActions(recipe),
            onDismiss = { selectedRecipe = null }
        )
    }

    recipeToEdit?.let { recipe ->
        EditRecipeDialog(
            scope = scope,
            snackbarHostState = snackbarHostState,
            recipe = recipe,
            onDismiss = { recipeToEdit = null },
            onSave = { title, description, steps, difficulty, ingredients, cookingTime, dietaryTags, newImageUri ->
                val userId = uiState.userId ?: return@EditRecipeDialog
                val recipeId = recipe.id ?: return@EditRecipeDialog

                recipeViewModel.updateRecipe(
                    recipeId = recipeId,
                    title = title,
                    description = description,
                    steps = steps,
                    difficulty = difficulty,
                    ingredients = ingredients,
                    cookingTime = cookingTime,
                    creator = userId,
                    dietaryTags = dietaryTags,
                    newImageUri = newImageUri,
                    onSuccess = {
                        recipeToEdit = null
                        showCustomMessage(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            message = "Recipe updated successfully!",
                            actionLabel = "Great",
                            duration = SnackbarDuration.Long
                        )
                    },
                    onError = { errorMessage ->
                        showCustomMessage(
                            scope = scope,
                            snackbarHostState = snackbarHostState,
                            message = "Update failed: $errorMessage",
                            actionLabel = "Try again",
                            duration = SnackbarDuration.Long
                        )
                    }
                )
            }
        )
    }

    recipeToDelete?.let { recipe ->
        AlertDialog(
            onDismissRequest = { recipeToDelete = null },
            title = { Text("Delete Recipe") },
            text = { Text("Are you sure you want to delete \"${recipe.title}\"? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        val userId = uiState.userId ?: return@Button
                        val recipeId = recipe.id ?: return@Button

                        recipeViewModel.deleteRecipe(
                            recipeId = recipeId,
                            creator = userId,
                            onSuccess = {
                                recipeToDelete = null
                                showCustomMessage(
                                    scope = scope,
                                    snackbarHostState = snackbarHostState,
                                    message = "Recipe deleted correctly",
                                    actionLabel = "Understood",
                                    duration = SnackbarDuration.Long
                                )
                            },
                            onError = { errorMessage ->
                                recipeToDelete = null
                                showCustomMessage(
                                    scope = scope,
                                    snackbarHostState = snackbarHostState,
                                    message = "Delete failed: $errorMessage",
                                    actionLabel = "Try again",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        )
                    },
                    colors = CustomColors.customSecondaryButtonColor()
                ) { Text("Delete") }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { recipeToDelete = null },
                    colors = CustomColors.customPrimaryButtonColor()
                ) { Text("Cancel") }
            }
        )
    }
}
