// DietaryTagSelector.kt (NUEVO ARCHIVO)
package com.example.cookpilot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cookpilot.model.DietaryTag
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun DietaryTagSelector(
    selectedTags: List<String>,
    onTagToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Dietary Information:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Select all that apply",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        FlowRow(
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            DietaryTag.entries.forEach { tag ->
                val isSelected = selectedTags.contains(tag.name)

                DietaryTagChip(
                    tag = tag,
                    isSelected = isSelected,
                    onClick = { onTagToggle(tag.name) }
                )
            }
        }
    }
}

@Composable
fun DietaryTagChip(
    tag: DietaryTag,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .background(
                color = if (isSelected) tag.color.copy(alpha = 0.2f) else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 2.dp,
                color = if (isSelected) tag.color else Color.Gray.copy(alpha = 0.3f),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = tag.color,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "${tag.emoji} ${tag.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) tag.color else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Componente para mostrar tags en RecipeCard (solo lectura)
@Composable
fun DietaryTagDisplay(
    tags: List<String>,
    modifier: Modifier = Modifier,
    maxVisible: Int = 3
) {
    if (tags.isEmpty()) return

    FlowRow(
        mainAxisSpacing = 4.dp,
        crossAxisSpacing = 4.dp,
        modifier = modifier
    ) {
        tags.take(maxVisible).forEach { tagName ->
            DietaryTag.fromString(tagName)?.let { tag ->
                Surface(
                    color = tag.color.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = tag.emoji,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = tag.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }

        if (tags.size > maxVisible) {
            Surface(
                color = Color.Gray.copy(alpha = 0.7f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(24.dp)
            ) {
                Text(
                    text = "+${tags.size - maxVisible}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
