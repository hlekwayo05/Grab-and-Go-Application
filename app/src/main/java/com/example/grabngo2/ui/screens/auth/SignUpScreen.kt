package com.example.grabngo2.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Style
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
import com.example.grabngo2.ui.components.AuthTextField
import com.example.grabngo2.ui.components.MainButton
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.AuthState
import com.example.grabngo2.ui.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    themeChoice: String = "dark",
    viewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onLoginClick: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var studentNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agreed by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var studentNumberError by remember { mutableStateOf<String?>(null) }
    
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onSignUpSuccess()
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
                .verticalScroll(rememberScrollState())
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
                TextButton(
                    onClick = onLoginClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Log In", color = if (themeChoice == "dark") TextGray else LightTextSecondary)
                }
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sign Up", color = TextWhite)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Create your",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "account",
                color = PrimaryOrange,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Join thousands of students on campus",
                color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) {
                    AuthTextField(
                        label = "FIRST NAME",
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "Thabo",
                        icon = Icons.Default.Person,
                        themeChoice = themeChoice
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    AuthTextField(
                        label = "LAST NAME",
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Mokoena",
                        icon = Icons.Default.Person,
                        themeChoice = themeChoice
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "STUDENT NUMBER",
                value = studentNumber,
                onValueChange = { 
                    if (it.length <= 8) {
                        studentNumber = it
                        studentNumberError = if (it.length > 0 && it.length < 8) "Must be 8 digits" else null
                    }
                },
                placeholder = "e.g. 12345678",
                icon = Icons.Default.Style,
                themeChoice = themeChoice,
                errorText = studentNumberError
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                placeholder = "Create a strong password",
                icon = Icons.Default.Lock,
                isPassword = true,
                trailingIcon = Icons.Default.Visibility,
                themeChoice = themeChoice
            )
            
            if (password.isNotEmpty()) {
                PasswordStrengthMeter(password)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = agreed,
                    onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryOrange,
                        uncheckedColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                        checkmarkColor = TextWhite
                    )
                )
                Text(
                    text = "I agree to the Terms of Service and Privacy Policy of GrabNGo",
                    color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { agreed = !agreed }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            MainButton(
                text = "Create Account",
                enabled = agreed && authState !is AuthState.Loading && emailError == null && studentNumberError == null && firstName.isNotBlank() && lastName.isNotBlank() && studentNumber.length == 8 && email.isNotBlank() && password.length >= 6,
                onClick = {
                    viewModel.signUpStudent(email, password, firstName, lastName, studentNumber)
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
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

@Composable
fun PasswordStrengthMeter(password: String) {
    val strength = remember(password) {
        when {
            password.length < 8 -> "Weak"
            password.any { it.isDigit() } && password.any { !it.isLetterOrDigit() } -> "Strong"
            password.any { it.isDigit() } -> "Fair"
            else -> "Weak"
        }
    }
    val color = when (strength) {
        "Weak" -> Color.Red
        "Fair" -> Color(0xFFFFA500) // Amber
        "Strong" -> Color.Green
        else -> Color.Gray
    }

    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Strength: ", fontSize = 12.sp, color = TextGray)
        Text(strength, fontSize = 12.sp, color = color, fontWeight = FontWeight.Bold)
    }
}
