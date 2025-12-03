package com.example.cookpilot.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.cookpilot.R
import com.example.cookpilot.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderApp(
    onMenuClick: () -> Unit,
    onGoToProfile: () -> Unit,
    userViewModel: UserViewModel
) {
    val uiState by userViewModel.uiState.collectAsState()
    var showAuthMenu by remember { mutableStateOf(false) }
    var currentView: String? by remember { mutableStateOf(null) }


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
            if (!uiState.isLoggedIn) {
                IconButton(onClick = { showAuthMenu = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = "LoginIcon",
                        modifier = Modifier
                            .size(60.dp)
                    )
                }
            } else {
                IconButton(onClick = onGoToProfile) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_user),
                        contentDescription = "UserIcon",
                        modifier = Modifier
                            .size(60.dp)
                    )
                }
            }
        }
    )

    if (showAuthMenu) {
        AuthMenu(
            onDismiss = { showAuthMenu = false },
            onLoginClick = { currentView = "LOGIN"; showAuthMenu = false},
            onRegisterClick = { currentView = "REGISTER"; showAuthMenu = false }
        )
    }

    if (currentView == "LOGIN") {
        Dialog(onDismissRequest = { currentView = null }) {
            UserLoginForm(
                onLoggingUser = {
                userViewModel.login(it.email, it.password)
                currentView = null
            },
                onRegisterClick = {
                    currentView = "REGISTER"
                }
            )
        }
    }

    if (currentView == "REGISTER") {
        Dialog(onDismissRequest = { currentView = null }) {
            UserRegistrationForm(onRegisterUser = {
                userViewModel.register(it)
                currentView = null
            },
                onLoginClick = {
                    currentView = "LOGIN"
                }
            )
        }
    }
}
