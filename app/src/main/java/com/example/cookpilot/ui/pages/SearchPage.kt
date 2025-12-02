package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

@Composable
fun SearchPage(
    recipeViewModel: RecipeViewModel,
    historyViewModel: HistoryViewModel,
    userViewModel: UserViewModel
) {
    val recipes by recipeViewModel.recipes.collectAsState()
    val uiState by userViewModel.uiState.collectAsState()
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    var query by rememberSaveable { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (recipes.isEmpty()) {
            recipeViewModel.loadAllRecipes()
        }
    }

    val suggestions = remember {
        listOf("chicken", "pasta", "rice", "tomato", "cheese", "eggs", "beef", "potato")
    }

    val searchTerms = query
        .lowercase()
        .split(',', ' ')
        .map { it.trim() }
        .filter { it.isNotBlank() }

    val searchResults: List<Recipe> = if (searchTerms.isEmpty()) {
        emptyList()
    } else {
        recipes.filter { recipe ->
            val lowerIngredients = recipe.ingredients.map { it.lowercase() }
            searchTerms.all { term ->
                lowerIngredients.any { ingredient -> ingredient.contains(term) }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { isSearching = true },
            suggestions = suggestions.filter { it.contains(query.lowercase()) },
            onSuggestionClick = { suggestion ->
                query = suggestion
                isSearching = true
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when {
                query.isEmpty() -> {
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
                            text = "Try different ingredients: ${searchTerms.joinToString(", ")}",
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
