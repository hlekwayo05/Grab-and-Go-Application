package com.example.grabngo2.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grabngo2.ui.theme.*

@Composable
fun TrackOrderScreen(
    themeChoice: String = "dark",
    onBackClick: () -> Unit
) {
    Scaffold(
        bottomBar = { StudentBottomBar(currentScreen = "orders", themeChoice = themeChoice) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Track Order", color = MaterialTheme.colorScheme.onBackground, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Order Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Order Number", color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 14.sp)
                        Text("#GNG-0042", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TrackStep("Placed", themeChoice = themeChoice, isCompleted = true)
                        TrackStep("Confirmed", themeChoice = themeChoice, isCompleted = true)
                        TrackStep("Preparing", themeChoice = themeChoice, isActive = true)
                        TrackStep("Ready", themeChoice = themeChoice, isCompleted = false)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "13:45",
                            color = PrimaryOrange,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Estimated pickup time • ~8 mins",
                            color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text("Your Order", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))

            OrderItem("Curry & Rice", "x1", "R30", "🍛", themeChoice)
            Spacer(modifier = Modifier.height(12.dp))
            OrderItem("Coke 500ml", "x1", "R15", "🥤", themeChoice)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 18.sp)
                Text("R45", color = PrimaryOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TrackStep(label: String, themeChoice: String = "dark", isCompleted: Boolean = false, isActive: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = if (isCompleted) StatusGreen else if (isActive) PrimaryOrange else (if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isCompleted) {
                    Text("✓", color = TextWhite, fontSize = 16.sp)
                } else if (isActive) {
                    Text("🔥", fontSize = 16.sp)
                } else {
                    Text("📦", fontSize = 16.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            label,
            color = if (isActive) PrimaryOrange else if (isCompleted) StatusGreen else (if (themeChoice == "dark") TextGray else LightTextSecondary),
            fontSize = 12.sp
        )
    }
}

@Composable
fun OrderItem(name: String, qty: String, price: String, icon: String, themeChoice: String = "dark") {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 24.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                Text(qty, color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 14.sp)
            }
            Text(price, color = PrimaryOrange, fontWeight = FontWeight.Bold)
        }
    }
}
