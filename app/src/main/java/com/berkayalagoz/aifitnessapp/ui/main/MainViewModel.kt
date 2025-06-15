package com.berkayalagoz.aifitnessapp.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkayalagoz.aifitnessapp.model.UserProfile
import com.berkayalagoz.aifitnessapp.service.AIRecommendations
import com.berkayalagoz.aifitnessapp.service.AIService
import com.berkayalagoz.aifitnessapp.service.FirestoreDataService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.delay

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
    private val firestoreDataService = FirestoreDataService()
    
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
                // Yeni kullanıcı için veriyi yükle - biraz gecikme ile
                viewModelScope.launch {
                    delay(500L) // Firebase senkronizasyonu için kısa bekleme
                    loadUserDataAndGenerateRecommendations()
                }
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
                
                // Firebase'den kullanıcı profilini çek - retry mekanizması ile
                val userProfile = getUserProfileWithRetry(currentUser.uid)
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
    
    private suspend fun getUserProfileWithRetry(userId: String, maxRetries: Int = 3): UserProfile? {
        repeat(maxRetries) { attempt ->
            try {
                Log.d("MainViewModel", "Attempting to load profile, attempt: ${attempt + 1}")
                
                // İlk önce server'dan dene
                val document = firestore.collection("users")
                    .document(userId)
                    .get(com.google.firebase.firestore.Source.SERVER)
                    .await()
                
                if (document.exists()) {
                    val profile = document.toObject(UserProfile::class.java)
                    Log.d("MainViewModel", "Profile loaded from server: ${profile?.fitnessGoal}")
                    return profile
                }
                
                // Server'da yoksa cache'den dene
                val cacheDocument = firestore.collection("users")
                    .document(userId)
                    .get(com.google.firebase.firestore.Source.CACHE)
                    .await()
                
                if (cacheDocument.exists()) {
                    val profile = cacheDocument.toObject(UserProfile::class.java)
                    Log.d("MainViewModel", "Profile loaded from cache: ${profile?.fitnessGoal}")
                    return profile
                }
                
                // Son deneme olarak default source
                val defaultDocument = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()
                
                if (defaultDocument.exists()) {
                    val profile = defaultDocument.toObject(UserProfile::class.java)
                    Log.d("MainViewModel", "Profile loaded from default: ${profile?.fitnessGoal}")
                    return profile
                }
                
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error fetching user profile, attempt ${attempt + 1}", e)
                if (attempt < maxRetries - 1) {
                    // Son deneme değilse biraz bekle
                    delay(1000L * (attempt + 1)) // 1s, 2s, 3s
                }
            }
        }
        
        Log.w("MainViewModel", "Failed to load profile after $maxRetries attempts")
        return null
    }
    
    fun refreshRecommendations() {
        loadUserDataAndGenerateRecommendations()
    }
    
    // Demo veri oluşturma fonksiyonu
    fun createDemoData() {
        viewModelScope.launch {
            try {
                Log.d("MainViewModel", "Creating demo data...")
                firestoreDataService.createDemoData()
                
                // Demo veri oluşturulduktan sonra sayfayı yenile
                delay(1000L) // Firestore'a yazma işleminin tamamlanması için bekle
                refreshRecommendations()
                
                Log.d("MainViewModel", "Demo data created and page refreshed")
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error creating demo data", e)
            }
        }
    }
    
    // Antrenman tamamlandığında çağrılır
    fun recordWorkoutCompletion(
        workoutName: String,
        duration: Int,
        caloriesBurned: Int,
        difficulty: String,
        exercises: List<String>
    ) {
        viewModelScope.launch {
            try {
                val success = firestoreDataService.recordWorkoutSession(
                    workoutName = workoutName,
                    duration = duration,
                    caloriesBurned = caloriesBurned,
                    difficulty = difficulty,
                    exercises = exercises
                )
                
                if (success) {
                    Log.d("MainViewModel", "Workout recorded successfully")
                    // Sayfayı yenile
                    refreshRecommendations()
                } else {
                    Log.e("MainViewModel", "Failed to record workout")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error recording workout", e)
            }
        }
    }
    
    // Beslenme verisi eklendiğinde çağrılır
    fun recordNutritionData(
        calories: Int,
        protein: Double,
        carbs: Double,
        fat: Double,
        waterIntake: Int,
        mealName: String
    ) {
        viewModelScope.launch {
            try {
                val success = firestoreDataService.recordNutritionData(
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    waterIntake = waterIntake,
                    mealName = mealName
                )
                
                if (success) {
                    Log.d("MainViewModel", "Nutrition data recorded successfully")
                    // Sayfayı yenile
                    refreshRecommendations()
                } else {
                    Log.e("MainViewModel", "Failed to record nutrition data")
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Error recording nutrition data", e)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        // Auth state listener'ı temizle
        auth.removeAuthStateListener(authStateListener)
    }
} 