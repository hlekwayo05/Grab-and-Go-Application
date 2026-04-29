// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.repository

import com.example.grabngo2.data.model.Cafeteria
import com.example.grabngo2.data.model.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Provides real-time Firestore listeners for menu items per cafeteria.
 */
class MenuRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Returns a real-time flow of available menu items for a specific cafeteria.
     * Filters for items where isAvailable is true.
     */
    fun getMenuItemsByCafeteria(cafeteriaId: String): Flow<List<MenuItem>> {
        return firestore.collection("menuItems")
            .whereEqualTo("cafeteriaId", cafeteriaId)
            .whereEqualTo("isAvailable", true)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject<MenuItem>() }
            }
    }

    /**
     * Returns a real-time flow of a single cafeteria document.
     */
    fun getCafeteria(cafeteriaId: String): Flow<Cafeteria> {
        return firestore.collection("cafeterias")
            .document(cafeteriaId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject<Cafeteria>() ?: Cafeteria()
            }
    }

    /**
     * Returns a real-time flow of all cafeteria documents.
     */
    fun getAllCafeterias(): Flow<List<Cafeteria>> {
        return firestore.collection("cafeterias")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject<Cafeteria>() }
            }
    }
}
