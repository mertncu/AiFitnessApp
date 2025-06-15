package com.berkayalagoz.aifitnessapp.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class WorkoutSession(
    val id: String = "",
    val name: String = "",
    val duration: Int = 0, // dakika
    val caloriesBurned: Int = 0,
    val difficulty: String = "",
    val exercises: List<String> = emptyList(),
    val completedAt: Long = System.currentTimeMillis(),
    val userId: String = ""
)

data class DailyNutrition(
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val totalCalories: Int = 0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val waterIntake: Int = 0, // litre
    val meals: List<String> = emptyList(),
    val userId: String = ""
)

data class UserAchievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",
    val category: String = "", // workout, nutrition, streak, milestone
    val unlockedAt: Long = System.currentTimeMillis(),
    val isNew: Boolean = true,
    val userId: String = ""
)

data class FitnessGoalProgress(
    val goalType: String = "", // LOSE_WEIGHT, GET_BULK, GAIN_ENDURANCE
    val startDate: Long = System.currentTimeMillis(),
    val targetDate: Long = System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000L), // 90 gün
    val currentProgress: Float = 0f, // 0-100 arası
    val milestones: List<String> = emptyList(),
    val userId: String = ""
)

class FirestoreDataService {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // Antrenman tamamlandığında çağrılır
    suspend fun recordWorkoutSession(
        workoutName: String,
        duration: Int,
        caloriesBurned: Int,
        difficulty: String,
        exercises: List<String>
    ): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val workoutId = UUID.randomUUID().toString()
            
            val workoutSession = WorkoutSession(
                id = workoutId,
                name = workoutName,
                duration = duration,
                caloriesBurned = caloriesBurned,
                difficulty = difficulty,
                exercises = exercises,
                completedAt = System.currentTimeMillis(),
                userId = userId
            )
            
            // Antrenman kaydını ekle
            firestore.collection("users")
                .document(userId)
                .collection("completed_workouts")
                .document(workoutId)
                .set(workoutSession)
                .await()
            
            // Günlük istatistikleri güncelle
            updateDailyStats(userId, caloriesBurned, isWorkout = true)
            
            // Haftalık ilerlemeyi güncelle
            updateWeeklyProgress(userId, duration, caloriesBurned)
            
            // Başarımları kontrol et
            checkAndUnlockAchievements(userId, workoutSession)
            
