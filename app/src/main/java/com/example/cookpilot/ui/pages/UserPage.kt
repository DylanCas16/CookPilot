package com.example.cookpilot.ui.pages

import APPWRITE_BUCKET_ID
import APPWRITE_PROJECT_ID
import APPWRITE_PUBLIC_ENDPOINT
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.EditRecipeDialog
import com.example.cookpilot.ui.components.RecipeAction
import com.example.cookpilot.ui.components.RecipeDetailDialog
import com.example.cookpilot.ui.components.RecipeList
import com.example.cookpilot.viewmodel.RecipeViewModel
import com.example.cookpilot.viewmodel.UserViewModel

@Composable
fun buildProfileImageUrl(fileId: String?, bucketId: String = APPWRITE_BUCKET_ID): String? {
    if (fileId == null) return null
    val endpoint = APPWRITE_PUBLIC_ENDPOINT
    val projectId = APPWRITE_PROJECT_ID
    return "$endpoint/storage/buckets/$bucketId/files/$fileId/view?project=$projectId"
}

@Composable
fun UserPage(
    recipeViewModel: RecipeViewModel,
    userViewModel: UserViewModel,
) {
    val uiState by userViewModel.uiState.collectAsState()
    val userRecipes by recipeViewModel.userRecipes.collectAsState()
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var recipeToEdit by remember { mutableStateOf<Recipe?>(null) }
    var recipeToDelete by remember { mutableStateOf<Recipe?>(null) }

    LaunchedEffect(uiState.userId) {
        uiState.userId?.let { userId ->
            recipeViewModel.loadUserRecipes(userId)
        }
    }

    val userRecipeActions: (Recipe) -> List<RecipeAction> = { recipe ->
        listOf(
            RecipeAction("Edit") {
                recipeToEdit = recipe
            },
            RecipeAction("Delete") {
                recipeToDelete = recipe
            }
        )
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                println("🔵 Image selected: $uri")
                userViewModel.uploadProfilePicture(it)
            }
        }
    )

    // --- INTERFACE ---
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
                ) {
                    Text(
                        text = uiState.userName ?: "Chef CookPilot",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .background(Color.LightGray)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        val profileImageUrl = buildProfileImageUrl(uiState.profilePictureId)

                        if (profileImageUrl != null) {
                            AsyncImage(
                                model = profileImageUrl,
                                contentDescription = "Profile picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default profile picture",
                                modifier = Modifier.size(60.dp),
                                tint = Color.White
                            )
                        }

                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Text(
                        text = "Change profile picture",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                HorizontalDivider()

                Text(
                    text = "My Recipes (${userRecipes.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }

            item {
                if (userRecipes.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You haven't created any recipes yet",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Go to Create tab to start!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    RecipeList(
                        recipes = userRecipes,
                        onRecipeClick = { recipe ->
                            selectedRecipe = recipe
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    )
                }
            }
        }
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
            recipe = recipe,
            onDismiss = { recipeToEdit = null },
            onSave = { title, description, steps, difficulty, ingredients, cookingTime, dietaryTags, newImageUri ->
                uiState.userId?.let { userId ->
                    recipeViewModel.updateRecipe(
                        recipeId = recipe.id ?: return@let,
                        title = title,
                        description = description,
                        steps = steps,
                        difficulty = difficulty,
                        ingredients = ingredients,
                        cookingTime = cookingTime,
                        creator = userId,
                        dietaryTags = dietaryTags,
                        newImageUri = newImageUri
                    )
                }
            }
        )
    }

    recipeToDelete?.let { recipe ->
        AlertDialog(
            onDismissRequest = { recipeToDelete = null },
            title = { Text("Delete Recipe") },
            text = {
                Text("Are you sure you want to delete \"${recipe.title}\"? This action cannot be undone.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        uiState.userId?.let { userId ->
                            recipeViewModel.deleteRecipe(
                                recipeId = recipe.id ?: return@let,
                                creator = userId
                            )
                        }
                        recipeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { recipeToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
