package com.example.cookpilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.example.cookpilot.ui.Components.Header
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

    Header {  }

    NavigationSuiteScaffold(
        containerColor = Color.LightGray.copy(alpha = 0.5f),
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                val isSelected = it == currentDestination

                val selectedColor = SecondaryColor
                val unselectedColor = Color.DarkGray
                item(
                    icon = {
                        CompositionLocalProvider(LocalContentColor provides if (isSelected) selectedColor else unselectedColor)

                        {Icon(
                            painter = painterResource(id = it.icon),
                            contentDescription = it.label,
                            modifier = Modifier.size(30.dp)
                        )}
                    },
                    label = {
                        CompositionLocalProvider(LocalContentColor provides if (isSelected) selectedColor else unselectedColor)
                        {Text(it.label)}
                            },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    History("History", R.drawable.history_tab_icon),
    Create("Create", R.drawable.create_tab_icon),
    Search("Search", R.drawable.search_tab_icon),
}