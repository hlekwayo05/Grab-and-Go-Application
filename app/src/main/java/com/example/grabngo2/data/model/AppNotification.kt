// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.model

/** Notification document stored in Firestore notifications collection. */
data class AppNotification(
    val notificationId: String = "",
    val userId: String = "",
    val orderId: String = "",
    val type: String = "",
    val title: String = "",
    val body: String = "",
    val isRead: Boolean = false
)