            Log.d("FirestoreDataService", "Workout session recorded successfully")
            true
        } catch (e: Exception) {
            Log.e("FirestoreDataService", "Error recording workout session", e)
            false
        }
    }
    
    // Beslenme verisi ekler
    suspend fun recordNutritionData(
        calories: Int,
        protein: Double,
        carbs: Double,
        fat: Double,
        waterIntake: Int,
        mealName: String
    ): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            // Bugünkü beslenme verisini al
            val nutritionDoc = firestore.collection("users")
                .document(userId)
                .collection("daily_nutrition")
                .document(today)
                .get()
                .await()
            
            val currentNutrition = if (nutritionDoc.exists()) {
                DailyNutrition(
                    date = today,
                    totalCalories = nutritionDoc.getLong("totalCalories")?.toInt() ?: 0,
                    protein = nutritionDoc.getDouble("protein") ?: 0.0,
                    carbs = nutritionDoc.getDouble("carbs") ?: 0.0,
                    fat = nutritionDoc.getDouble("fat") ?: 0.0,
                    waterIntake = nutritionDoc.getLong("waterIntake")?.toInt() ?: 0,
                    meals = nutritionDoc.get("meals") as? List<String> ?: emptyList(),
                    userId = userId
                )
            } else {
                DailyNutrition(date = today, userId = userId)
            }
            
            // Yeni verileri ekle
            val updatedNutrition = currentNutrition.copy(
                totalCalories = currentNutrition.totalCalories + calories,
                protein = currentNutrition.protein + protein,
                carbs = currentNutrition.carbs + carbs,
                fat = currentNutrition.fat + fat,
                waterIntake = currentNutrition.waterIntake + waterIntake,
                meals = currentNutrition.meals + mealName
            )
            
            // Firestore'a kaydet
            firestore.collection("users")
                .document(userId)
                .collection("daily_nutrition")
                .document(today)
                .set(updatedNutrition)
                .await()
            
            Log.d("FirestoreDataService", "Nutrition data recorded successfully")
            true
        } catch (e: Exception) {
            Log.e("FirestoreDataService", "Error recording nutrition data", e)
            false
        }
    }
    
    // Günlük istatistikleri günceller
    private suspend fun updateDailyStats(
        userId: String,
        caloriesBurned: Int,
        isWorkout: Boolean = false,
        waterIntake: Int = 0,
        sleepHours: Int = 0
    ) {
        try {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val statsDoc = firestore.collection("users")
                .document(userId)
                .collection("daily_stats")
                .document(today)
            
            val currentStats = statsDoc.get().await()
            
            val updates = mutableMapOf<String, Any>()
            
            if (isWorkout) {
                val currentWorkouts = currentStats.getLong("completedWorkouts")?.toInt() ?: 0
                updates["completedWorkouts"] = currentWorkouts + 1
            }
            
            if (caloriesBurned > 0) {
                val currentCalories = currentStats.getLong("totalCaloriesBurned")?.toInt() ?: 0
                updates["totalCaloriesBurned"] = currentCalories + caloriesBurned
            }
            
            if (waterIntake > 0) {
                val currentWater = currentStats.getLong("waterIntake")?.toInt() ?: 0
                updates["waterIntake"] = currentWater + waterIntake
            }
            
            if (sleepHours > 0) {
                updates["sleepHours"] = sleepHours
            }
            
            updates["date"] = today
            updates["lastUpdated"] = System.currentTimeMillis()
            
            statsDoc.set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
            
        } catch (e: Exception) {
            Log.e("FirestoreDataService", "Error updating daily stats", e)
        }
    }
    
    // Haftalık ilerlemeyi günceller
    private suspend fun updateWeeklyProgress(userId: String, duration: Int, caloriesBurned: Int) {
        try {
            val progressDoc = firestore.collection("users")
                .document(userId)
                .collection("weekly_progress")
                .document("current_week")
            
            val currentProgress = progressDoc.get().await()
            
            val completedWorkouts = (currentProgress.getLong("workoutsCompleted")?.toInt() ?: 0) + 1
            val totalCalories = (currentProgress.getLong("caloriesBurned")?.toInt() ?: 0) + caloriesBurned
            val totalDuration = (currentProgress.getLong("totalDuration")?.toInt() ?: 0) + duration
            val averageDuration = if (completedWorkouts > 0) totalDuration / completedWorkouts else 0
            
            val updates = mapOf(
                "workoutsCompleted" to completedWorkouts,
                "totalWorkouts" to 5, // Haftalık hedef
                "caloriesBurned" to totalCalories,
                "totalDuration" to totalDuration,
                "averageWorkoutDuration" to averageDuration,
                "lastUpdated" to System.currentTimeMillis()
            )
            
            progressDoc.set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
            
        } catch (e: Exception) {
            Log.e("FirestoreDataService", "Error updating weekly progress", e)
        }
    }
    
    // Başarımları kontrol eder ve açar
    private suspend fun checkAndUnlockAchievements(userId: String, workoutSession: WorkoutSession) {
        try {
            val achievements = mutableListOf<UserAchievement>()
            
            // İlk antrenman başarımı
            val workoutCount = firestore.collection("users")
                .document(userId)
                .collection("completed_workouts")
                .get()
                .await()
                .size()
            
            if (workoutCount == 1) {
                achievements.add(
                    UserAchievement(
                        id = "first_workout",
                        title = "İlk Adım",
                        description = "İlk antrenmanınızı tamamladınız!",
                        icon = "🎯",
                        category = "workout",
                        userId = userId
                    )
                )
            }
            
            // 5 antrenman başarımı
            if (workoutCount == 5) {
                achievements.add(
                    UserAchievement(
                        id = "five_workouts",
                        title = "Kararlı Başlangıç",
                        description = "5 antrenman tamamladınız!",
                        icon = "💪",
                        category = "workout",
                        userId = userId
                    )
                )
            }
            
            // 10 antrenman başarımı
            if (workoutCount == 10) {
                achievements.add(
                    UserAchievement(
                        id = "ten_workouts",
                        title = "Alışkanlık Haline Geldi",
                        description = "10 antrenman tamamladınız!",
                        icon = "🔥",
                        category = "workout",
                        userId = userId
                    )
                )
            }
            
            // Kalori yakma başarımları
            val totalCalories = firestore.collection("users")
                .document(userId)
                .collection("completed_workouts")
                .get()
                .await()
                .documents
                .sumOf { it.getLong("caloriesBurned") ?: 0 }
            
            if (totalCalories >= 1000) {
                val existingAchievement = firestore.collection("users")
                    .document(userId)
                    .collection("achievements")
                    .document("calorie_burner_1000")
                    .get()
                    .await()
                
                if (!existingAchievement.exists()) {
                    achievements.add(
                        UserAchievement(
                            id = "calorie_burner_1000",
                            title = "Kalori Yakıcı",
                            description = "1000 kalori yaktınız!",
                            icon = "⚡",
                            category = "milestone",
                            userId = userId
                        )
                    )
                }
            }
            
            // Başarımları kaydet
            achievements.forEach { achievement ->
                firestore.collection("users")
                    .document(userId)
                    .collection("achievements")
                    .document(achievement.id)
                    .set(achievement)
                    .await()
            }
            
        } catch (e: Exception) {
            Log.e("FirestoreDataService", "Error checking achievements", e)
        }
    }
    
    // Kullanıcının fitness hedef ilerlemesini günceller
    suspend fun updateFitnessGoalProgress(progressPercentage: Float) {
        try {
            val userId = auth.currentUser?.uid ?: return
            
            val progressDoc = firestore.collection("users")
                .document(userId)
                .collection("fitness_goals")
                .document("current_goal")
            
            val updates = mapOf(
                "currentProgress" to progressPercentage,
                "lastUpdated" to System.currentTimeMillis()
            )
            
            progressDoc.set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
            
        } catch (e: Exception) {
            Log.e("FirestoreDataService", "Error updating fitness goal progress", e)
        }
    }
    
    // Demo veri oluşturur (test amaçlı)
    suspend fun createDemoData() {
        try {
            val userId = auth.currentUser?.uid ?: return
            
            // Demo antrenmanlar
            val demoWorkouts = listOf(
                WorkoutSession(
                    id = "demo_workout_1",
                    name = "HIIT Cardio",
                    duration = 25,
                    caloriesBurned = 320,
                    difficulty = "Orta",
                    exercises = listOf("Burpees", "Mountain Climbers", "Jumping Jacks"),
                    completedAt = System.currentTimeMillis() - 86400000, // 1 gün önce
                    userId = userId
                ),
                WorkoutSession(
                    id = "demo_workout_2",
                    name = "Upper Body Strength",
                    duration = 35,
                    caloriesBurned = 280,
                    difficulty = "Zor",
                    exercises = listOf("Push-ups", "Pull-ups", "Dumbbell Press"),
                    completedAt = System.currentTimeMillis() - 172800000, // 2 gün önce
                    userId = userId
                )
            )
            
            demoWorkouts.forEach { workout ->
                firestore.collection("users")
                    .document(userId)
                    .collection("completed_workouts")
                    .document(workout.id)
                    .set(workout)
                    .await()
            }
            
            // Demo başarımlar
            val demoAchievements = listOf(
                UserAchievement(
                    id = "demo_achievement_1",
                    title = "İlk Adım",
                    description = "İlk antrenmanınızı tamamladınız!",
                    icon = "🎯",
                    category = "workout",
                    unlockedAt = System.currentTimeMillis() - 86400000,
                    isNew = false,
                    userId = userId
                ),
                UserAchievement(
                    id = "demo_achievement_2",
                    title = "Haftalık Seri",
                    description = "7 gün üst üste antrenman yaptınız!",
                    icon = "🔥",
                    category = "streak",
                    unlockedAt = System.currentTimeMillis() - 3600000,
                    isNew = true,
                    userId = userId
                )
            )
            
            demoAchievements.forEach { achievement ->
                firestore.collection("users")
                    .document(userId)
                    .collection("achievements")
                    .document(achievement.id)
                    .set(achievement)
                    .await()
            }
            
            Log.d("FirestoreDataService", "Demo data created successfully")
            
        } catch (e: Exception) {
            Log.e("FirestoreDataService", "Error creating demo data", e)
        }
    }
} 