// GrabNGo | University of Mpumalanga 2026
package com.example.grabngo2.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grabngo2.data.model.User
import com.example.grabngo2.data.repository.AuthRepository
import com.example.grabngo2.data.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing authentication state for both student and staff login flows.
 * Uses AuthRepository for all Firebase Auth operations.
 * Exposes StateFlow for UI state observation.
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    /**
     * Auth state: Idle | Loading | Success(user) | Error(message)
     */
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    /**
     * Currently authenticated user profile.
     */
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    /**
     * Registers a new student account.
     */
    fun signUpStudent(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        studentNumber: String
    ) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signUpStudent(email, password, firstName, lastName, studentNumber)
            result.fold(
                onSuccess = { _authState.value = AuthState.Idle }, // Navigation handled by UI checking isEmailVerified or Success
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Registration failed") }
            )
        }
    }

    /**
     * Signs in a student and fetches their profile.
     */
    fun signInStudent(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInStudent(email, password)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Sign in failed") }
            )
        }
    }

    /**
     * Signs in a staff member and fetches their profile.
     */
    fun signInStaff(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.signInStaff(email, password)
            result.fold(
                onSuccess = { user ->
                    _currentUser.value = user
                    _authState.value = AuthState.Success(user)
                },
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Sign in failed") }
            )
        }
    }

    /**
     * Sends a password reset email.
     */
    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = authRepository.sendPasswordReset(email)
            result.fold(
                onSuccess = { _authState.value = AuthState.Success(User()) }, // Marker for success
                onFailure = { _authState.value = AuthState.Error(it.message ?: "Failed to send reset email") }
            )
        }
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _currentUser.value = null
            _authState.value = AuthState.Idle
        }
    }

    /**
     * Clears the current error state.
     */
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    /**
     * Reloads the current user state.
     */
    fun reloadUser(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = authRepository.reloadUser()
            result.fold(
                onSuccess = { user ->
                    onComplete(user?.isEmailVerified ?: false)
                },
                onFailure = { onComplete(false) }
            )
        }
    }

    /**
     * Resends the verification email.
     */
    fun resendVerificationEmail() {
        viewModelScope.launch {
            authRepository.getCurrentUser()?.sendEmailVerification()
        }
    }

    /**
     * Returns the current Firebase user.
     */
    fun getCurrentUser(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }
}

/**
 * Sealed class representing the various states of authentication.
 */
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
