package com.berkayalagoz.aifitnessapp.ui.diet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berkayalagoz.aifitnessapp.model.UserProfile
import com.berkayalagoz.aifitnessapp.service.AIRecommendations
import com.berkayalagoz.aifitnessapp.service.AIService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class FoodEntry(
    val id: String = "",
    val name: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val mealType: String,
    val date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val time: String = ""
)

data class WaterIntake(
    val date: String = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE),
    val amount: Int = 0
)

data class DietState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val aiRecommendations: AIRecommendations? = null,
    val todaysFoodEntries: List<FoodEntry> = emptyList(),
    val waterIntake: WaterIntake = WaterIntake(),
    val dailyProgress: DailyProgress = DailyProgress()
)

data class DailyProgress(
    val totalCalories: Int = 0,
    val totalProtein: Double = 0.0,
    val totalCarbs: Double = 0.0,
    val totalFat: Double = 0.0,
    val waterProgress: Int = 0,
    val targetCalories: Int = 2000,
    val targetProtein: Int = 150,
    val targetCarbs: Int = 250,
    val targetFat: Int = 67,
    val targetWater: Int = 2500
)

class DietViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val aiService = AIService()
    private val auth = FirebaseAuth.getInstance()
    
    private val _state = MutableStateFlow(DietState())
    val state: StateFlow<DietState> = _state.asStateFlow()
    
    private var currentUserId: String? = null
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val newUserId = firebaseAuth.currentUser?.uid
        if (newUserId != currentUserId) {
            currentUserId = newUserId
            if (newUserId != null) {
                loadUserDietData()
            }
        }
    }
    
    init {
        auth.addAuthStateListener(authStateListener)
        currentUserId = auth.currentUser?.uid
        if (currentUserId != null) {
            loadUserDietData()
        }
    }
    
    private fun loadUserDietData() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                
                val userProfile = getUserProfile()
                if (userProfile != null) {
                    val recommendations = aiService.getPersonalizedRecommendations(userProfile)
                    
                    val updatedProgress = _state.value.dailyProgress.copy(
                        targetCalories = recommendations.dailyCalories,
                        targetProtein = recommendations.protein,
                        targetCarbs = recommendations.carbs,
                        targetFat = recommendations.fat,
                        targetWater = recommendations.water
                    )
                    
                    _state.value = _state.value.copy(
                        isLoading = false,
                        aiRecommendations = recommendations,
                        dailyProgress = updatedProgress
                    )
                    
                    // Bugünkü beslenme verilerini yükle
                    loadTodaysEntries()
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Kullanıcı profili bulunamadı"
                    )
                }
            } catch (e: Exception) {
                Log.e("DietViewModel", "Failed to load diet data", e)
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Diyet verileriniz yüklenemedi: ${e.message}"
                )
            }
        }
    }
    
    private fun loadTodaysEntries() {
        // Simüle edilmiş günlük veriler
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        val sampleEntries = listOf(
            FoodEntry(
                id = "1",
                name = "Protein Omlet",
                calories = 320,
                protein = 25.0,
                carbs = 8.0,
                fat = 22.0,
                mealType = "Kahvaltı",
                time = "08:30"
            ),
            FoodEntry(
                id = "2",
                name = "Izgara Tavuk Salatası",
                calories = 420,
                protein = 35.0,
                carbs = 25.0,
                fat = 18.0,
                mealType = "Öğle",
                time = "13:00"
            )
        )
        
        val totalCalories = sampleEntries.sumOf { it.calories }
        val totalProtein = sampleEntries.sumOf { it.protein }
        val totalCarbs = sampleEntries.sumOf { it.carbs }
        val totalFat = sampleEntries.sumOf { it.fat }
        
        val updatedProgress = _state.value.dailyProgress.copy(
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalCarbs = totalCarbs,
            totalFat = totalFat,
            waterProgress = 1200 // Simüle edilmiş su tüketimi
        )
        
        _state.value = _state.value.copy(
            todaysFoodEntries = sampleEntries,
            dailyProgress = updatedProgress,
            waterIntake = WaterIntake(amount = 1200)
        )
    }
    
    fun addFoodEntry(foodEntry: FoodEntry) {
        viewModelScope.launch {
            try {
                val updatedEntries = _state.value.todaysFoodEntries + foodEntry
                val newProgress = calculateDailyProgress(updatedEntries, _state.value.waterIntake.amount)
                
                _state.value = _state.value.copy(
                    todaysFoodEntries = updatedEntries,
                    dailyProgress = newProgress
                )
            } catch (e: Exception) {
                Log.e("DietViewModel", "Failed to add food entry", e)
                _state.value = _state.value.copy(error = "Yemek eklenemedi: ${e.message}")
            }
        }
    }
    
    fun addWaterIntake(amount: Int) {
        viewModelScope.launch {
            try {
                val newWaterAmount = _state.value.waterIntake.amount + amount
                val updatedWaterIntake = _state.value.waterIntake.copy(amount = newWaterAmount)
                val newProgress = _state.value.dailyProgress.copy(waterProgress = newWaterAmount)
                
                _state.value = _state.value.copy(
                    waterIntake = updatedWaterIntake,
                    dailyProgress = newProgress
                )
            } catch (e: Exception) {
                Log.e("DietViewModel", "Failed to add water intake", e)
                _state.value = _state.value.copy(error = "Su tüketimi eklenemedi: ${e.message}")
            }
        }
    }
    
    fun removeFoodEntry(entryId: String) {
        viewModelScope.launch {
            try {
                val updatedEntries = _state.value.todaysFoodEntries.filter { it.id != entryId }
                val newProgress = calculateDailyProgress(updatedEntries, _state.value.waterIntake.amount)
                
                _state.value = _state.value.copy(
                    todaysFoodEntries = updatedEntries,
                    dailyProgress = newProgress
                )
            } catch (e: Exception) {
                Log.e("DietViewModel", "Failed to remove food entry", e)
                _state.value = _state.value.copy(error = "Yemek silinemedi: ${e.message}")
            }
        }
    }
    
    private fun calculateDailyProgress(foodEntries: List<FoodEntry>, waterAmount: Int): DailyProgress {
        val totalCalories = foodEntries.sumOf { it.calories }
        val totalProtein = foodEntries.sumOf { it.protein }
        val totalCarbs = foodEntries.sumOf { it.carbs }
        val totalFat = foodEntries.sumOf { it.fat }
        
        return _state.value.dailyProgress.copy(
            totalCalories = totalCalories,
            totalProtein = totalProtein,
            totalCarbs = totalCarbs,
            totalFat = totalFat,
            waterProgress = waterAmount
        )
    }
    
    fun refreshData() {
        loadUserDietData()
    }
    
    private suspend fun getUserProfile(): UserProfile? {
        return try {
            val userId = auth.currentUser?.uid ?: return null
            val document = firestore.collection("users")
                .document(userId)
                .get()
                .await()
            
            if (document.exists()) {
                document.toObject(UserProfile::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("DietViewModel", "Error fetching user profile", e)
            null
        }
    }
    
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
} 