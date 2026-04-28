// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.repository

import com.example.grabngo2.data.model.Order
import com.example.grabngo2.data.model.OrderItem
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Handles order creation and real-time order tracking from Firestore.
 * Note: Order status changes go through Cloud Functions, not direct Firestore writes.
 */
class OrderRepository {

    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Writes a new order document to the Firestore orders collection.
     * Sets initial status to "pending" and uses server timestamp for placedAt.
     */
    suspend fun placeOrder(
        studentId: String,
        cafeteriaId: String,
        items: List<OrderItem>,
        specialInstructions: String
    ): Result<String> {
        return try {
            val orderRef = firestore.collection("orders").document()
            val totalAmount = items.sumOf { it.subtotal }
            
            val orderData = mapOf(
                "orderId" to orderRef.id,
                "studentId" to studentId,
                "cafeteriaId" to cafeteriaId,
                "items" to items,
                "totalAmount" to totalAmount,
                "status" to "pending",
                "specialInstructions" to specialInstructions,
                "placedAt" to FieldValue.serverTimestamp()
            )

            orderRef.set(orderData).await()
            Result.success(orderRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Returns a real-time flow of active orders for a student.
     * Active orders are those that are not yet collected or cancelled.
     */
    fun getActiveOrders(studentId: String): Flow<List<Order>> {
        return firestore.collection("orders")
            .whereEqualTo("studentId", studentId)
            .whereNotIn("status", listOf("collected", "cancelled"))
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject<Order>() }
            }
    }

    /**
     * Returns a real-time flow of all orders for a student, ordered by placement date.
     */
    fun getOrderHistory(studentId: String): Flow<List<Order>> {
        return firestore.collection("orders")
            .whereEqualTo("studentId", studentId)
            .orderBy("placedAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject<Order>() }
            }
    }

    /**
     * Returns a real-time flow of a single order document.
     */
    fun getOrderById(orderId: String): Flow<Order> {
        return firestore.collection("orders")
            .document(orderId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject<Order>() ?: Order()
            }
    }
}
