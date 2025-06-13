package com.berkayalagoz.aifitnessapp.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(
        val user: FirebaseUser,
        val isAssessmentCompleted: Boolean = false
    ) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    init {
        // Check if user is already signed in
        auth.currentUser?.let { user ->
            checkAssessmentStatus(user)
        }
    }

    private fun checkAssessmentStatus(user: FirebaseUser) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                // Add a small delay to ensure Firestore is properly initialized
                delay(500)
                
                var userDoc: com.google.firebase.firestore.DocumentSnapshot? = null
                var isFromCache = false
                
                Log.d("AuthViewModel", "Checking assessment for user: ${user.uid}")
                
                // Try multiple strategies
                try {
                    // Strategy 1: Try server first
                    userDoc = firestore.collection("users")
                        .document(user.uid)
                        .get(Source.SERVER)
                        .await()
                    Log.d("AuthViewModel", "Got document from server. Exists: ${userDoc.exists()}")
                } catch (serverException: Exception) {
                    try {
                        // Strategy 2: Try cache
                        userDoc = firestore.collection("users")
                            .document(user.uid)
                            .get(Source.CACHE)
                            .await()
                        isFromCache = true
                        Log.d("AuthViewModel", "Got document from cache. Exists: ${userDoc.exists()}")
                    } catch (cacheException: Exception) {
                        // Strategy 3: Try default source (auto)
                        try {
                            userDoc = firestore.collection("users")
                                .document(user.uid)
                                .get()
                                .await()
                            Log.d("AuthViewModel", "Got document from default source. Exists: ${userDoc.exists()}")
                        } catch (defaultException: Exception) {
                            // Strategy 4: Assume new user and continue
                            Log.d("AuthViewModel", "All strategies failed. Assuming new user.")
                            _authState.value = AuthState.Success(
                                user = user,
                                isAssessmentCompleted = false
                            )
                            return@launch
                        }
                    }
                }

                val isCompleted = userDoc?.exists() == true
                Log.d("AuthViewModel", "Assessment completed: $isCompleted")
                
                _authState.value = AuthState.Success(
                    user = user,
                    isAssessmentCompleted = isCompleted
                )
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error checking assessment", e)
                // Final fallback - assume new user
                _authState.value = AuthState.Success(
                    user = user,
                    isAssessmentCompleted = false
                )
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    checkAssessmentStatus(user)
                } ?: run {
                    _authState.value = AuthState.Error("Sign in failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let { user ->
                    // New users haven't completed assessment
                    _authState.value = AuthState.Success(user, isAssessmentCompleted = false)
                } ?: run {
                    _authState.value = AuthState.Error("Sign up failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.sendPasswordResetEmail(email).await()
                _authState.value = AuthState.Initial
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Failed to send reset email")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Initial
    }
} 