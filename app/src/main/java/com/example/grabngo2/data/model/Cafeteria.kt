// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.model

/** Represents one of the two cafeterias. cafeteriaId is either main-caf or snack-bar. */
data class Cafeteria(
    val cafeteriaId: String = "",
    val name: String = "",
    val description: String = "",
    val isOpen: Boolean = false,
    val openingTime: String = "07:30",
    val closingTime: String = "17:00",
    val imageUrl: String = ""
)
