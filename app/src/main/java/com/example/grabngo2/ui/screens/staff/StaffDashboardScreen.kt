package com.example.grabngo2.ui.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grabngo2.ui.theme.*
import com.example.grabngo2.ui.screens.student.ThemeDialog

@Composable
fun StaffDashboardScreen(
    themeChoice: String = "dark",
    onThemeChange: (String) -> Unit = {}
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
        bottomBar = { StaffBottomBar(themeChoice = themeChoice) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (themeChoice == "dark") Color(0xFF3D2A1D) else LightIconBg,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Staff Dashboard",
                            color = PrimaryOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = if (themeChoice == "dark") TextGray else LightTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Orders Today", color = MaterialTheme.colorScheme.onBackground, fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("12", "Pending", Color(0xFFFFD700), modifier = Modifier.weight(1f))
                StatCard("5", "Preparing", PrimaryOrange, modifier = Modifier.weight(1f))
                StatCard("28", "Collected", Color(0xFF4CAF50), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Incoming Orders", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(listOf(
                    OrderData("#GNG-0042", "Thabo M.", "2 mins ago", "1x Curry & Rice · 1x Coke 500ml", "Preparing"),
                    OrderData("#GNG-0041", "Lerato K.", "5 mins ago", "2x Boerewors Roll · 1x Fanta", "Preparing")
                )) { order ->
                    StaffOrderCard(order)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
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
        }
    }
}

data class OrderData(val id: String, val name: String, val time: String, val items: String, val status: String)

@Composable
fun StatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun StaffOrderCard(order: OrderData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.id, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Surface(
                    color = PrimaryOrange.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        order.status,
                        color = PrimaryOrange,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF42A5F5), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(order.name, color = TextGray, fontSize = 14.sp)
                Text(" · Placed ${order.time}", color = TextGray.copy(alpha = 0.6f), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(order.items, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Mark Preparing", fontSize = 13.sp)
                }
                Button(
                    onClick = { },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E4D3E)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("✓ Mark Ready", color = Color(0xFF81C784), fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun StaffBottomBar(themeChoice: String = "dark") {
    NavigationBar(
        containerColor = if (themeChoice == "dark") Color(0xFF1A110A) else LightSurface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = true,
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
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.RestaurantMenu, contentDescription = null) },
            label = { Text("Menu") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryOrange,
                selectedTextColor = PrimaryOrange,
                unselectedIconColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                unselectedTextColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.BarChart, contentDescription = null) },
            label = { Text("Stats") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryOrange,
                selectedTextColor = PrimaryOrange,
                unselectedIconColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                unselectedTextColor = if (themeChoice == "dark") TextGray else LightTextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
            label = { Text("Settings") },
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
