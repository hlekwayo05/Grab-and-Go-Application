package com.example.grabngo2.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    onPrimary = TextWhite,
    secondary = SecondaryOrange,
    background = DarkBackground,
    surface = SurfaceColor,
    onBackground = TextWhite,
    onSurface = TextWhite,
    surfaceVariant = CardBackground
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    onPrimary = LightTextPrimary,
    secondary = SecondaryOrange,
    background = LightBackground,
    surface = LightSurface,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    surfaceVariant = LightCardBackground
)

@Composable
fun Grabngo2Theme(
    themeChoice: String = "dark",
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeChoice) {
        "light" -> LightColorScheme
        "dark" -> DarkColorScheme
        "system" -> if (darkTheme) DarkColorScheme else LightColorScheme
        else -> DarkColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            val isLight = if (themeChoice == "system") !darkTheme else themeChoice == "light"
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isLight
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
