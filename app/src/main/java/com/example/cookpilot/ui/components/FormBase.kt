package com.example.cookpilot.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cookpilot.ui.theme.CustomColors
import com.example.cookpilot.utils.FORM_BACKGROUND_ALPHA

@Composable
fun FormBase(
    modifier: Modifier = Modifier,
    formTitle: String,
    buttonText: String,
    onConfirmClick: () -> Unit,
    color: Color = MaterialTheme.colorScheme.surface.copy(FORM_BACKGROUND_ALPHA),
    snackbarHostState: SnackbarHostState? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 4.dp,
        color = color
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formTitle,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            content()
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onConfirmClick,
                modifier = Modifier.fillMaxWidth(),
                colors = CustomColors.customSecondaryButtonColor()
            ) {
                Text(buttonText)
            }
            snackbarHostState?.let { hostState ->
                Spacer(modifier = Modifier.height(16.dp))
                SnackbarHost(
                    hostState = hostState,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }
    }
}