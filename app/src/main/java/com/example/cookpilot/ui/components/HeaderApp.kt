package com.example.cookpilot.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookpilot.R
import com.example.cookpilot.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderApp(onMenuClick: () -> Unit) {

    val userViewModel: UserViewModel = viewModel()
    val uiState by userViewModel.uiState.collectAsState()

    var showRegisterDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            showRegisterDialog = false
        }
    }

    if (showRegisterDialog) {
        Dialog(onDismissRequest = { showRegisterDialog = false }) {
            UserRegistrationForm(
                onRegisterUser = { user ->
                    userViewModel.register(user)
                }
            )
            if (uiState.error != null) {
                Text("Error: ${uiState.error}")
            }
        }
    }

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
            IconButton(onClick = { showRegisterDialog = true }) {
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
