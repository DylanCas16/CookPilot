package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookpilot.ui.components.HistoryEmptyCard
import com.example.cookpilot.viewmodel.RecipeViewModel

@Composable
fun HistoryPage(
    onNavigateToCreate: () -> Unit,
    viewModel: RecipeViewModel = viewModel()
) {
    val recipes by viewModel.recipes.collectAsState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (recipes.isEmpty()) {
            items(20) { _ ->
                HistoryEmptyCard(
                    onClick = onNavigateToCreate
                )
            }
        } /*else {
            items(recipes) { recipe ->
                HistoryCard(
                    recipe = recipe,
                    onClick = { /* más tarde */ }
                )
            }
        }*/
    }
}
