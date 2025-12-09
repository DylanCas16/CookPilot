package com.example.cookpilot

import android.content.Intent
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cookpilot.data.PreferencesManager
import com.example.cookpilot.ui.components.AuthMenu
import com.example.cookpilot.ui.components.DashedDivider
import com.example.cookpilot.ui.components.HeaderApp
import com.example.cookpilot.ui.components.LoginDialog
import com.example.cookpilot.ui.components.Sidebar
import com.example.cookpilot.ui.pages.CreatePage
import com.example.cookpilot.ui.pages.HistoryPage
import com.example.cookpilot.ui.pages.SearchPage
import com.example.cookpilot.ui.pages.UserPage
import com.example.cookpilot.ui.theme.CookPilotTheme
import com.example.cookpilot.ui.theme.CustomColors
import com.example.cookpilot.ui.theme.CustomColors.customNavigationSuiteContainerColors
import com.example.cookpilot.ui.theme.Transparent
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
            val context = LocalContext.current
            val preferencesManager = remember { PreferencesManager(context) }
            val isDarkMode by preferencesManager.isDarkModeFlow.collectAsState(initial = false)

            val chefPainterBackground = painterResource(id = R.drawable.background_image)
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = chefPainterBackground,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                CookPilotTheme(
                    darkTheme = isDarkMode
                ) {
                    CookPilotApp(
                        onRestartApp = { restartApp() }
                    )
                }
            }
        }
    }
    private fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
            finish()
    }

@Composable
fun CookPilotApp(onRestartApp: () -> Unit = {}) {
    val userViewModel: UserViewModel = viewModel()
    val recipeViewModel: RecipeViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val uiState by userViewModel.uiState.collectAsState()
    var showAuthMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.checkSession()
    }

        var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.History) }
        val myItemColors = CustomColors.customNavigationSuiteColors()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerContent = {
                Sidebar(
                    onOptionSelected = {
                        scope.launch { drawerState.close() }
                    },
                    userViewModel = userViewModel,
                    onLogout = {
                        scope.launch { drawerState.close() }
                        userViewModel.logout(onLogoutComplete = {
                            onRestartApp()
                        })
                    }
                )
            },
            drawerState = drawerState,
        ) {
            NavigationSuiteScaffold(
                containerColor = Transparent,
                navigationSuiteColors = customNavigationSuiteContainerColors(),
                navigationSuiteItems = {
                    AppDestinations.entries.forEach { destination ->
                        val isSelected = destination == currentDestination
                        item(
                            selected = isSelected,
                            onClick = { currentDestination = destination },
                            colors = myItemColors,
                            icon = {
                                Icon(
                                    painter = painterResource(id = destination.icon),
                                    contentDescription = destination.label,
                                    modifier = Modifier.size(30.dp)
                                )
                            },
                            label = { Text(destination.label) }
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
                    )}

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
                            onGoToAuthMenu = {
                                showAuthMenu = true
                            }
                        )
                        AppDestinations.Search -> SearchPage(
                            recipeViewModel = recipeViewModel,
                            historyViewModel = historyViewModel,
                            userViewModel = userViewModel,
                            )
                            AppDestinations.Profile -> UserPage(
                            recipeViewModel = recipeViewModel,
                            userViewModel = userViewModel
                            )
                        }
                        if (showAuthMenu) {
                            AuthMenu(
                            onDismiss = { showAuthMenu = false },
                            userViewModel = userViewModel
                            )
                        }
                    }

                    DashedDivider(
                        color = MaterialTheme.colorScheme.tertiary,
                        strokeWidth = 5.dp,
                        modifier = Modifier
                    )
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