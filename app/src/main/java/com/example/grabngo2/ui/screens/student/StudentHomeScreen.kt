// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.screens.student

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.grabngo2.data.model.Cafeteria
import com.example.grabngo2.data.model.MenuItem
import com.example.grabngo2.data.model.User
import com.example.grabngo2.ui.components.StudentBottomBar
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.viewmodel.StudentViewModel
import java.util.*

/**
 * Student Home Screen: displays personalized greetings, cafeteria selection,
 * featured specials, and popular meals.
 */
@Composable
fun StudentHomeScreen(
    themeChoice: String = "dark",
    currentUser: User,
    viewModel: StudentViewModel,
    onThemeToggle: () -> Unit = {},
    onThemeChange: (String) -> Unit = {},
    onOrderClick: () -> Unit,
    onCategoryClick: (String) -> Unit = {},
    onCartClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    var showThemeDialog by remember { mutableStateOf(false) }

    val selectedCafeteriaId by viewModel.selectedCafeteriaId.collectAsStateWithLifecycle()
    val cafeterias by viewModel.cafeterias.collectAsStateWithLifecycle()
    val featuredItem by viewModel.featuredItem.collectAsStateWithLifecycle()
    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    val popularItems by viewModel.menuItems.collectAsStateWithLifecycle()

    val selectedCafeteria = cafeterias.find { it.cafeteriaId == selectedCafeteriaId } ?: Cafeteria()

    // Load student specific data (like active orders) once
    LaunchedEffect(currentUser.userId) {
        viewModel.loadStudentData(currentUser.userId)
    }

    if (showThemeDialog) {
        ThemeDialog(
            currentTheme = themeChoice,
            onDismiss = { showThemeDialog = false },
            onSelect = {
                onThemeChange(it)
                showThemeDialog = false
            }
        )
    }

    Scaffold(
        bottomBar = { 
            StudentBottomBar(
                currentScreen = "home", 
                themeChoice = themeChoice,
                cartCount = cartItemCount,
                onHomeClick = { /* Already here */ },
                onSearchClick = { /* Navigate to search */ },
                onCartClick = onCartClick,
                onOrdersClick = onHistoryClick
            ) 
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Personalized Greeting
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val greeting = when {
                hour < 12 -> "Good morning"
                hour < 17 -> "Good afternoon"
                else -> "Good evening"
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("$greeting, ${currentUser.firstName} 👋", color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 14.sp)
                    Text(
                        "What are you hungry for?",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 34.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (themeChoice == "dark") Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = if (themeChoice == "dark") TextGray else LightTextSecondary
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = if (themeChoice == "dark") TextGray else LightTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Cafeteria Selector Chips
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CafeteriaChip(
                    name = "Main Cafeteria",
                    isSelected = selectedCafeteriaId == "main-caf",
                    onClick = { viewModel.selectCafeteria("main-caf") }
                )
                CafeteriaChip(
                    name = "Snack Bar",
                    isSelected = selectedCafeteriaId == "snack-bar",
                    onClick = { viewModel.selectCafeteria("snack-bar") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Special Offer Banner or Closed Message
            if (!selectedCafeteria.isOpen) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = TextGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Cafeteria closed - Opens at ${selectedCafeteria.openingTime}",
                                color = TextGray,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else if (featuredItem != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(PrimaryOrange, Color(0xFFFF9800))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "🔥 Today's Special",
                                    color = TextWhite,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                )
                            }
                            
                            Column {
                                Text(
                                    "${featuredItem?.name}\nonly R${featuredItem?.price}!",
                                    color = TextWhite,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onOrderClick,
                                    colors = ButtonDefaults.buttonColors(containerColor = TextWhite),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 20.dp)
                                ) {
                                    Text("Order Now", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Categories", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            val categories = listOf("Meals", "Snacks", "Drinks", "Combos", "Desserts")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(categories) { category ->
                    CategoryItem(category, category == "Meals", themeChoice) { onCategoryClick(category) }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Popular Meals", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Displaying first two popular items as cards
            Row(modifier = Modifier.fillMaxWidth()) {
                popularItems.take(2).forEachIndexed { index, item ->
                    if (index > 0) Spacer(modifier = Modifier.width(16.dp))
                    PopularMealCard(
                        item = item,
                        themeChoice = themeChoice,
                        isReadOnly = !selectedCafeteria.isOpen,
                        onAddClick = { viewModel.addToCart(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (popularItems.isEmpty()) {
                    Text("No meals available today.", color = TextGray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text("Appearance", color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showThemeDialog = true },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = PrimaryOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Theme", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                            val themeLabel = when(themeChoice) {
                                "dark" -> "Dark Ember"
                                "light" -> "Campus Light"
                                else -> "System Default"
                            }
                            Text(themeLabel, color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 12.sp)
                        }
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = if (themeChoice == "dark") TextGray else LightTextSecondary)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ThemeDialog(
    currentTheme: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Theme", color = MaterialTheme.colorScheme.onBackground) },
        text = {
            Column {
                ThemeOption("Dark Ember", "dark", currentTheme, onSelect)
                ThemeOption("Campus Light", "light", currentTheme, onSelect)
                ThemeOption("System Default", "system", currentTheme, onSelect)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = PrimaryOrange)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        textContentColor = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun ThemeOption(label: String, value: String, currentTheme: String, onSelect: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onSelect(value) }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = currentTheme == value,
            onClick = { onSelect(value) },
            colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrange)
        )
        Spacer(Modifier.width(12.dp))
        Text(label, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun CafeteriaChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (isSelected) PrimaryOrange else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, TextGray.copy(alpha = 0.3f))
    ) {
        Text(
            text = name,
            color = if (isSelected) TextWhite else TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun CategoryItem(name: String, isSelected: Boolean, themeChoice: String = "dark", onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) PrimaryOrange else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                val icon = when(name) {
                    "Meals" -> "🍽️"
                    "Snacks" -> "🥪"
                    "Drinks" -> "🥤"
                    "Combos" -> "🍱"
                    else -> "🍰"
                }
                Text(icon, fontSize = 24.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, color = if (isSelected) PrimaryOrange else (if (themeChoice == "dark") TextGray else LightTextSecondary), fontSize = 12.sp)
    }
}

@Composable
fun PopularMealCard(
    item: MenuItem, 
    themeChoice: String = "dark", 
    isReadOnly: Boolean = false,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (item.isFeatured) {
                Surface(
                    modifier = Modifier.padding(8.dp).align(Alignment.TopEnd),
                    color = PrimaryOrange,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "🔥 Hot",
                        color = TextWhite,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(if (item.category == "drinks") "🥤" else "🍽️", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.name, 
                    color = MaterialTheme.colorScheme.onBackground, 
                    fontSize = 14.sp, 
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                Text(
                    "R${item.price}", 
                    color = PrimaryOrange, 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Bold
                )
                
                if (!isReadOnly) {
                    Spacer(modifier = Modifier.height(8.dp))
                    IconButton(
                        onClick = onAddClick,
                        modifier = Modifier.size(32.dp).background(PrimaryOrange, CircleShape)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add to cart", tint = TextWhite, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
