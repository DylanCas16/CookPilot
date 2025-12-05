// UserPage.kt
package com.example.cookpilot.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.RecipeAction
import com.example.cookpilot.ui.components.RecipeDetailDialog
import com.example.cookpilot.ui.components.RecipeList
import com.example.cookpilot.viewmodel.RecipeViewModel
import com.example.cookpilot.viewmodel.UserViewModel

@Composable
fun UserPage(
    recipeViewModel: RecipeViewModel,
    userViewModel: UserViewModel
) {
    // --- STATES ---
    val uiState by userViewModel.uiState.collectAsState()
    val userRecipes by recipeViewModel.userRecipes.collectAsState()
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    LaunchedEffect(uiState.userId) {
        uiState.userId?.let { userId ->
            recipeViewModel.loadUserRecipes(userId)
        }
    }

    val userRecipeActions: (Recipe) -> List<RecipeAction> = { recipe ->
        listOf(
            RecipeAction("Edit") {
                // TODO: Navegar a edición
            },
            RecipeAction("Delete") {
                // TODO: Implementar eliminación
            }
        )
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> profileImageUri = uri }
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
                // 1. HEADER
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
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile picture",
                            modifier = Modifier.size(60.dp),
                            tint = Color.White
                        )
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
                // 2. RECIPE LIST
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

    // --- ACTION DIALOGUE ---
    selectedRecipe?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            actions = userRecipeActions(recipe),
            onDismiss = { selectedRecipe = null }
        )
    }
}
