// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.model

import com.google.firebase.Timestamp

/** Full order document from Firestore orders collection. */
data class Order(
    val orderId: String = "",
    val studentId: String = "",
    val cafeteriaId: String = "",
    val items: List<OrderItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val status: String = "pending",
    val specialInstructions: String = "",
    val estimatedPickupMinutes: Int = 15,
    val collectionToken: String = "",
    val placedAt: Timestamp? = null,
    val cancellationReason: String = ""
)
