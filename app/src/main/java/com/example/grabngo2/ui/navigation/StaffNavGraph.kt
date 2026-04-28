// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grabngo2.ui.screens.staff.StaffDashboardScreen
import com.example.grabngo2.ui.screens.staff.StaffLoginScreen
import com.example.grabngo2.ui.viewmodel.AuthViewModel

/**
 * Navigation graph for the staff portal only.
 * Entry: StaffLogin. On auth success: StaffDashboard with staff bottom nav.
 * Student sign-up is unreachable from this graph.
 */
sealed class StaffScreen(val route: String) {
    object Login : StaffScreen("staff_login")
    object Dashboard : StaffScreen("staff_dashboard")
    object MenuManagement : StaffScreen("staff_menu")
    object Statistics : StaffScreen("staff_stats")
    object Settings : StaffScreen("staff_settings")
}

/**
 * Renders the navigation host for the staff portal.
 * 
 * @param navController The navigation controller to manage app navigation.
 * @param themeChoice The current theme preference ("light" or "dark").
 * @param onThemeChange Callback to update the theme preference.
 */
@Composable
fun StaffNavGraph(
    navController: NavHostController,
    themeChoice: String,
    onThemeChange: (String) -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = StaffScreen.Login.route
    ) {
        composable(StaffScreen.Login.route) {
            StaffLoginScreen(
                themeChoice = themeChoice,
                viewModel = authViewModel,
                onLoginSuccess = { user ->
                    navController.navigate(StaffScreen.Dashboard.route) {
                        popUpTo(StaffScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(StaffScreen.Dashboard.route) {
            StaffDashboardScreen(
                themeChoice = themeChoice,
                onThemeChange = onThemeChange
            )
        }
        composable(StaffScreen.MenuManagement.route) {
            Text("Menu Management Screen Placeholder")
        }
        composable(StaffScreen.Statistics.route) {
            Text("Statistics Screen Placeholder")
        }
        composable(StaffScreen.Settings.route) {
            Text("Staff Settings Screen Placeholder")
        }
    }
}
