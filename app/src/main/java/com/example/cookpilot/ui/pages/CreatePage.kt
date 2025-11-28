package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookpilot.ui.components.RecipeForm
import com.example.cookpilot.viewmodel.RecipeViewModel
import kotlinx.coroutines.launch

@Composable
fun CreatePage(
    viewModel: RecipeViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val showSuccessMessage: () -> Unit = {
        scope.launch {
            snackbarHostState.showSnackbar(
                message = "Recipe created successfully",
                actionLabel = "OK",
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            RecipeForm(
                onSaveRecipe = { recipe ->
                    viewModel.createRecipeFromForm(
                        title = recipe.recipeName,
                        description = recipe.description,
                        steps = recipe.steps,
                        difficulty = recipe.difficulty,
                        ingredients = recipe.ingredients,
                        cookingTime = recipe.cookingTime,
                        creator = recipe.creator,
                        imageUri = recipe.imageUri
                    )
                    showSuccessMessage()
                }
            )
        }
    }
}