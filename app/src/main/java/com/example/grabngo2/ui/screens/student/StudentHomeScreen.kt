package com.example.grabngo2.ui.screens.student

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grabngo2.ui.theme.*

@Composable
fun StudentHomeScreen(
    themeChoice: String = "dark",
    onThemeToggle: () -> Unit = {},
    onThemeChange: (String) -> Unit = {},
    onOrderClick: () -> Unit
) {
    var showThemeDialog by remember { mutableStateOf(false) }

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
        bottomBar = { StudentBottomBar(currentScreen = "home", themeChoice = themeChoice) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Good afternoon 👋", color = if (themeChoice == "dark") TextGray else LightTextSecondary, fontSize = 14.sp)
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

            Spacer(modifier = Modifier.height(24.dp))

            // Special Offer Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
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
                                "Pap & Chakalaka\nonly R25!",
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

            Spacer(modifier = Modifier.height(32.dp))

            Text("Categories", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            val categories = listOf("Meals", "Snacks", "Drinks", "Combos", "Desserts")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(categories) { category ->
                    CategoryItem(category, category == "Meals", themeChoice)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Popular Meals", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                PopularMealCard(
                    name = "Curry & Rice",
                    image = "🍛",
                    isHot = true,
                    themeChoice = themeChoice,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                PopularMealCard(
                    name = "Steak & Chips",
                    image = "🥩",
                    themeChoice = themeChoice,
                    modifier = Modifier.weight(1f)
                )
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
fun CategoryItem(name: String, isSelected: Boolean, themeChoice: String = "dark") {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) PrimaryOrange else MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Placeholder icons
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
fun PopularMealCard(name: String, image: String, isHot: Boolean = false, themeChoice: String = "dark", modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isHot) {
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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(image, fontSize = 48.sp)
            }
        }
    }
}

@Composable
fun StudentBottomBar(currentScreen: String, themeChoice: String = "dark") {
    NavigationBar(
        containerColor = if (themeChoice == "dark") Color(0xFF1A110A) else LightSurface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryOrange,
                selectedTextColor = PrimaryOrange,
                unselectedIconColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                unselectedTextColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentScreen == "search",
            onClick = { },
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryOrange,
                selectedTextColor = PrimaryOrange,
                unselectedIconColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                unselectedTextColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentScreen == "cart",
            onClick = { },
            icon = { 
                BadgedBox(badge = { Badge { Text("2") } }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                }
            },
            label = { Text("Cart") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryOrange,
                selectedTextColor = PrimaryOrange,
                unselectedIconColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                unselectedTextColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = currentScreen == "orders",
            onClick = { },
            icon = { Icon(Icons.Default.Assignment, contentDescription = null) },
            label = { Text("Orders") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryOrange,
                selectedTextColor = PrimaryOrange,
                unselectedIconColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                unselectedTextColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                indicatorColor = Color.Transparent
            )
        )
    }
}
