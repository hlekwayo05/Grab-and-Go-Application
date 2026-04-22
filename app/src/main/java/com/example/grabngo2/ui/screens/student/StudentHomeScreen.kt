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
    onOrderClick: () -> Unit
) {
    Scaffold(
        bottomBar = { StudentBottomBar(currentScreen = "home") }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
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
                    Text("Good afternoon 👋", color = TextGray, fontSize = 14.sp)
                    Text(
                        "What are you hungry for?",
                        color = TextWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 34.sp
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = TextGray)
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

            Text("Categories", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            val categories = listOf("Meals", "Snacks", "Drinks", "Combos", "Desserts")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(categories) { category ->
                    CategoryItem(category, category == "Meals")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Popular Meals", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                PopularMealCard(
                    name = "Curry & Rice",
                    image = "🍛",
                    isHot = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                PopularMealCard(
                    name = "Steak & Chips",
                    image = "🥩",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, isSelected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) PrimaryOrange else CardBackground
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
        Text(name, color = if (isSelected) PrimaryOrange else TextGray, fontSize = 12.sp)
    }
}

@Composable
fun PopularMealCard(name: String, image: String, isHot: Boolean = false, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
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
fun StudentBottomBar(currentScreen: String) {
    NavigationBar(
        containerColor = Color(0xFF1A110A),
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
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
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
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
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
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
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
                unselectedIconColor = TextGray,
                unselectedTextColor = TextGray,
                indicatorColor = Color.Transparent
            )
        )
    }
}
