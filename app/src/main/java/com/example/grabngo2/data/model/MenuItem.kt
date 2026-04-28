// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.model

/** Menu item belonging to a cafeteria. isAvailable=false hides item from students instantly. */
data class MenuItem(
    val itemId: String = "",
    val cafeteriaId: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "",          // meals | snacks | drinks | combos | desserts
    val price: Double = 0.0,
    val imageUrl: String = "",
    val stockCount: Int = 0,
    val defaultStockCount: Int = 0,
    val isAvailable: Boolean = true,
    val isFeatured: Boolean = false,
    val allergens: List<String> = emptyList()
)
