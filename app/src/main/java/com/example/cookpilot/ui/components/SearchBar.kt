package com.example.cookpilot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cookpilot.R
import com.example.cookpilot.model.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    recipes: List<Recipe>,
    onRecipeSelected: (Recipe) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    val allRecipes = recipes

    val results: List<Recipe> = if (query.isBlank()) {
        emptyList()
    } else {
        val term = query.lowercase().trim()
        allRecipes.filter { recipe ->
            val lowerIngredients = recipe.ingredients.map { it.lowercase() }
            lowerIngredients.any { it.contains(term) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                active = false
            },
            active = active,
            onActiveChange = { active = it },
            placeholder = { Text("Search recipe or ingredients") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search icon")
            },
            trailingIcon = {
                if (active) {
                    IconButton(onClick = {
                        if (query.isNotEmpty()) query = "" else active = false
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (query.isBlank()) {
                    items(listOf("chicken", "pasta", "eggs")) { suggestion ->
                        ListItem(
                            headlineContent = { Text(suggestion) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_history_tab),
                                    contentDescription = "History icon"
                                )
                            },
                            modifier = Modifier
                                .clickable {
                                    query = suggestion
                                    active = false
                                }
                                .size(40.dp)
                        )
                    }
                } else {
                    // Ahora results es List<Recipe>
                    items(results) { recipe ->
                        ListItem(
                            headlineContent = { Text(recipe.title) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_pizza_search),
                                    contentDescription = "Search icon",
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            modifier = Modifier.clickable {
                                query = recipe.title
                                active = false
                                onRecipeSelected(recipe)
                            }
                        )
                    }

                    if (results.isEmpty()) {
                        item {
                            Text(
                                "Not recipes found for \"$query\"",
                                modifier = Modifier.padding(16.dp),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}
