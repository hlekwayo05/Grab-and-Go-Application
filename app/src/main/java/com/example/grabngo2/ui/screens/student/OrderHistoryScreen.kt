// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.student

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.data.model.Order
import com.example.grabngo2.ui.components.StudentBottomBar
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.StudentViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Chronological list of all student orders. Active orders at top with live status.
 * Tapping an active order opens Order Tracking. Tapping past order shows read-only detail.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    viewModel: StudentViewModel,
    themeChoice: String = "dark",
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    onTrackOrder: (String) -> Unit
) {
    val history by viewModel.fullOrderHistory.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    
    var selectedOrderForDetail by remember { mutableStateOf<Order?>(null) }

    val activeOrders = history.filter { it.status.lowercase() !in listOf("collected", "cancelled") }
    val pastOrders = history.filter { it.status.lowercase() in listOf("collected", "cancelled") }

    Scaffold(
        bottomBar = {
            StudentBottomBar(
                currentScreen = "orders",
                themeChoice = themeChoice,
                cartCount = cartCount,
                onHomeClick = onHomeClick,
                onSearchClick = onSearchClick,
                onCartClick = onCartClick
            )
        }
    ) { padding ->
        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No orders yet", color = TextGray, fontSize = 18.sp)
                    Text("Place your first order!", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onHomeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Browse Menu")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Orders",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (activeOrders.isNotEmpty()) {
                    item {
                        Text(
                            "Active Orders",
                            color = PrimaryOrange,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(activeOrders) { order ->
                        OrderCard(
                            order = order,
                            themeChoice = themeChoice,
                            onClick = { onTrackOrder(order.orderId) }
                        )
                    }
                }

                if (pastOrders.isNotEmpty()) {
                    item {
                        Text(
                            "Past Orders",
                            color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    items(pastOrders) { order ->
                        OrderCard(
                            order = order,
                            themeChoice = themeChoice,
                            onClick = { selectedOrderForDetail = order }
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }

        if (selectedOrderForDetail != null) {
            PastOrderDetailBottomSheet(
                order = selectedOrderForDetail!!,
                onDismiss = { selectedOrderForDetail = null }
            )
        }
    }
}

/**
 * Individual order card with status badge and summary.
 */
@Composable
fun OrderCard(
    order: Order,
    themeChoice: String,
    onClick: () -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }
    val dateString = order.placedAt?.toDate()?.let { dateFormatter.format(it) } ?: "Just now"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = PrimaryOrange.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "#GNG-${order.orderId.takeLast(4).uppercase()}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = PrimaryOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(status = order.status, isActive = order.status.lowercase() !in listOf("collected", "cancelled"))
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = order.cafeteriaId.replace("-", " ").replaceFirstChar { it.uppercase() },
                    color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                    fontSize = 12.sp
                )
                
                Text(
                    text = order.items.joinToString { "${it.quantity}x ${it.name}" },
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = dateString,
                    color = if (themeChoice == "dark") TextGray else LightTextSecondary,
                    fontSize = 12.sp
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "R${order.totalAmount}",
                    color = PrimaryOrange,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = if (themeChoice == "dark") TextGray else LightTextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

/**
 * Colorful status pill for order states.
 */
@Composable
fun StatusBadge(status: String, isActive: Boolean) {
    val statusLower = status.lowercase()
    val bgColor = when (statusLower) {
        "pending" -> Color.Gray.copy(alpha = 0.2f)
        "confirmed" -> Color.Blue.copy(alpha = 0.2f)
        "preparing" -> Color(0xFFFFD700).copy(alpha = 0.2f) // Amber
        "ready" -> StatusGreen.copy(alpha = 0.2f)
        "collected" -> Color.DarkGray.copy(alpha = 0.2f)
        "cancelled" -> Color.Red.copy(alpha = 0.2f)
        else -> Color.Gray.copy(alpha = 0.2f)
    }
    val textColor = when (statusLower) {
        "pending" -> Color.Gray
        "confirmed" -> Color.Blue
        "preparing" -> Color(0xFFB8860B)
        "ready" -> StatusGreen
        "collected" -> Color.DarkGray
        "cancelled" -> Color.Red
        else -> Color.Gray
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        modifier = if (statusLower == "ready" && isActive) Modifier.alpha(alpha) else Modifier
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Read-only bottom sheet for past order details.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastOrderDetailBottomSheet(
    order: Order,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
                .fillMaxWidth()
        ) {
            Text(
                "Order Summary",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                "#GNG-${order.orderId.uppercase()}",
                fontSize = 14.sp,
                color = PrimaryOrange,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${item.quantity}x ${item.name}", modifier = Modifier.weight(1f))
                    Text("R${item.subtotal}", fontWeight = FontWeight.Bold)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Paid", fontWeight = FontWeight.Bold)
                Text("R${order.totalAmount}", color = PrimaryOrange, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            }
            
            if (order.status == "cancelled" && order.cancellationReason.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = Color.Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Reason for cancellation:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                        Text(order.cancellationReason, fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Close", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}
