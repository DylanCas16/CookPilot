package com.example.cookpilot.ui.components.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

private val BorderWidth = 5.dp
private const val DashLength = 30f
private const val GapLength = 30f
private const val CardCornerRadius = 12f

@Composable
fun HistoryEmptyCard(onClick: () -> Unit) {
    val borderColor = MaterialTheme.colorScheme.tertiary
    val density = LocalDensity.current.density
    val cornerRadiusPx = CardCornerRadius * density

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val dashPathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(DashLength, GapLength),
                        phase = 0f
                    )
                    drawRoundRect(
                        color = borderColor,
                        topLeft = Offset(BorderWidth.toPx() / 2, BorderWidth.toPx() / 2),
                        size = Size(
                            width = size.width - BorderWidth.toPx(),
                            height = size.height - BorderWidth.toPx()
                        ),
                        style = Stroke(
                            width = BorderWidth.toPx(),
                            pathEffect = dashPathEffect
                        ),
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                }
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create recipe",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}