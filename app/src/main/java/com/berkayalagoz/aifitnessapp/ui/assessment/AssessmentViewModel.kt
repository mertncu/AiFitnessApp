package com.berkayalagoz.aifitnessapp.ui.assessment

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkayalagoz.aifitnessapp.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AssessmentState {
    object Initial : AssessmentState()
    object Loading : AssessmentState()
    object Success : AssessmentState()
    data class Error(val message: String) : AssessmentState()
}

class AssessmentViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow<AssessmentState>(AssessmentState.Initial)
    val state: StateFlow<AssessmentState> = _state

    var currentStep by mutableStateOf(1)
        private set

    var userProfile by mutableStateOf(
        UserProfile(
            userId = auth.currentUser?.uid ?: ""
        )
    )
        private set

    fun updateFitnessGoal(goal: String) {
        userProfile = userProfile.copy(fitnessGoal = goal)
        moveToNextStep()
    }

    fun updateGender(gender: String) {
        userProfile = userProfile.copy(gender = gender)
        moveToNextStep()
    }

    fun updateWeight(weight: Float) {
        userProfile = userProfile.copy(weight = weight)
        moveToNextStep()
    }

    fun updateAge(age: Int) {
        userProfile = userProfile.copy(age = age)
        moveToNextStep()
    }

    fun updateFitnessExperience(hasExperience: Boolean) {
        userProfile = userProfile.copy(hasPreviousFitnessExperience = hasExperience)
        moveToNextStep()
    }

    fun updateFitnessLevel(level: Int) {
        userProfile = userProfile.copy(fitnessLevel = level)
        moveToNextStep()
    }

    fun updatePhysicalLimitations(limitations: List<String>) {
        userProfile = userProfile.copy(physicalLimitations = limitations)
        moveToNextStep()
    }

    fun updateDietPreference(preference: String) {
        userProfile = userProfile.copy(dietPreference = preference)
        moveToNextStep()
    }

    fun updateWeeklyWorkoutDays(days: Int) {
        userProfile = userProfile.copy(weeklyWorkoutDays = days)
        moveToNextStep()
    }

    fun updateExercisePreferences(preferences: List<String>) {
        userProfile = userProfile.copy(exercisePreferences = preferences)
        moveToNextStep()
    }

    fun updateSupplements(supplements: List<String>) {
        userProfile = userProfile.copy(supplements = supplements)
        moveToNextStep()
    }

    fun updateDailyCalorieGoal(calories: Int) {
        userProfile = userProfile.copy(dailyCalorieGoal = calories)
        moveToNextStep()
    }

    fun updateSleepQuality(quality: String) {
        userProfile = userProfile.copy(sleepQuality = quality)
        moveToNextStep()
    }

    private fun moveToNextStep() {
        if (currentStep < TOTAL_STEPS) {
            currentStep++
        }
    }

    fun moveToPreviousStep() {
        if (currentStep > 1) {
            currentStep--
        }
    }

    fun saveUserProfile() {
        viewModelScope.launch {
            try {
                _state.value = AssessmentState.Loading
                
                val userId = auth.currentUser?.uid
                val currentUser = auth.currentUser
                if (userId == null || currentUser == null) {
                    _state.value = AssessmentState.Error("Kullanıcı kimlik doğrulaması yapılmamış")
                    return@launch
                }

                // Add delay to ensure network is ready
                kotlinx.coroutines.delay(300)
                
                // Firebase Auth'dan kullanıcı bilgilerini al ve profile ekle
                val updatedProfile = userProfile.copy(
                    name = currentUser.displayName ?: "Kullanıcı",
                    email = currentUser.email ?: "",
                    location = "İstanbul, Türkiye", // Default location, sonra kullanıcıdan alınabilir
                    membershipType = "Basic Member",
                    updatedAt = System.currentTimeMillis()
                )
                
                // Add logging to see what's being saved
                Log.d("AssessmentViewModel", "Saving user profile for user: $userId")
                Log.d("AssessmentViewModel", "Profile data: $updatedProfile")
                
                // Try to save with retry mechanism
                var attempts = 0
                val maxAttempts = 3
                
                while (attempts < maxAttempts) {
                    try {
                        firestore.collection("users")
                            .document(userId)
                            .set(updatedProfile, SetOptions.merge())
                            .await()
                        
                        Log.d("AssessmentViewModel", "Profile saved successfully!")
                        _state.value = AssessmentState.Success
                        return@launch
                        
                    } catch (e: Exception) {
                        Log.e("AssessmentViewModel", "Save attempt ${attempts + 1} failed", e)
                        attempts++
                        if (attempts >= maxAttempts) {
                            throw e
                        }
                        // Wait before retry
                        kotlinx.coroutines.delay(1000L * attempts)
                    }
                }

            } catch (e: Exception) {
                Log.e("AssessmentViewModel", "Final save error", e)
                // Better error handling with Turkish messages
                val errorMessage = when {
                    e.message?.contains("UNAVAILABLE") == true -> "Şu anda sunucuya erişilemiyor. Lütfen daha sonra deneyin."
                    e.message?.contains("offline") == true -> "İnternet bağlantınızı kontrol edin"
                    e.message?.contains("PERMISSION_DENIED") == true -> "Veritabanına erişim izni reddedildi"
                    e.message?.contains("network") == true -> "Ağ bağlantı hatası. İnternet bağlantınızı kontrol edin."
                    else -> "Profil kaydediliyor... Lütfen bekleyin"
                }
                
                // Don't show error immediately for connection issues, keep trying in background
                if (e.message?.contains("UNAVAILABLE") == true || 
                    e.message?.contains("offline") == true ||
                    e.message?.contains("network") == true) {
                    
                    // Show loading state and retry in background
                    _state.value = AssessmentState.Loading
                    
                    // Background retry
                    viewModelScope.launch {
                        kotlinx.coroutines.delay(5000) // Wait 5 seconds
                        saveUserProfile() // Retry
                    }
                } else {
                    _state.value = AssessmentState.Error(errorMessage)
                }
            }
        }
    }

    // Test function to create sample data
    fun createTestUserProfile() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                val currentUser = auth.currentUser
                if (userId == null || currentUser == null) {
                    Log.e("AssessmentViewModel", "No user logged in for test")
                    return@launch
                }

                val testProfile = UserProfile(
                    userId = userId,
                    name = currentUser.displayName ?: "Test Kullanıcı",
                    email = currentUser.email ?: "",
                    location = "İstanbul, Türkiye",
                    membershipType = "Basic Member",
                    fitnessGoal = "LOSE_WEIGHT",
                    gender = "MALE",
                    weight = 75f,
                    height = 175f,
                    age = 25,
                    hasPreviousFitnessExperience = true,
                    fitnessLevel = 3,
                    activityLevel = 3,
                    physicalLimitations = listOf("NONE"),
                    medicalConditions = "",
                    dietPreference = "TRADITIONAL",
                    dietaryPreferences = "Geleneksel beslenme",
                    weeklyWorkoutDays = 5,
                    exercisePreferences = listOf("JOGGING", "WEIGHTLIFT"),
                    supplements = listOf(),
                    dailyCalorieGoal = 2000,
                    sleepQuality = "GREAT",
                    sleepHours = 8,
                    waterIntake = 8,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                Log.d("AssessmentViewModel", "Creating test profile for user: $userId")
                
                firestore.collection("users")
                    .document(userId)
                    .set(testProfile)
                    .await()
                
                Log.d("AssessmentViewModel", "Test profile created successfully!")
                _state.value = AssessmentState.Success
                
            } catch (e: Exception) {
                Log.e("AssessmentViewModel", "Failed to create test profile", e)
                _state.value = AssessmentState.Error("Test profil oluşturulamadı: ${e.message}")
            }
        }
    }

    companion object {
        const val TOTAL_STEPS = 13 // Toplam assessment ekranı sayısı
    }
} 