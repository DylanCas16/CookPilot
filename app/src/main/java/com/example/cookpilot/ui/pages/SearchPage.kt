package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.CustomDivider
import com.example.cookpilot.ui.components.SearchBar
import com.example.cookpilot.ui.components.recipe.RecipeAction
import com.example.cookpilot.ui.components.recipe.RecipeCard
import com.example.cookpilot.ui.components.recipe.RecipeDetailDialog
import com.example.cookpilot.ui.components.search.EmptySearchHint
import com.example.cookpilot.ui.components.search.NoResultsHint
import com.example.cookpilot.viewmodel.HistoryViewModel
import com.example.cookpilot.viewmodel.RecipeViewModel
import com.example.cookpilot.viewmodel.UserViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchPage(
    recipeViewModel: RecipeViewModel,
    historyViewModel: HistoryViewModel,
    userViewModel: UserViewModel
) {
    var activeTags by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    var currentInput by rememberSaveable { mutableStateOf("") }

    val recipes by recipeViewModel.recipes.collectAsState()
    val uiState by userViewModel.uiState.collectAsState()

    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    val suggestions = remember {
        listOf("chicken", "pasta", "rice", "tomato", "cheese", "eggs", "beef", "potato")
    }

    LaunchedEffect(recipes.isEmpty()) {
        if (recipes.isEmpty()) recipeViewModel.loadAllRecipes()
    }

    val allSearchTerms: List<String> = remember(activeTags, currentInput) {
        val inputTerm = currentInput.trim().lowercase().takeIf { it.isNotBlank() }
        val allTerms = if (inputTerm != null) activeTags + inputTerm else activeTags
        allTerms.map { it.lowercase() }.distinct()
    }

    val searchResults: List<Recipe> = remember(allSearchTerms, recipes) {
        filterRecipesByIngredients(recipes, allSearchTerms)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            activeTags = activeTags,
            onTagsChange = { activeTags = it },
            currentInput = currentInput,
            onInputChange = { currentInput = it },
            suggestions = suggestions.filter { it.contains(currentInput.lowercase()) },
            onSuggestionClick = { suggestion ->
                activeTags = activeTags + suggestion
                currentInput = ""
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        CustomDivider()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when {
                allSearchTerms.isEmpty() -> EmptySearchHint()
                searchResults.isEmpty() -> NoResultsHint()
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(searchResults) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = {
                                    uiState.userId?.let { userId ->
                                        recipe.id?.let { recipeId ->
                                            historyViewModel.saveRecipeView(userId, recipeId)
                                        }
                                    }
                                    selectedRecipe = recipe
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    selectedRecipe?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            actions = listOf(RecipeAction("Close") { selectedRecipe = null }),
            onDismiss = { selectedRecipe = null }
        )
    }
}

private fun filterRecipesByIngredients(
    recipes: List<Recipe>,
    terms: List<String>
): List<Recipe> {
    if (terms.isEmpty()) return emptyList()

    return recipes.filter { recipe ->
        val lowerIngredients = recipe.ingredients.map { it.lowercase() }
        terms.all { term -> lowerIngredients.any { it.contains(term) } }
    }
}
