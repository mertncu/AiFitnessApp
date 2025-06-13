package com.berkayalagoz.aifitnessapp.service

import android.util.Log
import com.berkayalagoz.aifitnessapp.model.UserProfile
import kotlinx.coroutines.delay

data class AIRecommendations(
    val dailyCalories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val water: Int,
    val workoutPlan: List<WorkoutRecommendation>,
    val dietPlan: List<DietRecommendation>,
    val tips: List<String>
)

data class WorkoutRecommendation(
    val name: String,
    val duration: String,
    val difficulty: String,
    val calories: String,
    val description: String,
    val type: String
)

data class DietRecommendation(
    val name: String,
    val calories: String,
    val protein: String,
    val carbs: String,
    val fat: String,
    val mealType: String,
    val description: String
)

class AIService {
    
    suspend fun getPersonalizedRecommendations(userProfile: UserProfile): AIRecommendations {
        return try {
            delay(2000)
            
            Log.d("AIService", "Generating recommendations for user: ${userProfile.userId}")
            Log.d("AIService", "User goal: ${userProfile.fitnessGoal}, Weight: ${userProfile.weight}")
            
            generateSmartRecommendations(userProfile)
        } catch (e: Exception) {
            Log.e("AIService", "AI recommendation failed", e)
            generateFallbackRecommendations(userProfile)
        }
    }
    
    private fun generateSmartRecommendations(userProfile: UserProfile): AIRecommendations {
        val baseCalories = calculateBaseCalories(userProfile)
        val macros = calculateMacros(baseCalories, userProfile)
        val waterIntake = calculateWaterIntake(userProfile)
        
        return AIRecommendations(
            dailyCalories = baseCalories,
            protein = macros.protein,
            carbs = macros.carbs,
            fat = macros.fat,
            water = waterIntake,
            workoutPlan = generateWorkouts(userProfile),
            dietPlan = generateDietPlan(userProfile, baseCalories),
            tips = generatePersonalizedTips(userProfile)
        )
    }
    
    private fun calculateBaseCalories(userProfile: UserProfile): Int {
        // Harris-Benedict denklemi modifiye edilmiş versiyonu
        val bmr = when (userProfile.gender.uppercase()) {
            "MALE" -> (88.362 + (13.397 * userProfile.weight) + (4.799 * 175) - (5.677 * userProfile.age)).toInt()
            "FEMALE" -> (447.593 + (9.247 * userProfile.weight) + (3.098 * 162) - (4.330 * userProfile.age)).toInt()
            else -> (500 + (11 * userProfile.weight) + (4 * 170) - (5 * userProfile.age)).toInt()
        }
        
        // Aktivite seviyesi çarpanı
        val activityMultiplier = when {
            userProfile.weeklyWorkoutDays >= 6 -> 1.725f
            userProfile.weeklyWorkoutDays >= 4 -> 1.55f
            userProfile.weeklyWorkoutDays >= 3 -> 1.375f
            userProfile.weeklyWorkoutDays >= 1 -> 1.2f
            else -> 1.0f
        }
        
        // Hedefe göre kalori ayarlaması
        val goalAdjustment = when (userProfile.fitnessGoal) {
            "LOSE_WEIGHT" -> -400
            "GET_BULK" -> +500
            "GAIN_ENDURANCE" -> +200
            else -> 0
        }
        
        return ((bmr * activityMultiplier) + goalAdjustment).toInt()
    }
    
    private fun calculateMacros(calories: Int, userProfile: UserProfile): MacroNutrients {
        return when (userProfile.fitnessGoal) {
            "LOSE_WEIGHT" -> MacroNutrients(
                protein = (calories * 0.35 / 4).toInt(),
                carbs = (calories * 0.30 / 4).toInt(),
                fat = (calories * 0.35 / 9).toInt()
            )
            "GET_BULK" -> MacroNutrients(
                protein = (calories * 0.30 / 4).toInt(),
                carbs = (calories * 0.50 / 4).toInt(),
                fat = (calories * 0.20 / 9).toInt()
            )
            "GAIN_ENDURANCE" -> MacroNutrients(
                protein = (calories * 0.25 / 4).toInt(),
                carbs = (calories * 0.55 / 4).toInt(),
                fat = (calories * 0.20 / 9).toInt()
            )
            else -> MacroNutrients(
                protein = (calories * 0.25 / 4).toInt(),
                carbs = (calories * 0.50 / 4).toInt(),
                fat = (calories * 0.25 / 9).toInt()
            )
        }
    }
    
    private fun calculateWaterIntake(userProfile: UserProfile): Int {
        val baseWater = (userProfile.weight * 35).toInt() // 35ml per kg
        val activityBonus = userProfile.weeklyWorkoutDays * 200 // 200ml per workout day
        return baseWater + activityBonus
    }
    
