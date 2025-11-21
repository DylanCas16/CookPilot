package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cookpilot.ui.components.RecipeForm

@Composable
fun CreatePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        RecipeForm(
            onSaveRecipe = { data ->
                println("created")
            }
        )
    }
}