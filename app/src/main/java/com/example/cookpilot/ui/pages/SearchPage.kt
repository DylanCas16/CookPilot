package com.example.cookpilot.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.SearchBar
import com.example.cookpilot.viewmodel.RecipeViewModel

@Composable
fun SearchPage(
    recipeViewModel: RecipeViewModel,
    onOpenRecipe: (Recipe) -> Unit
) {
    val recipes by recipeViewModel.recipes.collectAsState()

    SearchBar(
        recipes = recipes,
        onRecipeSelected = {  }
    )
}
