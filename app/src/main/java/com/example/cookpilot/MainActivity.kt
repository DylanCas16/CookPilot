package com.example.cookpilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookpilot.ui.components.HeaderApp
import com.example.cookpilot.ui.components.LoginDialog
import com.example.cookpilot.ui.components.Sidebar
import com.example.cookpilot.ui.pages.CreatePage
import com.example.cookpilot.ui.pages.HistoryPage
import com.example.cookpilot.ui.pages.SearchPage
import com.example.cookpilot.ui.pages.UserPage
import com.example.cookpilot.ui.theme.CookPilotTheme
import com.example.cookpilot.ui.theme.SecondaryColor
import com.example.cookpilot.viewmodel.HistoryViewModel
import com.example.cookpilot.viewmodel.RecipeViewModel
import com.example.cookpilot.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppwriteClient.init(this)
        enableEdgeToEdge()
        setContent {
            val fondoChefPainter = painterResource(id = R.drawable.background_image)
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = fondoChefPainter,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                CookPilotTheme {
                    CookPilotApp()
                }
            }
        }
    }

@PreviewScreenSizes
@Composable
fun CookPilotApp() {
    val userViewModel: UserViewModel = viewModel()
    val recipeViewModel: RecipeViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val uiState by userViewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            userViewModel.checkSession()
        }

        var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.History) }
        val myItemColors = NavigationSuiteDefaults.itemColors(
            navigationBarItemColors = NavigationBarItemDefaults.colors(
                selectedIconColor = SecondaryColor,
                selectedTextColor = SecondaryColor,
                unselectedIconColor = Color.DarkGray,
                unselectedTextColor = Color.DarkGray,
                indicatorColor = SecondaryColor.copy(alpha = 0.2f)
            )
        )
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerContent = {
                Sidebar(
                    onOptionSelected = {
                        scope.launch { drawerState.close() }
                    },
                    userViewModel = userViewModel,
                    drawerState = drawerState
                )
            },
            drawerState = drawerState,
        ) {
            NavigationSuiteScaffold(
                containerColor = Color.Transparent,
                navigationSuiteItems = {
                    AppDestinations.entries/*.filter { it != AppDestinations.Profile }*/.forEach { destination ->

                        val isSelected = destination == currentDestination

                    item(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                painter = painterResource(id = destination.icon),
                                contentDescription = destination.label,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        label = { Text(destination.label) },
                        colors = myItemColors
                    )
                }
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HeaderApp(
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onGoToProfile = { currentDestination = AppDestinations.Profile },
                    userViewModel = userViewModel
                )
                if (uiState.showLoginDialog) {
                    LoginDialog(
                        uiState = uiState,
                        onLogin = { email, password ->
                            userViewModel.login(email, password)
                        },
                        onDismiss = { userViewModel.closeLoginDialog() },
                        onRegisterClick = {
                            userViewModel.closeLoginDialog()
                            userViewModel.openRegisterDialog()
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    when (currentDestination) {
                        AppDestinations.History -> HistoryPage(
                            historyViewModel = historyViewModel,
                            userViewModel = userViewModel,
                            onNavigateToCreate = { currentDestination = AppDestinations.Create }
                        )
                        AppDestinations.Create -> CreatePage(
                            recipeViewModel = recipeViewModel,
                            userViewModel = userViewModel,
                            onGoToLogin = {
                                userViewModel.openLoginDialog()
                            }
                        )
                        AppDestinations.Search -> SearchPage(
                            recipeViewModel = recipeViewModel,
                            historyViewModel = historyViewModel,
                            userViewModel = userViewModel,
                        )
                        AppDestinations.Profile -> UserPage()
                    }

                    }
                }
            }
        }
    }

    enum class AppDestinations(
        val label: String,
        val icon: Int,
    ) {
        History("History", R.drawable.ic_history_tab),
        Create("Create", R.drawable.ic_create_tab),
        Search("Search", R.drawable.ic_search_tab),
        Profile("Profile", R.drawable.ic_user)

    }
}