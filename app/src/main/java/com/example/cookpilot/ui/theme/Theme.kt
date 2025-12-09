package com.example.cookpilot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColorDark,
    secondary = SecondaryColorDark,
    tertiary = TertiaryColorDark,
    background = Color(0xFF151515),
    surface = PrimaryColorDark,

    onPrimary = OnSurfaceDark,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = OnSurfaceDark,
    onSurface = OnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColorLight,
    secondary = SecondaryColorLight,
    tertiary = TertiaryColorLight,
    background = Color(0xFFF9F9F9),
    surface = PrimaryColorLight,

    onPrimary = OnSurfaceLight,
    onSecondary = OnSurfaceDark,
    onTertiary = OnSurfaceDark,
    onBackground = OnSurfaceLight,
    onSurface = OnSurfaceLight
)

@Composable
fun CookPilotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}