    private fun generateWorkouts(userProfile: UserProfile): List<WorkoutRecommendation> {
        val workouts = mutableListOf<WorkoutRecommendation>()
        val difficulty = getDifficulty(userProfile.fitnessLevel)
        
        // Hedefe göre workout'lar
        when (userProfile.fitnessGoal) {
            "LOSE_WEIGHT" -> {
                workouts.add(WorkoutRecommendation(
                    name = "HIIT Fat Burner",
                    duration = "25 min",
                    difficulty = difficulty,
                    calories = "350 kcal",
                    description = "Yağ yakımına odaklı yüksek yoğunluklu egzersizler",
                    type = "Cardio"
                ))
                workouts.add(WorkoutRecommendation(
                    name = "Full Body Circuit",
                    duration = "30 min",
                    difficulty = difficulty,
                    calories = "280 kcal",
                    description = "Kas tonunu koruyacak kuvvet antrenmanı",
                    type = "Strength"
                ))
            }
            "GET_BULK" -> {
                workouts.add(WorkoutRecommendation(
                    name = "Upper Body Power",
                    duration = "50 min",
                    difficulty = difficulty,
                    calories = "320 kcal",
                    description = "Üst vücut kas gelişimi için ağırlık antrenmanı",
                    type = "Strength"
                ))
                workouts.add(WorkoutRecommendation(
                    name = "Lower Body Strength",
                    duration = "45 min",
                    difficulty = difficulty,
                    calories = "380 kcal",
                    description = "Alt vücut kas kütlesi geliştirme",
                    type = "Strength"
                ))
            }
            "GAIN_ENDURANCE" -> {
                workouts.add(WorkoutRecommendation(
                    name = "Endurance Cardio",
                    duration = "40 min",
                    difficulty = difficulty,
                    calories = "400 kcal",
                    description = "Kardiyovasküler dayanıklılık geliştirme",
                    type = "Cardio"
                ))
                workouts.add(WorkoutRecommendation(
                    name = "Functional Training",
                    duration = "35 min",
                    difficulty = difficulty,
                    calories = "300 kcal",
                    description = "Fonksiyonel hareket kalıpları",
                    type = "Functional"
                ))
            }
            else -> {
                workouts.add(WorkoutRecommendation(
                    name = "Balanced Workout",
                    duration = "35 min",
                    difficulty = difficulty,
                    calories = "250 kcal",
                    description = "Dengeli tam vücut antrenmanı",
                    type = "Mixed"
                ))
            }
        }
        
        // Kullanıcı tercihlerine göre ek workout'lar
        if (userProfile.exercisePreferences.contains("YOGA") && userProfile.sleepQuality in listOf("BAD", "INSOMNIAC")) {
            workouts.add(WorkoutRecommendation(
                name = "Restorative Yoga",
                duration = "30 min",
                difficulty = "Beginner",
                calories = "120 kcal",
                description = "Stress azaltma ve uyku kalitesi iyileştirme",
                type = "Yoga"
            ))
        }
        
        return workouts.take(3)
    }
    
    private fun generateDietPlan(userProfile: UserProfile, targetCalories: Int): List<DietRecommendation> {
        val mealCalories = targetCalories / 3 // 3 ana öğün
        val dietPlan = mutableListOf<DietRecommendation>()
        
        when (userProfile.dietPreference) {
            "PLANT_BASED" -> {
                dietPlan.addAll(listOf(
                    DietRecommendation(
                        "Avokadolu Quinoa Bowl", 
                        "${(mealCalories * 0.9).toInt()} kcal", 
                        "22g", "65g", "18g", "Kahvaltı",
                        "Protein açısından zengin bitki bazlı kahvaltı"
                    ),
                    DietRecommendation(
                        "Mercimek & Sebze Curry", 
                        "${mealCalories} kcal", 
                        "18g", "75g", "12g", "Öğle",
                        "Tam protein profili sağlayan mercimek kombinasyonu"
                    ),
                    DietRecommendation(
                        "Tofu Stir-fry", 
                        "${(mealCalories * 1.1).toInt()} kcal", 
                        "25g", "45g", "15g", "Akşam",
                        "Yüksek protein tofu ile renkli sebzeler"
                    )
                ))
            }
            "CARBS_ONE" -> {
                dietPlan.addAll(listOf(
                    DietRecommendation(
                        "Yulaf & Meyve Bowl", 
                        "${(mealCalories * 0.8).toInt()} kcal", 
                        "15g", "85g", "8g", "Kahvaltı",
                        "Kompleks karbonhidrat ile enerjili başlangıç"
                    ),
                    DietRecommendation(
                        "Tatlı Patates Salatası", 
                        "${mealCalories} kcal", 
                        "12g", "95g", "10g", "Öğle",
                        "Vitamin A açısından zengin karbonhidrat kaynağı"
                    ),
                    DietRecommendation(
                        "Tam Buğday Makarna", 
                        "${(mealCalories * 1.2).toInt()} kcal", 
                        "18g", "110g", "12g", "Akşam",
                        "Lif açısından zengin kompleks karbonhidrat"
                    )
                ))
            }
            else -> {
                dietPlan.addAll(listOf(
                    DietRecommendation(
                        "Protein Omlet & Avokado", 
                        "${(mealCalories * 0.9).toInt()} kcal", 
                        "35g", "25g", "22g", "Kahvaltı",
                        "Yüksek protein ile güne başlangıç"
                    ),
                    DietRecommendation(
                        "Izgara Tavuk Salatası", 
                        "${mealCalories} kcal", 
                        "45g", "30g", "18g", "Öğle",
                        "Lean protein ile doyurucu ve hafif öğün"
                    ),
                    DietRecommendation(
                        "Somon & Sebze", 
                        "${(mealCalories * 1.1).toInt()} kcal", 
                        "42g", "25g", "28g", "Akşam",
                        "Omega-3 açısından zengin protein kaynağı"
                    )
                ))
            }
        }
        
        return dietPlan
    }
    
