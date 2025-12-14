package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.auth.LogInMessage
import com.example.cookpilot.ui.components.history.ClearHistoryDialog
import com.example.cookpilot.ui.components.history.ClearHistoryFab
import com.example.cookpilot.ui.components.history.HistoryGrid
import com.example.cookpilot.ui.components.recipe.RecipeAction
import com.example.cookpilot.ui.components.recipe.RecipeDetailDialog
import com.example.cookpilot.utils.HISTORY_TOTAL_SLOTS
import com.example.cookpilot.viewmodel.HistoryViewModel
import com.example.cookpilot.viewmodel.UserViewModel

@Composable
fun HistoryPage(
    historyViewModel: HistoryViewModel,
    userViewModel: UserViewModel,
    onNavigateToSearch: () -> Unit,
    onGoToAuthMenu: () -> Unit
) {
    val historyRecipes by historyViewModel.historyRecipes.collectAsState()
    val uiState by userViewModel.uiState.collectAsState()
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var showClearDialog by remember { mutableStateOf(false) }


    val totalSlots = HISTORY_TOTAL_SLOTS
    val filledSlots: List<Recipe> = historyRecipes.take(totalSlots)
    val emptySlots = (totalSlots - filledSlots.size).coerceAtLeast(0)

    if (!uiState.isLoggedIn) {
        LogInMessage(
            onGoToAuthMenu = onGoToAuthMenu,
            text = "You must be logged in to have a history."
        )
        return
    }

    LaunchedEffect(uiState.userId) {
        uiState.userId?.let(historyViewModel::loadUserHistory)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HistoryGrid(
            filledSlots = filledSlots,
            emptySlots = emptySlots,
            onRecipeClick = { selectedRecipe = it },
            onNavigateToSearch = onNavigateToSearch,
            modifier = Modifier.fillMaxSize()
        )

        if (historyRecipes.isNotEmpty()) Box(modifier = Modifier.fillMaxSize()) {
            ClearHistoryFab(
                onClick = { showClearDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            )
        }
    }

    if (showClearDialog) {
        ClearHistoryDialog(
            onDismiss = { showClearDialog = false },
            onConfirmDelete = {
                uiState.userId?.let(historyViewModel::clearHistory)
                showClearDialog = false
            }
        )
    }

    selectedRecipe?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            actions = listOf(RecipeAction("Close") { selectedRecipe = null }),
            onDismiss = { selectedRecipe = null }
        )
    }
}
