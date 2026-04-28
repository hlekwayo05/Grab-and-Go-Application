// GrabNGo | University of Mpumalanga 2026
// ARCHIVED: replaced by StudentNavGraph.kt and StaffNavGraph.kt
package com.example.grabngo2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.grabngo2.ui.screens.auth.LoginScreen
import com.example.grabngo2.ui.screens.auth.ResetPasswordScreen
import com.example.grabngo2.ui.screens.auth.SignUpScreen
import com.example.grabngo2.ui.screens.auth.WelcomeScreen
import com.example.grabngo2.ui.screens.staff.StaffDashboardScreen
import com.example.grabngo2.ui.screens.student.StudentHomeScreen
import com.example.grabngo2.ui.screens.student.TrackOrderScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object ResetPassword : Screen("reset_password")
    object StudentHome : Screen("student_home")
    object TrackOrder : Screen("track_order")
    object StaffDashboard : Screen("staff_dashboard")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    themeChoice: String = "dark",
    onThemeChange: (String) -> Unit = {},
    onThemeToggle: () -> Unit = {}
) {
    /* ARCHIVED: This navigation graph is no longer used.
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        ... (Rest of the code commented out or ignored)
    }
    */
}
