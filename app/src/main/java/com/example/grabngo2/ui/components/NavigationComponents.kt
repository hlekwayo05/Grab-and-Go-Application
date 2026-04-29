// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.grabngo2.ui.theme.*

/**
 * Shared bottom navigation bar for the student portal.
 * 
 * @param currentScreen The identifier of the currently active screen.
 * @param themeChoice The current theme preference ("light" or "dark").
 * @param cartCount The number of items currently in the student's cart.
 * @param onHomeClick Callback for the Home tab.
 * @param onSearchClick Callback for the Search tab.
 * @param onCartClick Callback for the Cart tab.
 * @param onOrdersClick Callback for the Orders tab.
 */
@Composable
fun StudentBottomBar(
    currentScreen: String,
    themeChoice: String = "dark",
    cartCount: Int = 0,
    onHomeClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onCartClick: () -> Unit = {},
    onOrdersClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = if (themeChoice == "dark") Color(0xFF1A110A) else LightSurface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
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
            onClick = onSearchClick,
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
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
            onClick = onCartClick,
            icon = {
                if (cartCount > 0) {
                    BadgedBox(
                        badge = {
                            Badge(containerColor = PrimaryOrange) {
                                Text(cartCount.toString(), color = TextWhite)
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                } else {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
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
            onClick = onOrdersClick,
            icon = { Icon(Icons.AutoMirrored.Filled.Assignment, contentDescription = "Orders") },
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
