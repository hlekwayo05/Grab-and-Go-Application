package com.example.grabngo2.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.ui.components.AuthTextField
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.AuthState
import com.example.grabngo2.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordScreen(
    themeChoice: String = "dark",
    viewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            isSuccess = true
            viewModel.clearError()
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

            // Key Icon Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            Text(
                text = "Reset your",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Password",
                color = Color(0xFFFFD700),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isSuccess) 
                    "Check your inbox! We've sent a link to $email"
                    else "No stress! Enter your student email and we'll send you a reset link in seconds.",
                color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Progress Steps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepItem(number = "1", label = "Enter Email", isActive = true, isCompleted = isSuccess)
                Box(modifier = Modifier.weight(1f).height(2.dp).background(if (isSuccess) PrimaryOrange else MaterialTheme.colorScheme.surfaceVariant))
                StepItem(number = "2", label = "Check Inbox", isActive = isSuccess)
                Box(modifier = Modifier.weight(1f).height(2.dp).background(MaterialTheme.colorScheme.surfaceVariant))
                StepItem(number = "3", label = "New Pass", isActive = false)
            }

            Spacer(modifier = Modifier.height(40.dp))

            if (!isSuccess) {
                AuthTextField(
                    label = "STUDENT EMAIL",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "you@university.ac.za",
                    icon = Icons.Default.Email,
                    themeChoice = themeChoice
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📧", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "A reset link will be sent to your university email. Check your inbox and spam folder. Link expires in 15 minutes.",
                            color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE8F5E9).copy(alpha = 0.1f) // Success background hint
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Link Sent Successfully!",
                            color = Color.Green,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please check $email and follow the instructions to set a new password.",
                            color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (!isSuccess) {
                Button(
                    onClick = { 
                        if (email.contains("@")) {
                            viewModel.sendPasswordReset(email)
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("Please enter a valid email") }
                        }
                    },
                    enabled = authState !is AuthState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                    } else {
                        Text("Send Reset Link →", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Back to Log In", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isSuccess) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("← Back to ", color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 14.sp)
                    Text(
                        "Log In",
                        color = PrimaryOrange,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onBackClick() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun StepItem(number: String, label: String, isActive: Boolean, isCompleted: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(24.dp),
            shape = CircleShape,
            color = if (isCompleted) PrimaryOrange else if (isActive) Color(0xFFFFD700) else CardBackground
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Text("✓", color = TextWhite, fontSize = 12.sp)
                } else {
                    Text(number, color = if (isActive) Color.Black else TextGray, fontSize = 12.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label.replace(" ", "\n"),
            color = if (isActive) (if (isCompleted) PrimaryOrange else Color(0xFFFFD700)) else TextGray,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp
        )
    }
}
