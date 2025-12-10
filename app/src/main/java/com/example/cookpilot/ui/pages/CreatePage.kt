package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cookpilot.ui.components.recipe.RecipeForm
import com.example.cookpilot.ui.components.showCustomMessage
import com.example.cookpilot.viewmodel.RecipeViewModel
import com.example.cookpilot.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun CreatePage(
    scope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    recipeViewModel: RecipeViewModel,
    userViewModel: UserViewModel,
    onGoToAuthMenu: () -> Unit
) {

    val uiState by userViewModel.uiState.collectAsState()

    if (!uiState.isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("You must be logged in to create a recipe")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onGoToAuthMenu) {
                Text("Go to log in")
            }
        }
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent.copy(alpha = 0.3f)
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(paddingValues)
            ) {
                RecipeForm(
                    onSaveRecipe = { recipe, imageUri ->
                        recipeViewModel.createRecipeFromForm(
                            title = recipe.title,
                            description = recipe.description,
                            steps = recipe.steps,
                            difficulty = recipe.difficulty,
                            ingredients = recipe.ingredients,
                            cookingTime = recipe.cookingTime,
                            creator = uiState.userId ?: "anon",
                            dietaryTags = recipe.dietaryTags,
                            fileUri = imageUri,

                            onSuccess = {showCustomMessage(
                                scope = scope,
                                snackbarHostState = snackbarHostState,
                                message = "Recipe created successfully!",
                                actionLabel = "Perfect",
                                duration = SnackbarDuration.Long
                            ) },
                            onError = { errorMessage ->
                                showCustomMessage(
                                    scope = scope,
                                    snackbarHostState = snackbarHostState,
                                    message = "Recipe creation failed: $errorMessage",
                                    actionLabel = "Try again",
                                    duration = SnackbarDuration.Long
                                )
                            }
                        )
                    }
                )
            }
        }
    }
}