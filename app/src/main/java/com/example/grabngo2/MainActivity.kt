// GrabNGo | University of Mpumalanga 2026
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
import com.example.grabngo2.ui.navigation.StaffNavGraph
import com.example.grabngo2.ui.navigation.StudentNavGraph
import com.example.grabngo2.ui.theme.Grabngo2Theme
import com.example.grabngo2.ui.theme.ThemePreference

/**
 * MainActivity: determines which portal to show based on a build config flag.
 * STUDENT_PORTAL = true shows StudentNavGraph.
 * STUDENT_PORTAL = false shows StaffNavGraph.
 * In production, two separate APK flavors would be created.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var themeChoice by remember { mutableStateOf(ThemePreference.getTheme(this)) }
            
            Grabngo2Theme(themeChoice = themeChoice) {
                val navController = rememberNavController()
                
                if (STUDENT_PORTAL) {
                    StudentNavGraph(
                        navController = navController,
                        themeChoice = themeChoice,
                        onThemeChange = { newTheme ->
                            themeChoice = newTheme
                            ThemePreference.saveTheme(this, newTheme)
                        }
                    )
                } else {
                    StaffNavGraph(
                        navController = navController,
                        themeChoice = themeChoice,
                        onThemeChange = { newTheme ->
                            themeChoice = newTheme
                            ThemePreference.saveTheme(this, newTheme)
                        }
                    )
                }
            }
        }
    }

    companion object {
        /**
         * Flag to toggle between Student and Staff portals.
         * Set to true for Student Portal, false for Staff Portal.
         */
        const val STUDENT_PORTAL = true
    }
}
