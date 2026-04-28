// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.data.repository

import com.example.grabngo2.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

/**
 * Handles all Firebase Authentication operations for GrabNGo.
 * Uses Firebase Auth email/password with @testump.ac.za domain for testing.
 * Real deployment enforces @ump.ac.za domain.
 */
class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Registers a new student account.
     * Validates domain, creates Auth user, writes Firestore document, and sends verification email.
     */
    suspend fun signUpStudent(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        studentNumber: String
    ): Result<FirebaseUser> {
        return try {
            if (!email.endsWith("@testump.ac.za") && !email.endsWith("@ump.ac.za")) {
                return Result.failure(Exception("Only @ump.ac.za or @testump.ac.za emails are allowed"))
            }

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("User creation failed")

            val user = User(
                userId = firebaseUser.uid,
                role = "student",
                firstName = firstName,
                lastName = lastName,
                email = email,
                studentNumber = studentNumber,
                isVerified = false
            )

            firestore.collection("users").document(firebaseUser.uid).set(user).await()
            firebaseUser.sendEmailVerification().await()

            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs in a student. Validates that the user has the student role and is active.
     */
    suspend fun signInStudent(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Sign in failed")

            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject<User>() ?: throw Exception("User profile not found")

            if (user.role != "student") {
                auth.signOut()
                return Result.failure(Exception("Staff portal access denied"))
            }

            if (!user.isActive) {
                auth.signOut()
                return Result.failure(Exception("Account deactivated"))
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs in staff or admin users. Denies access if the user is a student.
     */
    suspend fun signInStaff(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Sign in failed")

            val doc = firestore.collection("users").document(uid).get().await()
            val user = doc.toObject<User>() ?: throw Exception("User profile not found")

            if (user.role != "staff" && user.role != "admin") {
                auth.signOut()
                return Result.failure(Exception("This portal is for cafeteria staff only"))
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Sends a password reset email to the provided address.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Signs out the current user and clears their FCM token in Firestore.
     */
    suspend fun signOut() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            try {
                firestore.collection("users").document(uid).update("fcmToken", "").await()
            } catch (e: Exception) {
                // Log error or handle failure to clear token
            }
        }
        auth.signOut()
    }

    /**
     * Returns the currently authenticated Firebase user.
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}
