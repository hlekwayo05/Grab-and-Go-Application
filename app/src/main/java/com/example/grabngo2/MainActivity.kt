package com.example.grabngo2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.grabngo2.ui.navigation.NavGraph
import com.example.grabngo2.ui.theme.Grabngo2Theme
import com.example.grabngo2.ui.theme.ThemePreference

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeChoice by remember { mutableStateOf(ThemePreference.getTheme(this)) }
            Grabngo2Theme(themeChoice = themeChoice) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    themeChoice = themeChoice,
                    onThemeChange = { newTheme ->
                        themeChoice = newTheme
                        ThemePreference.saveTheme(this, newTheme)
                    },
                    onThemeToggle = {
                        val newTheme = if (themeChoice == "dark") "light" else "dark"
                        themeChoice = newTheme
                        ThemePreference.saveTheme(this, newTheme)
                    }
                )
            }
        }
    }
}
