// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.repository

import com.example.grabngo2.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

/**
 * Reads and writes user profile data from the Firestore users collection.
 */
class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Fetches the user profile document for the given UID.
     */
    suspend fun getUserProfile(uid: String): Result<User> {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject<User>() ?: throw Exception("User not found")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Updates the FCM token for the specified user.
     */
    suspend fun updateFcmToken(uid: String, token: String) {
        try {
            firestore.collection("users").document(uid).update("fcmToken", token).await()
        } catch (e: Exception) {
            // Silently handle or log failure
        }
    }
}
