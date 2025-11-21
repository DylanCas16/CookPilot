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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    // Datos de prueba (Simulando tu base de datos)
    val allRecipes = listOf(
        "Pasta Carbonara", "Pizza Margarita", "Sushi de Salmón",
        "Hamburguesa Vegana", "Ensalada César", "Tacos al Pastor",
        "Paella Valenciana", "Brownie de Chocolate", "Tortilla de Patatas",
        "Pollo al Curry", "Lasaña de Carne", "Gazpacho Andaluz"
    )

    val results = if (query.isBlank()) {
        emptyList()
    } else {
        allRecipes.filter { it.contains(query, ignoreCase = true) }
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (query.isBlank()) {
                    items(listOf("Pollo", "Pasta", "Vegano")) { sugerencia ->
                        ListItem(
                            headlineContent = { Text(sugerencia) },
                            leadingContent = { Icon(
                                painter = painterResource(R.drawable.ic_history_tab),
                                contentDescription = "History icon")
                            },
                            modifier = Modifier.clickable {
                                query = sugerencia
                                active = false
                            }.size(40.dp)
                        )
                    }
                } else {
                    items(results) { receta ->
                        ListItem(
                            headlineContent = { Text(receta) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_pizza_search),
                                    contentDescription = "Search icon",
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            modifier = Modifier.clickable {
                                query = receta
                                active = false
                                println("Navegar a receta: $receta")
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