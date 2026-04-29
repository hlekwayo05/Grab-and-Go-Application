// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.data.model.MenuItem
import com.example.grabngo2.data.model.User
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.OrderPlacementState
import com.example.grabngo2.ui.viewmodel.StudentViewModel

/**
 * Checkout screen showing all cart items. Student confirms order here.
 * Allergen warning fires if cart items conflict with student allergen profile.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    currentUser: User,
    viewModel: StudentViewModel,
    themeChoice: String = "dark",
    onBackClick: () -> Unit,
    onOrderSuccess: (String) -> Unit
) {
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val allItems by viewModel.menuItems.collectAsStateWithLifecycle()
    val orderState by viewModel.orderState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var kitchenNotes by remember { mutableStateOf("") }
    var allergenAcknowledged by remember { mutableStateOf(false) }

    // Derived state for cart items with full details
    val cartItems = remember(cart, allItems) {
        cart.mapNotNull { (itemId, quantity) ->
            allItems.find { it.itemId == itemId }?.let { it to quantity }
        }
    }

    val subtotal = cartItems.sumOf { it.first.price * it.second }
    val total = subtotal // Pick Up is free

    // Allergen detection logic
    val allergenConflicts = remember(cartItems, currentUser.allergens) {
        cartItems.flatMap { (item, _) ->
            val intersection = item.allergens.filter { it.lowercase() in currentUser.allergens.map { a -> a.lowercase() } }
            intersection.map { allergen -> "${item.name} contains $allergen — you have a $allergen allergy" }
        }.distinct()
    }

    val hasSoldOut = cartItems.any { !it.first.isAvailable || it.first.stockCount <= 0 }

    LaunchedEffect(orderState) {
        if (orderState is OrderPlacementState.Success) {
            onOrderSuccess((orderState as OrderPlacementState.Success).orderId)
            viewModel.clearOrderState()
        } else if (orderState is OrderPlacementState.Error) {
            snackbarHostState.showSnackbar((orderState as OrderPlacementState.Error).message)
            viewModel.clearOrderState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("My Cart", fontWeight = FontWeight.Bold)
                        if (cart.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = PrimaryOrange,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = cart.values.sum().toString(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 16.dp,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .navigationBarsPadding()
                    ) {
                        Button(
                            onClick = { viewModel.placeOrder(currentUser.userId, kitchenNotes) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                            shape = RoundedCornerShape(16.dp),
                            enabled = orderState !is OrderPlacementState.Loading && 
                                      !hasSoldOut && 
                                      (allergenConflicts.isEmpty() || allergenAcknowledged)
                        ) {
                            if (orderState is OrderPlacementState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Place Order • R$total", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (cart.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No items in cart", color = TextGray, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBackClick,
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
                // Allergen Warning Section
                if (allergenConflicts.isNotEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFF9800).copy(alpha = 0.1f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF9800)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF9800))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text("Allergen Warning", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                allergenConflicts.forEach { conflict ->
                                    Text("• $conflict", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = allergenAcknowledged,
                                        onCheckedChange = { allergenAcknowledged = it },
                                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFF9800))
                                    )
                                    Text(
                                        "I acknowledge this allergen warning",
                                        fontSize = 12.sp,
                                        modifier = Modifier.clickable { allergenAcknowledged = !allergenAcknowledged }
                                    )
                                }
                            }
                        }
                    }
                }

                // Cart Items
                items(cartItems) { (item, quantity) ->
                    CartItemRow(
                        item = item,
                        quantity = quantity,
                        onIncrement = { viewModel.addToCart(item) },
                        onDecrement = { viewModel.updateCartQuantity(item.itemId, quantity - 1) },
                        onRemove = { viewModel.removeFromCart(item.itemId) }
                    )
                }

                // Kitchen Notes
                item {
                    Column {
                        Text(
                            "Kitchen notes (optional)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (themeChoice == "dark") TextGray else LightTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = kitchenNotes,
                            onValueChange = { if (it.length <= 120) kitchenNotes = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Dietary needs only e.g. no onion", fontSize = 14.sp) },
                            shape = RoundedCornerShape(16.dp),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryOrange,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                        Text(
                            text = "${kitchenNotes.length}/120",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                            fontSize = 10.sp,
                            color = TextGray
                        )
                    }
                }

                // Order Summary
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            SummaryRow("Subtotal", "R$subtotal")
                            SummaryRow("Collection", "Pick Up (Free)")
                            SummaryRow("Delivery", "Coming soon", isGreyed = true)
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant)
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("TOTAL", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                Text("R$total", color = PrimaryOrange, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                            }
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: MenuItem,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    val isSoldOut = !item.isAvailable || item.stockCount <= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Box {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Item Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            if (isSoldOut) Color.Gray.copy(alpha = 0.2f) else PrimaryOrange.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = if (item.category == "drinks") "🥤" else "🍽️", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("R${item.price}", color = PrimaryOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Quantity Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDecrement,
                            modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.background, CircleShape)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                        Text(quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold)
                        IconButton(
                            onClick = onIncrement,
                            enabled = !isSoldOut,
                            modifier = Modifier.size(24.dp).background(if (isSoldOut) Color.Transparent else MaterialTheme.colorScheme.background, CircleShape)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    IconButton(onClick = onRemove) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.7f))
                    }
                    Text("R${item.price * quantity}", fontWeight = FontWeight.Bold)
                }
            }

            if (isSoldOut) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Red.copy(alpha = 0.1f))
                        .padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        color = Color.Red,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "SOLD OUT",
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, isGreyed: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = if (isGreyed) TextGray else MaterialTheme.colorScheme.onSurface)
        Text(value, fontWeight = if (isGreyed) FontWeight.Normal else FontWeight.Bold, color = if (isGreyed) TextGray else MaterialTheme.colorScheme.onSurface)
    }
}
