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
import com.example.grabngo2.ui.theme.*

@Composable
fun SignUpScreen(
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
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
                    .background(CardBackground, RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                color = Color(0xFF3D2A1D),
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
                    .background(CardBackground, RoundedCornerShape(16.dp))
                    .padding(4.dp)
            ) {
                TextButton(
                    onClick = onLoginClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Log In", color = TextGray)
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
                color = TextWhite,
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
                color = TextGray,
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
                        icon = Icons.Default.Person
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Box(modifier = Modifier.weight(1f)) {
                    AuthTextField(
                        label = "LAST NAME",
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Mokoena",
                        icon = Icons.Default.Person
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "STUDENT NUMBER",
                value = studentNumber,
                onValueChange = { studentNumber = it },
                placeholder = "e.g. 12345678",
                icon = Icons.Default.Style
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "STUDENT EMAIL",
                value = email,
                onValueChange = { email = it },
                placeholder = "you@university.ac.za",
                icon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                label = "PASSWORD",
                value = password,
                onValueChange = { password = it },
                placeholder = "Create a strong password",
                icon = Icons.Default.Lock,
                isPassword = true,
                trailingIcon = Icons.Default.Visibility
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = agreed,
                    onCheckedChange = { agreed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryOrange,
                        uncheckedColor = TextGray,
                        checkmarkColor = TextWhite
                    )
                )
                Text(
                    text = "I agree to the Terms of Service and Privacy Policy of GrabNGo",
                    color = TextGray,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { agreed = !agreed }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            MainButton(
                text = "Create Account",
                onClick = onSignUpSuccess
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
