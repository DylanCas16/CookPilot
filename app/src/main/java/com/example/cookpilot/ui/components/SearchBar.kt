package com.example.cookpilot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var active by rememberSaveable { mutableStateOf(false) }

    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = {
            onSearch()
            active = false
        },
        active = active,
        onActiveChange = { active = it },
        placeholder = { Text("Search by ingredients (e.g., chicken, rice)") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search icon")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // SOLO sugerencias de texto, sin iconos grandes
        LazyColumn(
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(suggestions) { suggestion ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    modifier = Modifier.clickable {
                        onSuggestionClick(suggestion)
                        active = false
                    }
                )
            }
        }
    }
}
