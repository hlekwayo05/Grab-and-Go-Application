// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.grabngo2.data.model.MenuItem
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.StudentViewModel

/**
 * Full scrollable menu for the selected cafeteria.
 * Sticky category tabs filter items in real time.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    cafeteriaId: String,
    viewModel: StudentViewModel,
    themeChoice: String = "dark",
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val menuItems by viewModel.menuItems.collectAsStateWithLifecycle()
    val cafeterias by viewModel.cafeterias.collectAsStateWithLifecycle()
    val cartCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    
    val cafeteria = cafeterias.find { it.cafeteriaId == cafeteriaId }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedItemForDetail by remember { mutableStateOf<MenuItem?>(null) }
    
    val categories = listOf("All", "Meals", "Snacks", "Drinks", "Combos", "Desserts")
    
    // Client-side filtering
    val filteredItems = remember(menuItems, selectedCategory, searchQuery) {
        menuItems.filter { item ->
            val matchesCategory = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = item.name.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        cafeteria?.name ?: "Menu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Expand search bar */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            if (cartCount > 0) {
                FloatingActionButton(
                    onClick = onCartClick,
                    containerColor = PrimaryOrange,
                    contentColor = TextWhite,
                    shape = CircleShape
                ) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = Color.White) {
                                Text(cartCount.toString(), color = PrimaryOrange)
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "View Cart")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Sticky Category Tabs
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 2.dp
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    items(categories) { category ->
                        CategoryTab(
                            name = category,
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            // Menu List
            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No items available in this category",
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredItems) { item ->
                        MenuItemCard(
                            item = item,
                            themeChoice = themeChoice,
                            onAddClick = { viewModel.addToCart(item) },
                            onItemClick = { selectedItemForDetail = item }
                        )
                    }
                }
            }
        }

        // Item Detail Bottom Sheet
        if (selectedItemForDetail != null) {
            ItemDetailBottomSheet(
                item = selectedItemForDetail!!,
                onDismiss = { selectedItemForDetail = null },
                onAddToCart = { quantity ->
                    repeat(quantity) { viewModel.addToCart(selectedItemForDetail!!) }
                    selectedItemForDetail = null
                }
            )
        }
    }
}

/**
 * Category tab with underline indicator.
 */
@Composable
fun CategoryTab(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            color = if (isSelected) PrimaryOrange else TextGray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(2.dp)
                    .background(PrimaryOrange, RoundedCornerShape(2.dp))
            )
        }
    }
}

/**
 * Individual menu item card.
 */
@Composable
fun MenuItemCard(
    item: MenuItem,
    themeChoice: String,
    onAddClick: () -> Unit,
    onItemClick: () -> Unit
) {
    val isSoldOut = !item.isAvailable || item.stockCount <= 0
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isSoldOut) 0.5f else 1f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        if (isSoldOut) TextGray.copy(alpha = 0.2f) else PrimaryOrange.copy(alpha = 0.1f),
                        RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (item.category.equals("drinks", true)) "🥤" else "🍽️",
                    fontSize = 28.sp,
                    modifier = Modifier.background(Color.Transparent)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isSoldOut) TextGray else MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (item.isFeatured) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = PrimaryOrange,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "HOT",
                                color = TextWhite,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = item.description,
                    color = TextGray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "R${item.price}",
                        color = if (isSoldOut) TextGray else PrimaryOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    if (item.stockCount in 1..10) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${item.stockCount} left",
                            color = Color.Red,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            if (isSoldOut) {
                Surface(
                    color = Color.Red,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Sold Out",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = { onAddClick() },
                    modifier = Modifier
                        .size(36.dp)
                        .background(PrimaryOrange, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = TextWhite, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

/**
 * Bottom sheet displaying full item details and quantity selector.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailBottomSheet(
    item: MenuItem,
    onDismiss: () -> Unit,
    onAddToCart: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    var quantity by remember { mutableIntStateOf(1) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Image/Emoji
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(PrimaryOrange.copy(alpha = 0.05f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (item.category.equals("drinks", true)) "🥤" else "🍽️", fontSize = 64.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = item.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "R${item.price}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = item.description,
                    fontSize = 14.sp,
                    color = TextGray
                )
            }

            if (item.allergens.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item.allergens.forEach { allergen ->
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = allergen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                color = TextGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Quantity Selector
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = PrimaryOrange)
                }
                
                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(
                    onClick = { quantity++ },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = PrimaryOrange)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onAddToCart(quantity) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(16.dp),
                enabled = item.isAvailable
            ) {
                Text(
                    text = if (item.isAvailable) "Add to Cart • R${item.price * quantity}" else "Currently Unavailable",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextWhite
                )
            }
        }
    }
}
