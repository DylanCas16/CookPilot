package com.example.cookpilot.ui.components.recipe

import APPWRITE_BUCKET_ID
import APPWRITE_PROJECT_ID
import APPWRITE_PUBLIC_ENDPOINT

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.cookpilot.R
import com.example.cookpilot.model.Recipe


data class RecipeAction(
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun buildImageUrl(fileId: String?, bucketId: String = APPWRITE_BUCKET_ID): String? {
    if (fileId == null) return null
    val endpoint = APPWRITE_PUBLIC_ENDPOINT
    val projectId = APPWRITE_PROJECT_ID
    return "$endpoint/storage/buckets/$bucketId/files/$fileId/view?project=$projectId"
}

@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: (Recipe) -> Unit,
    modifier: Modifier = Modifier
) {
    val imageUrl = buildImageUrl(recipe.fileId)

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .clickable { onClick(recipe) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // --- IMAGE / PLACEHOLDER ---
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "${recipe.title} picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop,
                    error = painterResource(android.R.drawable.ic_menu_report_image)
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.LightGray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Restaurant,
                        contentDescription = "Placeholder recipe",
                        modifier = Modifier.fillMaxSize(0.5f),
                        tint = Color.Gray
                    )
                }
            }

            if (recipe.dietaryTags.isNotEmpty()) {
                DietaryTagDisplay(
                    tags = recipe.dietaryTags,
                    maxVisible = 2,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }

            // --- TITLE ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = recipe.title,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                Text(
                    text = "Difficulty: ${recipe.difficulty}/5 • ${recipe.cookingTime} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun RecipeDetailDialog(
    recipe: Recipe,
    actions: List<RecipeAction>,
    onDismiss: () -> Unit
) {
    val imageUrl = buildImageUrl(recipe.fileId)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxHeight(0.8f)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    item {
                        // === IMAGE ===
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.LightGray.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUrl != null) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = "Recipe image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    error = painterResource(R.drawable.ic_pizza_search)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Filled.Restaurant,
                                    contentDescription = "Placeholder",
                                    modifier = Modifier.fillMaxSize(0.5f),
                                    tint = Color.Gray
                                )
                            }
                        }

                        // === TITLE AND DESCRIPTION ===
                        Column(modifier = Modifier.padding(15.dp)) {
                            Text(
                                text = recipe.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = recipe.description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            // === COOKING TIME ===
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Cooking time:", fontWeight = FontWeight.SemiBold)
                                Text("${recipe.cookingTime} minutes")
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // === CREATOR ===
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("CP Chef:", fontWeight = FontWeight.SemiBold)
                                Text(recipe.creator)
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            // DIFFICULTY
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(
                                    text = "Difficulty:",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))

                                for (i in 1..5) {
                                    Icon(
                                        imageVector = if (i <= recipe.difficulty) Icons.Filled.Star else Icons.Outlined.Star,
                                        contentDescription = "Difficulty $i",
                                        modifier = Modifier
                                            .size(30.dp)
                                            .padding(4.dp),
                                        tint = if (i <= recipe.difficulty) MaterialTheme.colorScheme.primary else Color.Gray
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 16.dp))
                        }
                    }
                    if (recipe.dietaryTags.isNotEmpty()) {
                        item {
                            Divider(modifier = Modifier.padding(vertical = 16.dp))

                            Text(
                                text = "Dietary Information:",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )

                            DietaryTagDisplay(
                                tags = recipe.dietaryTags,
                                maxVisible = 10,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    // === INGREDIENTS LIST ===
                    item {
                        Text(
                            text = "Ingredients:",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                    items(recipe.ingredients) { ingredient ->
                        ListItem(
                            headlineContent = { Text(ingredient) },
                            leadingContent = { Icon(Icons.Filled.Restaurant, contentDescription = null) }
                        )
                    }

                    // === STEPS ===
                    item {
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        Text(
                            text = "Steps:",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Text(
                            text = recipe.steps,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // --- ACTION MENU ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    actions.forEach { action ->
                        Button(
                            onClick = {
                                action.onClick()
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(action.label)
                        }
                    }
                }
            }
        }
    }
}