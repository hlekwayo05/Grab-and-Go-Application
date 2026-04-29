// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.data.model.Order
import com.example.grabngo2.data.model.User
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.StudentViewModel
import kotlinx.coroutines.launch

/**
 * Real-time order tracking screen. Uses Firestore listener on the specific order document.
 * Status tracker updates automatically as staff advances the order.
 */
@Composable
fun TrackOrderScreen(
    orderId: String,
    viewModel: StudentViewModel,
    currentUser: User,
    themeChoice: String = "dark",
    onBackClick: () -> Unit,
    onCancelSuccess: () -> Unit
) {
    val order by viewModel.getOrderById(orderId).collectAsStateWithLifecycle(initialValue = Order())
    var showCancelDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancel Order?") },
            text = { Text("Are you sure? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.cancelOrder(orderId)
                        showCancelDialog = false
                        onCancelSuccess()
                    }
                ) {
                    Text("Yes, Cancel", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("No, keep it")
                }
            }
        )
    }

    Scaffold(
        bottomBar = { StudentBottomBar(currentScreen = "orders", themeChoice = themeChoice) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
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

            // Running Late Banner
            if (order.estimatedPickupMinutes > 20 && order.status != "ready") {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800))
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Updated estimate received. We're running a bit behind!",
                            color = Color(0xFFE65100),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

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
                        Text("Order ID", color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 14.sp)
                        Text("#${orderId.takeLast(6).uppercase()}", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Progress Tracker
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val status = order.status.lowercase()
                        TrackStep(
                            "Placed", 
                            themeChoice = themeChoice, 
                            isCompleted = status != "pending" && status != "cancelled"
                        )
                        TrackStep(
                            "Confirmed", 
                            themeChoice = themeChoice, 
                            isCompleted = status in listOf("confirmed", "preparing", "ready", "collected"),
                            isActive = status == "pending" // Next step
                        )
                        TrackStep(
                            "Preparing", 
                            themeChoice = themeChoice, 
                            isCompleted = status in listOf("ready", "collected"),
                            isActive = status == "preparing"
                        )
                        TrackStep(
                            "Ready", 
                            themeChoice = themeChoice, 
                            isCompleted = status == "collected",
                            isActive = status == "ready"
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (order.status == "ready") {
                            Text(
                                "READY!",
                                color = StatusGreen,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                "Head to the pickup counter",
                                color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                                fontSize = 14.sp
                            )
                        } else if (order.status == "cancelled") {
                            Text(
                                "CANCELLED",
                                color = Color.Red,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                "${order.estimatedPickupMinutes} min",
                                color = PrimaryOrange,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Estimated pickup time",
                                color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Collection Token / QR Placeholder
            if (order.status == "ready" && order.collectionToken.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryOrange.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryOrange)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Show this to the cafeteria", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = order.collectionToken,
                                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                letterSpacing = 4.sp,
                                color = PrimaryOrange
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = { 
                                scope.launch {
                                    snackbarHostState.showSnackbar("Reminder sent")
                                }
                            }
                        ) {
                            Text("Notify Me Again", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text("Your Order", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))

            order.items.forEach { item ->
                OrderItem(
                    name = item.name,
                    qty = "x${item.quantity}",
                    price = "R${item.subtotal}",
                    icon = if (item.name.contains("Coke", true)) "🥤" else "🍽️",
                    themeChoice = themeChoice
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Paid", color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 18.sp)
                Text("R${order.totalAmount}", color = PrimaryOrange, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // Cancel Button
            if (order.status == "pending" || order.status == "confirmed") {
                Spacer(modifier = Modifier.height(48.dp))
                OutlinedButton(
                    onClick = { showCancelDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                ) {
                    Text("Cancel Order", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

/**
 * Single step in the order tracking progress bar.
 */
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
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Row displaying an individual order item in the tracking screen.
 */
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
