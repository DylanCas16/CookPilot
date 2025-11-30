package com.example.cookpilot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cookpilot.model.Recipe


@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(1f) // Esto hace que la tarjeta sea cuadrada (width = height)
            .fillMaxWidth()
            .clickable { onClick(recipe) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            contentAlignment = Alignment.Center // Centra el contenido (el texto)
        ) {
            Text(
                text = recipe.title,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 2, // Limita el nombre a dos líneas
                overflow = TextOverflow.Ellipsis // Añade puntos suspensivos si se excede el límite
            )
        }
    }
}