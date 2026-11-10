package com.example.phylax.ui.screens.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phylax.data.model.User
import com.example.phylax.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        firebaseAuth.addAuthStateListener { auth ->
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Keep the user signed in even if the Firestore profile fetch fails.
                // Previously a failed read left the app stuck on the Welcome screen.
                val fallbackUser = User(
                    uid = currentUser.uid,
                    name = currentUser.displayName ?: "",
                    phone = currentUser.phoneNumber ?: "",
                    email = currentUser.email ?: ""
                )
                uiState = uiState.copy(
                    user = uiState.user ?: fallbackUser,
                    isInitialized = true
                )
                fetchUserProfile(currentUser.uid)
            } else {
                // User is signed out
                uiState = uiState.copy(user = null, isInitialized = true)
            }
        }
    }

    private fun fetchUserProfile(uid: String) {
        viewModelScope.launch {
            try {
                val doc = authRepository.getUserProfile(uid)
                if (doc != null) {
                    // The Firebase session UID is authoritative even if the
                    // Firestore profile is missing its uid field.
                    uiState = uiState.copy(user = doc.copy(uid = uid))
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to fetch user profile: ${e.message}")
            }
        }
    }

    fun onEvent(event: AuthUserEvent) {
        when (event) {
            is AuthUserEvent.RegisterWithEmail -> registerWithEmail(event.user, event.password)
            is AuthUserEvent.LoginWithEmail -> loginWithEmail(event.email, event.password)
            is AuthUserEvent.ForgotPassword -> forgotPassword(event.email)
            is AuthUserEvent.Logout -> logout()
        }
    }

    private fun registerWithEmail(user: User, password: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val result = authRepository.registerWithEmail(user, password)
            uiState = uiState.copy(isLoading = false, user = result.getOrNull())
            result.onSuccess {
                _events.emit(AuthEvent.AuthSuccess)
            }.onFailure {
                _events.emit(AuthEvent.Error(it.message ?: "Registration failed"))
            }
        }
    }

    private fun loginWithEmail(email: String, password: String) {
        Log.d("AuthViewModel", "Attempting login with email: $email")
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val result = authRepository.loginWithEmail(email, password)
            uiState = uiState.copy(isLoading = false, user = result.getOrNull())
            result.onSuccess {
                Log.d("AuthViewModel", "Login successful for: $email")
                _events.emit(AuthEvent.AuthSuccess)
            }.onFailure {
                Log.e("AuthViewModel", "Login failed: ${it.message}")
                _events.emit(AuthEvent.Error(it.message ?: "Login failed"))
            }
        }
    }

    private fun forgotPassword(email: String) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true)
            val result = authRepository.sendPasswordResetEmail(email)
            uiState = uiState.copy(isLoading = false)
            result.onSuccess {
                _events.emit(AuthEvent.PasswordResetSent)
            }.onFailure {
                _events.emit(AuthEvent.Error(it.message ?: "Failed to send reset email"))
            }
        }
    }

    private fun logout() {
        authRepository.logout()
        uiState = uiState.copy(user = null)
        viewModelScope.launch {
            _events.emit(AuthEvent.LoggedOut)
        }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isInitialized: Boolean = false
)

sealed class AuthEvent {
    object AuthSuccess : AuthEvent()
    object LoggedOut : AuthEvent()
    object PasswordResetSent : AuthEvent()
    data class Error(val message: String) : AuthEvent()
}

sealed class AuthUserEvent {
    data class RegisterWithEmail(val user: User, val password: String) : AuthUserEvent()
    data class LoginWithEmail(val email: String, val password: String) : AuthUserEvent()
    data class ForgotPassword(val email: String) : AuthUserEvent()
    object Logout : AuthUserEvent()
}
