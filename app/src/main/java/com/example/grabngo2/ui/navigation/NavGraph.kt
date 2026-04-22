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
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onStudentClick = { navController.navigate(Screen.Login.route) },
                onStaffClick = { navController.navigate(Screen.StaffDashboard.route) }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpClick = { navController.navigate(Screen.SignUp.route) },
                onLoginSuccess = { navController.navigate(Screen.StudentHome.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ResetPassword.route) }
            )
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.popBackStack() },
                onSignUpSuccess = { navController.navigate(Screen.StudentHome.route) }
            )
        }
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.StudentHome.route) {
            StudentHomeScreen(
                onOrderClick = { navController.navigate(Screen.TrackOrder.route) }
            )
        }
        composable(Screen.TrackOrder.route) {
            TrackOrderScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.StaffDashboard.route) {
            StaffDashboardScreen()
        }
    }
}
