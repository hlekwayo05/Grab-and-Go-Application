// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

/**
 * Shown after student signs up. Student cannot proceed until email is verified.
 * Polls Firebase every 10 seconds to check if email has been verified.
 */
@Composable
fun EmailVerifyPendingScreen(
    themeChoice: String = "dark",
    email: String,
    viewModel: AuthViewModel,
    onVerified: () -> Unit,
    onBackToSignIn: () -> Unit
) {
    // Polling logic
    LaunchedEffect(Unit) {
        while (true) {
            viewModel.reloadUser { isVerified ->
                if (isVerified) {
                    onVerified()
                }
            }
            delay(10000) // Poll every 10 seconds
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Check your inbox",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "We sent a verification link to\n$email",
                color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = PrimaryOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Check your spam folder if you don't see it. The link expires in 24 hours.",
                        color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = { viewModel.resendVerificationEmail() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Resend Email", color = TextWhite, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(onClick = onBackToSignIn) {
                Text(
                    text = "Back to Sign In",
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