    private fun generatePersonalizedTips(userProfile: UserProfile): List<String> {
        val tips = mutableListOf<String>()
        
        // Uyku kalitesine göre
        when (userProfile.sleepQuality) {
            "BAD", "INSOMNIAC" -> {
                tips.add("😴 Uyku kaliteniz düşük. Akşam 8'den sonra kafein almaktan kaçının")
                tips.add("🌙 Yatmadan 2 saat önce ekran kullanımını azaltın")
            }
            "NORMAL" -> tips.add("💤 Uyku rutininizi düzenli tutarak kaliteyi artırabilirsiniz")
            else -> tips.add("✨ Harika uyku kaliteniz fitness hedeflerinize büyük katkı sağlıyor")
        }
        
        // Fitness seviyesine göre
        if (userProfile.fitnessLevel <= 2) {
            tips.add("🚀 Yavaş başlayın, tutarlılık anahtardır. Haftada 3 gün hedefleyin")
        } else if (userProfile.fitnessLevel >= 4) {
            tips.add("💪 İleri seviye fitness'ınızla progressive overload prensibini uygulayın")
        }
        
        // Hedefe özel tavsiyeler
        when (userProfile.fitnessGoal) {
            "LOSE_WEIGHT" -> {
                tips.add("🔥 Yağ yakımı için her öğünde protein bulundurun")
                tips.add("⚖️ Haftalık 0.5-1 kg kayıp hedefleyin, çok hızlı zayıflama metabolizmayı yavaşlatır")
            }
            "GET_BULK" -> {
                tips.add("🥩 Kas gelişimi için günde 1.6-2.2g/kg protein alın")
                tips.add("💧 Kas sentezi için bol su için (günde en az 3 litre)")
            }
            "GAIN_ENDURANCE" -> {
                tips.add("⚡ Dayanıklılık için antrenman öncesi karbonhidrat alın")
                tips.add("🔄 Dinlenme günlerinizde aktif recovery yapın")
            }
        }
        
        // Fiziksel kısıtlamalara göre
        if (userProfile.physicalLimitations.isNotEmpty() && !userProfile.physicalLimitations.contains("NONE")) {
            tips.add("⚠️ Fiziksel kısıtlarınızı göz önünde bulundurarak yavaş ilerleyin")
        }
        
        // Genel sağlık
        tips.add("💧 Günde ${calculateWaterIntake(userProfile)/1000}L su içmeyi hedefleyin")
        tips.add("🥗 Her öğünde farklı renkte sebze tüketmeye özen gösterin")
        
        return tips.take(5)
    }
    
    private fun getDifficulty(fitnessLevel: Int): String {
        return when {
            fitnessLevel <= 2 -> "Beginner"
            fitnessLevel <= 3 -> "Intermediate"
            else -> "Advanced"
        }
    }
    
    private fun generateFallbackRecommendations(userProfile: UserProfile): AIRecommendations {
        return AIRecommendations(
            dailyCalories = 2000,
            protein = 125,
            carbs = 250,
            fat = 67,
            water = 2500,
            workoutPlan = listOf(
                WorkoutRecommendation("Genel Fitness", "30 min", "Beginner", "200 kcal", "Temel fitness rutini", "Mixed")
            ),
            dietPlan = listOf(
                DietRecommendation("Dengeli Öğün", "400 kcal", "25g", "45g", "15g", "Ana Öğün", "Dengeli beslenme")
            ),
            tips = listOf("Sağlıklı beslenin ve düzenli egzersiz yapın")
        )
    }
}

data class MacroNutrients(
    val protein: Int,
    val carbs: Int,
    val fat: Int
) 