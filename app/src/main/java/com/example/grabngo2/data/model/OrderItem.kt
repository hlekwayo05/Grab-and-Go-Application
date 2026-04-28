// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.model

/** A single line item within an order. */
data class OrderItem(
    val itemId: String = "",
    val name: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val subtotal: Double = 0.0
)
