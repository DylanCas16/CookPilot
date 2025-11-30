package com.example.cookpilot.ui.pages

import androidx.compose.foundation.layout.Arrangement
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cookpilot.ui.components.RecipeForm
import com.example.cookpilot.viewmodel.RecipeViewModel
import com.example.cookpilot.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun CreatePage(
    recipeViewModel: RecipeViewModel,
    userViewModel: UserViewModel,
    onGoToLogin: () -> Unit
) {

    val uiState by userViewModel.uiState.collectAsState()
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

    if (!uiState.isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("You must be logged in to create a recipe")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onGoToLogin) {
                Text("Go to log in")
            }
        }
    } else {
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
                    onSaveRecipe = { recipe, imageUri ->
                        recipeViewModel.createRecipeFromForm(
                            title = recipe.title,
                            description = recipe.description,
                            steps = recipe.steps,
                            difficulty = recipe.difficulty,
                            ingredients = recipe.ingredients,
                            cookingTime = recipe.cookingTime,
                            creator = recipe.creator,
                            fileId = imageUri
                        )
                    }
                )

            }
        }
    }
}