// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.staff

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.data.model.User
import com.example.grabngo2.ui.components.AuthTextField
import com.example.grabngo2.ui.components.MainButton
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.AuthState
import com.example.grabngo2.ui.viewmodel.AuthViewModel

/**
 * Staff-only authentication screen. Displayed as the entry point of StaffNavGraph.
 * No sign-up option exists. Staff credentials are provisioned by admin only.
 * Contact Admin link directs staff to the administrator for credential issues.
 */
@Composable
fun StaffLoginScreen(
    themeChoice: String = "dark",
    viewModel: AuthViewModel,
    onLoginSuccess: (User) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val staffTeal = Color(0xFF0F6E56)

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess((authState as AuthState.Success).user)
        } else if (authState is AuthState.Error) {
            snackbarHostState.showSnackbar((authState as AuthState.Error).message)
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Staff Portal Badge
            Surface(
                color = staffTeal,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Staff Portal",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Branding
            Text(
                text = "GrabNGo",
                color = PrimaryOrange,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Cafeteria Management",
                color = staffTeal,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "Welcome back, Staff",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Please log in to manage your cafeteria",
                    color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Auth Fields
            AuthTextField(
                label = "STAFF EMAIL",
                value = email,
                onValueChange = { email = it },
                placeholder = "staff@ump.ac.za",
                icon = Icons.Default.Email,
                themeChoice = themeChoice
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "PASSWORD",
                value = password,
                onValueChange = { password = it },
                placeholder = "••••••••",
                icon = Icons.Default.Lock,
                isPassword = true,
                trailingIcon = Icons.Default.Visibility,
                themeChoice = themeChoice
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Log In Button
            MainButton(
                text = "Log In to Dashboard",
                enabled = authState !is AuthState.Loading && email.isNotBlank() && password.isNotBlank(),
                onClick = { viewModel.signInStaff(email, password) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Contact Admin Link
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(
                    text = "Having trouble?",
                    color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = "Contact your administrator",
                    color = staffTeal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:admin@testump.ac.za")
                                putExtra(Intent.EXTRA_SUBJECT, "Staff Account Support Request")
                            }
                            context.startActivity(intent)
                        }
                        .padding(top = 4.dp)
                )
            }
        }

        // Error Feedback for Role Mismatch
        if (authState is AuthState.Error) {
            val errorMsg = (authState as AuthState.Error).message
            if (errorMsg.contains("portal", ignoreCase = true)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .align(Alignment.Center),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                ) {
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Loading Overlay
        if (authState is AuthState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
