package com.example.cookpilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.cookpilot.ui.Components.HeaderApp
import com.example.cookpilot.ui.theme.CookPilotTheme
import com.example.cookpilot.ui.theme.SecondaryColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CookPilotTheme {
                CookPilotApp()
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun CookPilotApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.History) }
    val myItemColors = NavigationSuiteDefaults.itemColors(
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            selectedIconColor = SecondaryColor,
            selectedTextColor = SecondaryColor,
            unselectedIconColor = Color.DarkGray,
            unselectedTextColor = Color.DarkGray,
            indicatorColor = Color.Transparent
        )
    )
    NavigationSuiteScaffold(
        containerColor = Color.LightGray.copy(alpha = 0.5f),
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
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
            HeaderApp {}
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (currentDestination) {
                    AppDestinations.History -> Text("Pantalla Historial", style = MaterialTheme.typography.headlineMedium)
                    AppDestinations.Create -> Text("Pantalla Crear", style = MaterialTheme.typography.headlineMedium)
                    AppDestinations.Search -> Text("Pantalla Buscar", style = MaterialTheme.typography.headlineMedium)
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
    Create("Create", R.drawable.ic_search_tab),
    Search("Search", R.drawable.ic_search_tab),
}