package com.berkayalagoz.aifitnessapp.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkayalagoz.aifitnessapp.model.UserProfile
import com.berkayalagoz.aifitnessapp.service.AIRecommendations
import com.berkayalagoz.aifitnessapp.service.AIService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class MainUiState {
    object Loading : MainUiState()
    data class Success(
        val userProfile: UserProfile,
        val aiRecommendations: AIRecommendations
    ) : MainUiState()
    data class Error(val message: String) : MainUiState()
    object NoProfile : MainUiState()
}

class MainViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val aiService = AIService()
    
    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState
    
    // Şu anki kullanıcının UID'sini takip et
    private var currentUserId: String? = null
    
    // Auth state listener
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val newUserId = firebaseAuth.currentUser?.uid
        
        // Kullanıcı değişti mi kontrol et
        if (newUserId != currentUserId) {
            Log.d("MainViewModel", "User changed from $currentUserId to $newUserId")
            currentUserId = newUserId
            
            if (newUserId != null) {
                // Yeni kullanıcı için veriyi yükle
                loadUserDataAndGenerateRecommendations()
            } else {
                // Kullanıcı çıkış yaptı
                _uiState.value = MainUiState.Error("Kullanıcı giriş yapmamış")
            }
        }
    }
    
    init {
        // Auth state listener'ı ekle
        auth.addAuthStateListener(authStateListener)
        
        // İlk yükleme
        val initialUserId = auth.currentUser?.uid
        if (initialUserId != null) {
            currentUserId = initialUserId
            loadUserDataAndGenerateRecommendations()
        } else {
            _uiState.value = MainUiState.Error("Kullanıcı giriş yapmamış")
        }
    }
    
    fun loadUserDataAndGenerateRecommendations() {
        viewModelScope.launch {
            try {
                _uiState.value = MainUiState.Loading
                
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _uiState.value = MainUiState.Error("Kullanıcı giriş yapmamış")
                    return@launch
                }
                
                Log.d("MainViewModel", "Loading profile for user: ${currentUser.uid}")
                
                // Firebase'den kullanıcı profilini çek
                val userProfile = getUserProfile(currentUser.uid)
                if (userProfile == null) {
                    _uiState.value = MainUiState.NoProfile
                    return@launch
                }
                
                Log.d("MainViewModel", "Profile loaded: ${userProfile.fitnessGoal}")
                
                // AI önerilerini üret
                val aiRecommendations = aiService.getPersonalizedRecommendations(userProfile)
                
                Log.d("MainViewModel", "AI recommendations generated - Calories: ${aiRecommendations.dailyCalories}")
                
                _uiState.value = MainUiState.Success(
                    userProfile = userProfile,
                    aiRecommendations = aiRecommendations
                )
                
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error loading data", e)
                _uiState.value = MainUiState.Error("Veriler yüklenemedi: ${e.message}")
            }
        }
    }
    
    private suspend fun getUserProfile(userId: String): UserProfile? {
        return try {
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            if (document.exists()) {
                // Firebase document'ini UserProfile'a çevir
                document.toObject(UserProfile::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error fetching user profile", e)
            null
        }
    }
    
    fun refreshRecommendations() {
        loadUserDataAndGenerateRecommendations()
    }
    
    override fun onCleared() {
        super.onCleared()
        // Auth state listener'ı temizle
        auth.removeAuthStateListener(authStateListener)
    }
} 