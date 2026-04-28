package com.example.grabngo2.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.ui.components.AlignRightText
import com.example.grabngo2.ui.components.AuthTextField
import com.example.grabngo2.ui.components.MainButton
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.AuthState
import com.example.grabngo2.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    themeChoice: String = "dark",
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
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
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎓", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Student Portal",
                        color = PrimaryOrange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Log In", color = TextWhite)
                }
                TextButton(
                    onClick = onSignUpClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Sign Up", color = if (themeChoice == "dark") TextGray else LightTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Welcome",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "back!",
                color = PrimaryOrange,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Log in to your student account",
                color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            AuthTextField(
                label = "STUDENT EMAIL",
                value = email,
                onValueChange = { 
                    email = it
                    emailError = if (it.isNotBlank() && !it.endsWith("@testump.ac.za") && !it.endsWith("@ump.ac.za")) {
                        "Must be a university email (@ump.ac.za)"
                    } else null
                },
                placeholder = "you@university.ac.za",
                icon = Icons.Default.Email,
                themeChoice = themeChoice,
                errorText = emailError
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "PASSWORD",
                value = password,
                onValueChange = { password = it },
                placeholder = "Enter your password",
                icon = Icons.Default.Lock,
                isPassword = true,
                trailingIcon = Icons.Default.Visibility,
                themeChoice = themeChoice
            )

            AlignRightText(
                text = "Forgot Password?",
                onClick = onForgotPasswordClick
            )

            Spacer(modifier = Modifier.weight(1f))

            MainButton(
                text = "Log In",
                enabled = authState !is AuthState.Loading,
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Please enter email and password") }
                    } else if (emailError == null) {
                        viewModel.signInStudent(email, password)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.surfaceVariant)
                    Text("  OR  ", color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 12.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (authState is AuthState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
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
