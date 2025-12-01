package com.example.cookpilot.ui.pages

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cookpilot.model.Recipe
import com.example.cookpilot.ui.components.RecipeAction
import com.example.cookpilot.ui.components.RecipeDetailDialog
import com.example.cookpilot.ui.components.RecipeList

@Composable
fun UserPage() {
    // --- STATES ---
    var userName by remember { mutableStateOf("Chef CookPilot") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    val userRecipeActions: (Recipe) -> List<RecipeAction> = { recipe ->
        listOf(
            RecipeAction("Edit") { /* Lógica de edición */ },
            RecipeAction("Remove") { /* Eliminar de la lista local */ }
        )
    }


    // RECIPE LIST
    val recetas = remember { mutableStateListOf(
        Recipe("1", "Pasta Carbonara", "Clásica italiana", 30, listOf("Pasta", "Huevo"), "Step 5: enjoy", 2, "Chef", null),
        Recipe("2", "Sushi Roll", "Fresco y delicioso", 30, listOf("Arroz", "Salmón"), "Step 5: enjoy", 4, "Chef", null),
        Recipe("3", "Tarta de Queso", "Postre suave", 30, listOf("Queso", "Galleta"), "Step 5: enjoy", 1, "Chef", null)
    )}

    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> profileImageUri = uri }
    )

    // --- INTERFACE ---
    Scaffold { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // 1. HEADER
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
                ) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .background(Color.LightGray)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileImageUri != null) {
                            Icon(
                                painter = painterResource(android.R.drawable.ic_menu_gallery),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                                tint = Color.DarkGray
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Placeholder picture",
                                modifier = Modifier.size(60.dp),
                                tint = Color.White
                            )
                        }
                    }
                    Text(
                        text = "Change profile picture",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                HorizontalDivider()

                Text(
                    text = "My Recipes (${recetas.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
            item {
                RecipeList(
                    recipes = recetas,
                    onRecipeClick = { recipe ->
                        selectedRecipe = recipe
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }
        }
    }

    // --- ACTION DIALOGUE ---
    if (selectedRecipe != null) {
        RecipeDetailDialog(
            recipe = selectedRecipe!!,
            actions = userRecipeActions(selectedRecipe!!),
            onDismiss = { selectedRecipe = null }
        )
    }
}