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
    
    suspend fun getDailyNutritionPlan(userProfile: UserProfile): DailyNutritionPlan {
        return try {
            delay(1500) // Simulate AI processing
            
            Log.d("AIService", "Generating nutrition plan for user: ${userProfile.userId}")
            
            val baseCalories = calculateBaseCalories(userProfile)
            val macros = calculateMacros(baseCalories, userProfile)
            val waterIntake = calculateWaterIntake(userProfile)
            
            DailyNutritionPlan(
                targetCalories = baseCalories,
                targetProtein = macros.protein,
                targetCarbs = macros.carbs,
                targetFat = macros.fat,
                targetWater = waterIntake,
                mealPlan = generateDetailedMealPlan(userProfile, baseCalories),
                nutritionTips = generateNutritionTips(userProfile)
            )
        } catch (e: Exception) {
            Log.e("AIService", "Failed to generate nutrition plan", e)
            generateFallbackNutritionPlan(userProfile)
        }
    }
    
    private fun generateDetailedMealPlan(userProfile: UserProfile, targetCalories: Int): List<DietRecommendation> {
        val breakfastCalories = (targetCalories * 0.30).toInt()
        val lunchCalories = (targetCalories * 0.40).toInt()
        val dinnerCalories = (targetCalories * 0.30).toInt()
        
        val mealPlan = mutableListOf<DietRecommendation>()
        
        when (userProfile.dietPreference) {
            "PLANT_BASED" -> {
                mealPlan.addAll(listOf(
                    DietRecommendation(
                        "Çikolatalı Chia Pudding", 
                        "$breakfastCalories kcal", 
                        "15g", "45g", "12g", "Kahvaltı",
                        "Omega-3 ve lif açısından zengin, tok tutucu kahvaltı"
                    ),
                    DietRecommendation(
                        "Nohut Köri Bowl", 
                        "$lunchCalories kcal", 
                        "22g", "65g", "14g", "Öğle",
                        "Tam protein profili sağlayan bitkisel protein kaynağı"
                    ),
                    DietRecommendation(
                        "Quinoa Stuffed Bell Peppers", 
                        "$dinnerCalories kcal", 
                        "18g", "50g", "10g", "Akşam",
                        "Vitamin C ve antioksidan açısından zengin akşam yemeği"
                    ),
                    DietRecommendation(
                        "Fındık & Kuru Meyve Karışımı", 
                        "180 kcal", 
                        "6g", "15g", "12g", "Ara Öğün",
                        "Doğal şeker ve sağlıklı yağ kaynağı"
                    )
                ))
            }
            "CARBS_ONE" -> {
                mealPlan.addAll(listOf(
                    DietRecommendation(
                        "Muzlu Yulaf Lapası", 
                        "$breakfastCalories kcal", 
                        "12g", "75g", "8g", "Kahvaltı",
                        "Uzun süre enerji sağlayan kompleks karbonhidrat"
                    ),
                    DietRecommendation(
                        "Ton Balıklı Makarna Salatası", 
                        "$lunchCalories kcal", 
                        "25g", "85g", "12g", "Öğle",
                        "Protein ve karbonhidrat dengeli öğün"
                    ),
                    DietRecommendation(
                        "Sebzeli Bulgur Pilavı", 
                        "$dinnerCalories kcal", 
                        "15g", "70g", "8g", "Akşam",
                        "Lif ve mineral açısından zengin tam tahıl"
                    ),
                    DietRecommendation(
                        "Muz & Fındık Ezmesi Tostu", 
                        "220 kcal", 
                        "8g", "35g", "8g", "Ara Öğün",
                        "Hızlı enerji ve potasyum kaynağı"
                    )
                ))
            }
            else -> {
                mealPlan.addAll(listOf(
                    DietRecommendation(
                        "Protein Scramble", 
                        "$breakfastCalories kcal", 
                        "30g", "15g", "18g", "Kahvaltı",
                        "Yüksek protein ile güçlü başlangıç"
                    ),
                    DietRecommendation(
                        "Izgara Somon Salatası", 
                        "$lunchCalories kcal", 
                        "38g", "20g", "22g", "Öğle",
                        "Omega-3 ve kaliteli protein kaynağı"
                    ),
                    DietRecommendation(
                        "Tavuk Göğsü & Brokoli", 
                        "$dinnerCalories kcal", 
                        "35g", "18g", "12g", "Akşam",
                        "Lean protein ve yeşil sebze kombinasyonu"
                    ),
                    DietRecommendation(
                        "Protein Smoothie", 
                        "200 kcal", 
                        "25g", "12g", "6g", "Ara Öğün",
                        "Antrenman sonrası kas onarımı"
                    )
                ))
            }
        }
        
        return mealPlan
    }
    
    private fun generateNutritionTips(userProfile: UserProfile): List<String> {
        val tips = mutableListOf<String>()
        
        // Hedefe özel beslenme tavsiyeleri
        when (userProfile.fitnessGoal) {
            "LOSE_WEIGHT" -> {
                tips.addAll(listOf(
                    "🍽️ Porsiyon kontrolü için küçük tabaklar kullanın",
                    "⏰ Yemek yeme saatlerini düzenli tutun",
                    "🥗 Her öğünün yarısını sebze ile doldurun",
                    "💧 Yemeklerden 30 dk önce 1 bardak su için"
                ))
            }
            "GET_BULK" -> {
                tips.addAll(listOf(
                    "🥩 Her öğünde 25-30g protein hedefleyin",
                    "🍌 Antrenman öncesi karbonhidrat almayı unutmayın",
                    "🥜 Sağlıklı yağları ihmal etmeyin",
                    "🕐 3-4 saatte bir beslenin"
                ))
            }
            "GAIN_ENDURANCE" -> {
                tips.addAll(listOf(
                    "🍝 Kompleks karbonhidratları tercih edin",
                    "⚡ Antrenman sonrası 30 dk içinde beslenin",
                    "🧂 Elektrolit dengesini koruyun",
                    "🍇 Doğal şeker kaynaklarını tercih edin"
                ))
            }
        }
        
        // Diyet tercihine özel tavsiyeler
        when (userProfile.dietPreference) {
            "PLANT_BASED" -> {
                tips.addAll(listOf(
                    "🌱 B12 vitamini desteği almayı düşünün",
                    "🥜 Farklı protein kaynaklarını kombine edin",
                    "🍋 Demir emilimini artırmak için C vitamini ekleyin"
                ))
            }
            "CARBS_ONE" -> {
                tips.addAll(listOf(
                    "🌾 Tam tahıl ürünlerini tercih edin",
                    "⏰ Akşam karbonhidrat alımını sınırlayın",
                    "🥬 Her karbonhidrat ile lif alın"
                ))
            }
        }
        
        // Genel sağlık tavsiyeleri
        tips.addAll(listOf(
            "🌈 Her gün 5 farklı renkte sebze-meyve tüketin",
            "🐟 Haftada 2-3 kez balık tüketin",
            "🥛 Kemik sağlığı için kalsiyum alımına dikkat edin",
            "😴 Kaliteli uyku metabolizma için kritik"
        ))
        
        return tips.take(6)
    }
    
    suspend fun getFoodSuggestions(userProfile: UserProfile, mealType: String): List<FoodSuggestion> {
        return try {
            delay(1000)
            
            val suggestions = mutableListOf<FoodSuggestion>()
            
            when (mealType.lowercase()) {
                "kahvaltı" -> {
                    if (userProfile.dietPreference == "PLANT_BASED") {
                        suggestions.addAll(listOf(
                            FoodSuggestion("Avokado Toast", "Bitkisel", 320, 12.0, 25.0, 22.0, "Sağlıklı yağ ve lif kaynağı"),
                            FoodSuggestion("Chia Pudding", "Bitkisel", 280, 10.0, 35.0, 15.0, "Omega-3 ve antioksidan"),
                            FoodSuggestion("Smoothie Bowl", "Bitkisel", 350, 15.0, 45.0, 12.0, "Vitamin ve mineral deposu")
                        ))
                    } else {
                        suggestions.addAll(listOf(
                            FoodSuggestion("Protein Omlet", "Protein", 320, 25.0, 8.0, 22.0, "Yüksek protein, düşük karbonhidrat"),
                            FoodSuggestion("Greek Yogurt Bowl", "Protein", 280, 20.0, 25.0, 12.0, "Probiyotik ve protein"),
                            FoodSuggestion("Cottage Cheese Pancake", "Protein", 350, 28.0, 30.0, 14.0, "Kas gelişimi destekleyici")
                        ))
                    }
                }
                "öğle" -> {
                    suggestions.addAll(listOf(
                        FoodSuggestion("Quinoa Salat", "Dengeli", 420, 18.0, 55.0, 16.0, "Tam protein profili"),
                        FoodSuggestion("Izgara Tavuk Wrap", "Protein", 380, 32.0, 35.0, 12.0, "Lean protein kaynağı"),
                        FoodSuggestion("Somon Salatası", "Protein", 450, 35.0, 25.0, 25.0, "Omega-3 ve kaliteli protein")
                    ))
                }
                "akşam" -> {
                    suggestions.addAll(listOf(
                        FoodSuggestion("Sebzeli Protein Bowl", "Dengeli", 380, 28.0, 30.0, 18.0, "Vitamin ve mineral zengin"),
                        FoodSuggestion("Balık & Brokoli", "Protein", 320, 30.0, 15.0, 16.0, "Kolay sindirim"),
                        FoodSuggestion("Tofu Stir-fry", "Bitkisel", 350, 22.0, 25.0, 20.0, "Bitkisel protein kaynağı")
                    ))
                }
                else -> {
                    suggestions.addAll(listOf(
                        FoodSuggestion("Protein Bar", "Snack", 180, 15.0, 12.0, 8.0, "Pratik protein kaynağı", "0 dk"),
                        FoodSuggestion("Fındık Karışımı", "Snack", 200, 8.0, 15.0, 14.0, "Sağlıklı yağ ve protein", "0 dk"),
                        FoodSuggestion("Greek Yogurt", "Snack", 150, 12.0, 18.0, 4.0, "Probiyotik ve protein", "0 dk")
                    ))
                }
            }
            
            suggestions
        } catch (e: Exception) {
            Log.e("AIService", "Failed to get food suggestions", e)
            emptyList()
        }
    }
    
    private fun generateFallbackNutritionPlan(userProfile: UserProfile): DailyNutritionPlan {
        return DailyNutritionPlan(
            targetCalories = 2000,
            targetProtein = 125,
            targetCarbs = 250,
            targetFat = 67,
            targetWater = 2500,
            mealPlan = listOf(
                DietRecommendation("Dengeli Kahvaltı", "400 kcal", "20g", "40g", "15g", "Kahvaltı", "Dengeli beslenme"),
                DietRecommendation("Sağlıklı Öğle", "500 kcal", "30g", "50g", "18g", "Öğle", "Kaliteli besinler"),
                DietRecommendation("Hafif Akşam", "400 kcal", "25g", "35g", "16g", "Akşam", "Kolay sindirim")
            ),
            nutritionTips = listOf(
                "Düzenli beslenme alışkanlığı edinin",
                "Su tüketiminizi artırın",
                "Sebze ve meyve tüketimini artırın"
            )
        )
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

data class DailyNutritionPlan(
    val targetCalories: Int,
    val targetProtein: Int,
    val targetCarbs: Int,
    val targetFat: Int,
    val targetWater: Int,
    val mealPlan: List<DietRecommendation>,
    val nutritionTips: List<String>
)

data class FoodSuggestion(
    val name: String,
    val category: String,
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val benefits: String,
    val preparationTime: String = "15 dk"
) 