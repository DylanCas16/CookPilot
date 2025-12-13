package com.example.cookpilot.ui.components.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.example.cookpilot.ui.theme.CustomColors

@Composable
fun LogInMessage(
    onGoToAuthMenu: () -> Unit,
    text: String
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RectangleShape)
            .clip(RectangleShape)
            .border(3.dp, MaterialTheme.colorScheme.secondary, RectangleShape)
            .padding(30.dp, 25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text)
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onGoToAuthMenu, colors = CustomColors.customPrimaryButtonColor()) {
            Text("Go to log in")
        }
    }
}