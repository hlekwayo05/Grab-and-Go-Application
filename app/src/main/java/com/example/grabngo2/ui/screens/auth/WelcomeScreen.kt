package com.example.grabngo2.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grabngo2.ui.theme.*

@Composable
fun WelcomeScreen(
    themeChoice: String = "dark",
    onStudentClick: () -> Unit,
    onStaffClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = if (themeChoice == "dark") {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2D1B10),
                            DarkBackground
                        ),
                        startY = 0f,
                        endY = 1000f
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFEBDD),
                            LightBackground
                        ),
                        startY = 0f,
                        endY = 1000f
                    )
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))
            
            // Placeholder for Logo
            Box(modifier = Modifier.height(100.dp)) {
                // Option to add logo later
                // Image(painter = painterResource(id = R.drawable.logo), contentDescription = null)
            }

            Text(
                text = "Welcome to",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "GrabNGo",
                color = PrimaryOrange,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Skip the queue. Order ahead.",
                color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "WHO ARE YOU?",
                color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            RoleCard(
                title = "Student",
                description = "Browse the menu, order food & track your pickup time",
                icon = "🎓",
                themeChoice = themeChoice,
                onClick = onStudentClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleCard(
                title = "Cafeteria Staff",
                description = "Manage orders, update the menu & mark orders ready",
                icon = "👨‍🍳",
                themeChoice = themeChoice,
                onClick = onStaffClick
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "By continuing you agree to our Terms of Service & Privacy Policy",
                color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: String,
    themeChoice: String = "dark",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = icon, fontSize = 24.sp)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = if (themeChoice == "dark") TextGray else LightTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
