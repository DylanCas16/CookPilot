package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookpilot.ui.components.RecipeForm
import com.example.cookpilot.viewmodel.RecipeViewModel

@Composable
fun CreatePage(
    viewModel: RecipeViewModel = viewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        RecipeForm(
            onSaveRecipe = { recipe ->
                viewModel.createRecipeFromForm(
                    title = recipe.title,
                    description = recipe.description,
                    steps = recipe.steps,
                    difficulty = recipe.difficulty,
                    ingredients = recipe.ingredients,
                    cookingTime = recipe.cookingTime,
                    creator = recipe.creator,
                    imageUri = recipe.imageUri
                )
            }
        )
    }
}
