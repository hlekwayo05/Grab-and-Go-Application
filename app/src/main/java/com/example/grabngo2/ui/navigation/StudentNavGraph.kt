// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.grabngo2.ui.screens.auth.EmailVerifyPendingScreen
import com.example.grabngo2.ui.screens.auth.LoginScreen
import com.example.grabngo2.ui.screens.auth.ResetPasswordScreen
import com.example.grabngo2.ui.screens.auth.SignUpScreen
import com.example.grabngo2.ui.screens.student.StudentHomeScreen
import com.example.grabngo2.ui.screens.student.TrackOrderScreen
import com.example.grabngo2.ui.viewmodel.AuthViewModel

/**
 * Navigation graph for the student portal only.
 * Entry: StudentLogin. On auth success: StudentHome with bottom nav.
 * Staff screens are unreachable from this graph.
 */
sealed class StudentScreen(val route: String) {
    object Login : StudentScreen("student_login")
    object SignUp : StudentScreen("student_signup")
    object ResetPassword : StudentScreen("student_reset_password")
    object EmailVerifyPending : StudentScreen("student_email_verify/{email}")
    object Home : StudentScreen("student_home")
    object Menu : StudentScreen("student_menu/{cafeteriaId}")
    object Cart : StudentScreen("student_cart")
    object TrackOrder : StudentScreen("student_track/{orderId}")
    object OrderHistory : StudentScreen("student_orders")
    object Profile : StudentScreen("student_profile")
}

/**
 * Renders the navigation host for the student portal.
 * 
 * @param navController The navigation controller to manage app navigation.
 * @param themeChoice The current theme preference ("light" or "dark").
 * @param onThemeChange Callback to update the theme preference.
 */
@Composable
fun StudentNavGraph(
    navController: NavHostController,
    themeChoice: String,
    onThemeChange: (String) -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = StudentScreen.Login.route
    ) {
        composable(StudentScreen.Login.route) {
            LoginScreen(
                themeChoice = themeChoice,
                viewModel = authViewModel,
                onBackClick = { /* No back from student login start */ },
                onSignUpClick = { navController.navigate(StudentScreen.SignUp.route) },
                onLoginSuccess = { navController.navigate(StudentScreen.Home.route) },
                onForgotPasswordClick = { navController.navigate(StudentScreen.ResetPassword.route) }
            )
        }
        composable(StudentScreen.SignUp.route) {
            SignUpScreen(
                themeChoice = themeChoice,
                viewModel = authViewModel,
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.popBackStack() },
                onSignUpSuccess = { 
                    val user = authViewModel.getCurrentUser()
                    navController.navigate(StudentScreen.EmailVerifyPending.route.replace("{email}", user?.email ?: ""))
                }
            )
        }
        composable(StudentScreen.ResetPassword.route) {
            ResetPasswordScreen(
                themeChoice = themeChoice,
                viewModel = authViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(StudentScreen.EmailVerifyPending.route) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerifyPendingScreen(
                themeChoice = themeChoice,
                email = email,
                viewModel = authViewModel,
                onVerified = { navController.navigate(StudentScreen.Home.route) },
                onBackToSignIn = { 
                    authViewModel.signOut()
                    navController.navigate(StudentScreen.Login.route) {
                        popUpTo(StudentScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(StudentScreen.Home.route) {
            StudentHomeScreen(
                themeChoice = themeChoice,
                onThemeToggle = {
                    val nextTheme = if (themeChoice == "dark") "light" else "dark"
                    onThemeChange(nextTheme)
                },
                onThemeChange = onThemeChange,
                onOrderClick = { navController.navigate(StudentScreen.TrackOrder.route.replace("{orderId}", "test_order")) }
            )
        }
        composable(StudentScreen.Menu.route) {
            Text("Menu Screen Placeholder for Cafeteria: ${it.arguments?.getString("cafeteriaId")}")
        }
        composable(StudentScreen.Cart.route) {
            Text("Shopping Cart Screen Placeholder")
        }
        composable(StudentScreen.TrackOrder.route) {
            TrackOrderScreen(
                themeChoice = themeChoice,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(StudentScreen.OrderHistory.route) {
            Text("Order History Screen Placeholder")
        }
        composable(StudentScreen.Profile.route) {
            Text("User Profile Screen Placeholder")
        }
    }
}
