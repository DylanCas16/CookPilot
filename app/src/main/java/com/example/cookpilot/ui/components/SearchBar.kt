package com.example.cookpilot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.cookpilot.R
import com.example.cookpilot.ui.theme.CustomColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchBar(
    activeTags: List<String>,
    onTagsChange: (List<String>) -> Unit,
    currentInput: String,
    onInputChange: (String) -> Unit,
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isInputFocused by remember { mutableStateOf(false) }
    var showSuggestions by remember { mutableStateOf(false) }

    var textFieldValue by remember(currentInput) {
        mutableStateOf(TextFieldValue(currentInput, TextRange(currentInput.length)))
    }

    val addTag: (String) -> Unit = { tag ->
        val trimmedTag = tag.trim().lowercase()
        if (trimmedTag.isNotBlank() && !activeTags.contains(trimmedTag))
            onTagsChange(activeTags + trimmedTag)
        onInputChange("")
        textFieldValue = TextFieldValue("")
        showSuggestions = false
    }

    val removeTag: (String) -> Unit = { tagToRemove ->
        onTagsChange(activeTags.filter { it != tagToRemove })
    }

    val handleInputChange: (TextFieldValue) -> Unit = { newValue ->
        val newText = newValue.text

        if (newText.isNotEmpty() && (newText.endsWith(",") || newText.endsWith(" "))) {
            val tagCandidate = newText.dropLast(1)
            addTag(tagCandidate)
        } else {
            onInputChange(newText)
            textFieldValue = newValue
            showSuggestions = newText.isNotBlank()
        }
    }

    Surface(
        shape = MaterialTheme.shapes.extraLarge,
        shadowElevation = 4.dp,
        modifier = modifier
    ) {
        Column {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = handleInputChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isInputFocused = focusState.isFocused
                        if (focusState.isFocused)
                            showSuggestions = textFieldValue.text.isNotBlank()
                    },
                shape = RoundedCornerShape(20.dp),
                colors = CustomColors.customTextFieldColors(),
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_pizza_search),
                        contentDescription = "Search icon",
                        modifier = Modifier.size(24.dp)
                    )
                },
                placeholder = {
                    if (activeTags.isEmpty() && currentInput.isBlank())
                        Text("Search for ingredients...")
                },
                prefix = {
                    FlowRow(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(end = 4.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        activeTags.forEach { tag ->
                            InputChip(
                                selected = true,
                                onClick = { removeTag(tag) },
                                label = { Text(tag) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove $tag",
                                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                                    )
                                },
                                colors = CustomColors.customInputChipColors(),
                                modifier = Modifier.height(32.dp)
                            )
                        }
                    }
                }
            )

            if (isInputFocused && showSuggestions && suggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 200.dp)
                ) {
                    items(suggestions) { suggestion ->
                        ListItem(
                            headlineContent = { Text(suggestion) },
                            modifier = Modifier.clickable {
                                onSuggestionClick(suggestion)
                                showSuggestions = false
                            }
                        )
                    }
                }
            }
        }
    }
}
