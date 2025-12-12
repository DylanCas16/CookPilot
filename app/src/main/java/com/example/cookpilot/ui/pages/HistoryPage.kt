package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.recipe.HistoryEmptyCard
import com.example.cookpilot.ui.components.recipe.RecipeAction
import com.example.cookpilot.ui.components.recipe.RecipeCard
import com.example.cookpilot.ui.components.recipe.RecipeDetailDialog
import com.example.cookpilot.viewmodel.HistoryViewModel
import com.example.cookpilot.viewmodel.UserViewModel

@Composable
fun HistoryPage(
    historyViewModel: HistoryViewModel,
    userViewModel: UserViewModel,
    onNavigateToSearch: () -> Unit
) {
    val historyRecipes by historyViewModel.historyRecipes.collectAsState()
    val uiState by userViewModel.uiState.collectAsState()
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    LaunchedEffect(uiState.userId) {
        uiState.userId?.let { userId ->
            historyViewModel.loadUserHistory(userId)
        }
    }

    val totalSlots = 10
    val filledSlots = historyRecipes.take(totalSlots)
    val emptySlots = totalSlots - filledSlots.size

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(filledSlots) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { selectedRecipe = recipe }
            )
        }

        if (emptySlots > 0) item { HistoryEmptyCard(onClick = onNavigateToSearch) }
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
