// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.model

/** Represents a user in Firestore users collection. Role can be student, staff, or admin. */
data class User(
    val userId: String = "",
    val role: String = "student",       // "student" | "staff" | "admin"
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val studentNumber: String = "",    // null for staff
    val cafeteriaId: String = "",       // assigned for staff only
    val allergens: List<String> = emptyList(),
    val fcmToken: String = "",
    val isVerified: Boolean = false,
    val isActive: Boolean = true,
    val noShowCount: Int = 0,
    val lastVerifiedYear: Int = 0
)
