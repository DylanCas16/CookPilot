package com.example.cookpilot.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun DashedDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.Red,
    strokeWidth: Dp = 2.dp,
    dashLength: Dp = 3.dp,
    gapLength: Dp = 0.dp
) {
    Canvas(modifier.fillMaxWidth().height(strokeWidth)) {
        val interval = floatArrayOf(dashLength.toPx(), gapLength.toPx())

        drawLine(
            color = color,
            start = Offset(0f, strokeWidth.toPx() / 2),
            end = Offset(size.width, strokeWidth.toPx() / 2),
            strokeWidth = strokeWidth.toPx(),
            pathEffect = PathEffect.dashPathEffect(interval, 0f)
        )
    }
}