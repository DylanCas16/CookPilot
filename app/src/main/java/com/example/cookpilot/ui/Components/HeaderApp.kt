package com.example.cookpilot.ui.Components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cookpilot.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderApp(onMenuClick: () -> Unit) {
    CenterAlignedTopAppBar (
        modifier = Modifier.fillMaxWidth().height(120.dp),
        title = {
            IconButton(onClick = onMenuClick) { }
            Icon(
                painter = painterResource(id = R.drawable.user_icon),
                contentDescription = "LoginIcon",
                modifier = Modifier.size(100.dp).padding(0.dp, 20.dp)
            )
        }
    )
}
