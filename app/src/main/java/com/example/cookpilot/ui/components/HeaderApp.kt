package com.example.cookpilot.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
        modifier = Modifier.height(100.dp),
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menú lateral",
                    modifier = Modifier
                        .size(40.dp)
                )
            }
        },
        title = {
            IconButton(onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "LoginIcon",
                    modifier = Modifier
                        .size(60.dp)
                )
            }
        }
    )
}
