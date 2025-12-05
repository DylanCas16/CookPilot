package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.RecipeAction
import com.example.cookpilot.ui.components.RecipeCard
import com.example.cookpilot.ui.components.RecipeDetailDialog
import com.example.cookpilot.ui.components.SearchBar
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
    // --- ESTADOS DE LA BÚSQUEDA PERSONALIZADA (Tags Incrustados) ---
    var activeTags by rememberSaveable { mutableStateOf(emptyList<String>()) }
    var currentInput by rememberSaveable { mutableStateOf("") }

    val recipes by recipeViewModel.recipes.collectAsState()
    val uiState by userViewModel.uiState.collectAsState()
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    val suggestions = remember {
        listOf("chicken", "pasta", "rice", "tomato", "cheese", "eggs", "beef", "potato")
    }

    LaunchedEffect(Unit) {
        if (recipes.isEmpty()) {
            recipeViewModel.loadAllRecipes()
        }
    }

    // SEARCH TERMS COMBINED
    val allSearchTerms = remember(activeTags, currentInput) {
        val inputTerm = currentInput.trim().lowercase().takeIf { it.isNotBlank() }
        val allTerms = if (inputTerm != null) activeTags + inputTerm else activeTags
        allTerms.distinct()
    }

    // SEARCH RESULTS
    val searchResults: List<Recipe> = remember(allSearchTerms, recipes) {
        if (allSearchTerms.isEmpty()) {
            emptyList()
        } else {
            recipes.filter { recipe ->
                val lowerIngredients = recipe.ingredients.map { it.lowercase() }
                allSearchTerms.all { term ->
                    lowerIngredients.any { ingredient -> ingredient.contains(term) }
                }
            }
        }
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

        // --- PAGE CONTENT ---
        Divider()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when {
                allSearchTerms.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Search for recipes by ingredients",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try: chicken, pasta, tomato...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                searchResults.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No recipes found",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try different ingredients: ${allSearchTerms.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
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

    // --- RECIPE DETAILS ---
    selectedRecipe?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            actions = listOf(
                RecipeAction("Close") { selectedRecipe = null }
            ),
            onDismiss = { selectedRecipe = null }
        )
    }
}