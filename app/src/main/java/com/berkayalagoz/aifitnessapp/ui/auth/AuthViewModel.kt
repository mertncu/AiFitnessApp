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
                delay(500L)
                
                Log.d("AuthViewModel", "Checking assessment for user: ${user.uid}")
                
                // Retry mekanizması ile profil kontrolü
                val isCompleted = checkAssessmentWithRetry(user.uid)
                
                Log.d("AuthViewModel", "Final assessment status: $isCompleted")
                
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
    
    private suspend fun checkAssessmentWithRetry(userId: String, maxRetries: Int = 3): Boolean {
        repeat(maxRetries) { attempt ->
            try {
                Log.d("AuthViewModel", "Assessment check attempt: ${attempt + 1}")
                
                var userDoc: com.google.firebase.firestore.DocumentSnapshot? = null
                
                // Try multiple strategies
                try {
                    // Strategy 1: Try server first
                    userDoc = firestore.collection("users")
                        .document(userId)
                        .get(Source.SERVER)
                        .await()
                    Log.d("AuthViewModel", "Got document from server. Exists: ${userDoc.exists()}")
                } catch (serverException: Exception) {
                    try {
                        // Strategy 2: Try cache
                        userDoc = firestore.collection("users")
                            .document(userId)
                            .get(Source.CACHE)
                            .await()
                        Log.d("AuthViewModel", "Got document from cache. Exists: ${userDoc.exists()}")
                    } catch (cacheException: Exception) {
                        // Strategy 3: Try default source (auto)
                        userDoc = firestore.collection("users")
                            .document(userId)
                            .get()
                            .await()
                        Log.d("AuthViewModel", "Got document from default source. Exists: ${userDoc.exists()}")
                    }
                }

                // Check if user document exists and has required fields
                val isCompleted = userDoc?.exists() == true && 
                    userDoc.data?.containsKey("fitnessGoal") == true &&
                    userDoc.data?.containsKey("gender") == true &&
                    userDoc.data?.containsKey("weight") == true
                
                Log.d("AuthViewModel", "Assessment completed: $isCompleted")
                Log.d("AuthViewModel", "User doc exists: ${userDoc?.exists()}")
                Log.d("AuthViewModel", "Has fitness goal: ${userDoc?.data?.containsKey("fitnessGoal")}")
                
                // Eğer profil bulunduysa sonucu döndür
                if (userDoc?.exists() == true) {
                    return isCompleted
                }
                
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error in assessment check attempt ${attempt + 1}", e)
                if (attempt < maxRetries - 1) {
                    // Son deneme değilse biraz bekle
                    delay(1000L * (attempt + 1)) // 1s, 2s, 3s
                }
            }
        }
        
        // Tüm denemeler başarısız olursa yeni kullanıcı varsay
        Log.w("AuthViewModel", "Failed to check assessment after $maxRetries attempts, assuming new user")
        return false
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

    fun markAssessmentCompleted() {
        val currentState = _authState.value
        if (currentState is AuthState.Success) {
            _authState.value = currentState.copy(isAssessmentCompleted = true)
        }
    }
} 