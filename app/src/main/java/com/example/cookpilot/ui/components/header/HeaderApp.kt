package com.example.cookpilot.ui.components.header

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cookpilot.R
import com.example.cookpilot.ui.theme.CustomColors
import com.example.cookpilot.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderApp(
    onMenuClick: () -> Unit,
    onGoToProfile: () -> Unit,
    onGoToAuthMenu: () -> Unit,
    userViewModel: UserViewModel
) {
    val uiState by userViewModel.uiState.collectAsState()

    CenterAlignedTopAppBar (
        modifier = Modifier.height(100.dp),
        colors = CustomColors.customTopAppBarColors(),
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
                IconButton(onClick = onGoToAuthMenu) {
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
}
