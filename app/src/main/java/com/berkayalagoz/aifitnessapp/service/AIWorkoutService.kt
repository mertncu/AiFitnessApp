package com.berkayalagoz.aifitnessapp.service

import android.util.Log
import com.berkayalagoz.aifitnessapp.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt

class AIWorkoutService {
    
    private val geminiAIService = GeminiAIService()
    
    suspend fun generatePersonalizedWorkouts(userProfile: UserProfile): List<AIWorkoutRecommendation> {
        // Try to get AI-generated workouts from Gemini first
        return try {
            val geminiWorkouts = generateGeminiWorkouts(userProfile)
            if (geminiWorkouts.isNotEmpty()) {
                Log.d("AIWorkoutService", "Using Gemini AI workouts")
                geminiWorkouts
            } else {
                Log.d("AIWorkoutService", "Falling back to static workouts")
                generateStaticWorkouts(userProfile)
            }
        } catch (e: Exception) {
            Log.e("AIWorkoutService", "Error with Gemini AI, using fallback", e)
            generateStaticWorkouts(userProfile)
        }
    }
    
    private suspend fun generateGeminiWorkouts(userProfile: UserProfile): List<AIWorkoutRecommendation> = coroutineScope {
        // Generate 3 different AI workouts in parallel for faster loading
        val workoutJobs = (0..2).map { index ->
            async {
                try {
                    val geminiWorkout = geminiAIService.generatePersonalizedWorkout(userProfile)
                    geminiWorkout?.let { workout ->
                        AIWorkoutRecommendation(
                            id = "gemini_workout_${userProfile.name.hashCode()}_$index",
                            title = workout.workoutTitle,
                            description = workout.description,
                            category = when (workout.difficulty) {
                                "Beginner" -> "Başlangıç"
                                "Intermediate" -> "Orta"
                                "Advanced" -> "İleri"
                                else -> workout.difficulty
                            },
                            difficulty = workout.difficulty,
                            duration = extractDuration(workout.duration),
                            estimatedCalories = workout.targetCalories,
                            equipment = workout.equipment,
                            aiReason = workout.aiReasoning,
                            personalizedScore = workout.compatibilityScore,
                            exercises = workout.exercises.map { exercise ->
                                AIExercise(
                                    name = exercise.name,
                                    instruction = exercise.description,
                                    reps = exercise.sets,
                                    sets = exercise.sets,
                                    restTime = 60
                                )
                            },
                            focusAreas = workout.focusAreas,
                            tips = workout.tips
                        )
                    }
                } catch (e: Exception) {
                    null // Return null for failed requests
                }
            }
        }
        
        // Wait for all parallel requests to complete and collect successful results
        val workouts = mutableListOf<AIWorkoutRecommendation>()
        workoutJobs.forEach { job ->
            try {
                val workout = job.await()
                workout?.let { workouts.add(it) }
            } catch (e: Exception) {
                // Skip failed workouts
            }
        }
        
        workouts
    }
    
    private suspend fun generateStaticWorkouts(userProfile: UserProfile): List<AIWorkoutRecommendation> {
        // Reduced simulation time for faster loading
        delay(200)
        
        val workouts = mutableListOf<AIWorkoutRecommendation>()
        
        // Analyze user data
        val fitnessLevel = determineFitnessLevel(userProfile)
        val preferredDuration = calculatePreferredDuration(userProfile)
        val targetCalories = calculateTargetCalories(userProfile)
        val focusAreas = determineFocusAreas(userProfile)
        
        // Generate workouts based on goals
        when (userProfile.fitnessGoal.lowercase()) {
            "lose_weight", "kilo vermek" -> {
                workouts.addAll(generateWeightLossWorkouts(userProfile, fitnessLevel, targetCalories))
            }
            "get_bulk", "kas yapmak" -> {
                workouts.addAll(generateMuscleGainWorkouts(userProfile, fitnessLevel))
            }
            "gain_endurance", "kondisyon artırmak" -> {
                workouts.addAll(generateCardioWorkouts(userProfile, fitnessLevel))
            }
            "güçlenmek" -> {
                workouts.addAll(generateStrengthWorkouts(userProfile, fitnessLevel))
            }
            "esneklik artırmak" -> {
                workouts.addAll(generateFlexibilityWorkouts(userProfile, fitnessLevel))
            }
            else -> {
                // Default mixed workouts
                workouts.addAll(generateCardioWorkouts(userProfile, fitnessLevel))
                workouts.addAll(generateStrengthWorkouts(userProfile, fitnessLevel))
            }
        }
        
        // Add general wellness workouts
        workouts.addAll(generateWellnessWorkouts(userProfile, fitnessLevel))
        
        return workouts.take(8) // Limit to 8 recommendations
    }
    
    private fun determineFitnessLevel(userProfile: UserProfile): FitnessLevel {
        val bmi = calculateBMI(userProfile.height.toDouble(), userProfile.weight.toDouble())
        val age = userProfile.age
        
        return when {
            age < 25 && bmi < 25 -> FitnessLevel.ADVANCED
            age < 35 && bmi < 27 -> FitnessLevel.INTERMEDIATE
            age < 45 && bmi < 30 -> FitnessLevel.BEGINNER
            else -> FitnessLevel.BEGINNER
        }
    }
    
    private fun calculatePreferredDuration(userProfile: UserProfile): Int {
        val age = userProfile.age
        return when {
            age < 25 -> 45
            age < 35 -> 35
            age < 50 -> 30
            else -> 25
        }
    }
    
    private fun calculateTargetCalories(userProfile: UserProfile): Int {
        val weight = userProfile.weight.toDouble()
        val height = userProfile.height.toDouble()
        val age = userProfile.age
        
        // Basit kalori hesaplama
        val bmr = if (userProfile.gender == "Erkek") {
            88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age)
        } else {
            447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age)
        }
        
        return (bmr * 0.3).roundToInt() // 30% of BMR for workout
    }
    
    private fun determineFocusAreas(userProfile: UserProfile): List<String> {
        val bmi = calculateBMI(userProfile.height.toDouble(), userProfile.weight.toDouble())
        
        return when {
            bmi > 27 -> listOf("Cardio", "Fat Burn", "Full Body")
            bmi < 20 -> listOf("Strength", "Muscle Building", "Upper Body")
            else -> listOf("Balanced", "Functional", "Core")
        }
    }
    
    private fun generateWeightLossWorkouts(userProfile: UserProfile, level: FitnessLevel, targetCalories: Int): List<AIWorkoutRecommendation> {
        return listOf(
            AIWorkoutRecommendation(
                id = "weight_loss_hiit_${userProfile.email.hashCode()}",
                title = "Yağ Yakım HIIT Antrenmanı",
                description = "Sizin için özel tasarlanmış yoğun interval antrenmanı. Maksimum kalori yakımı için optimize edildi.",
                category = "HIIT",
                difficulty = level.toString(),
                duration = when(level) {
                    FitnessLevel.BEGINNER -> 20
                    FitnessLevel.INTERMEDIATE -> 25
                    FitnessLevel.ADVANCED -> 30
                },
                estimatedCalories = (targetCalories * 1.5).roundToInt(),
                equipment = listOf("Mat", "Timer"),
                aiReason = "BMI'niz ve hedefleriniz göz önünde bulundurularak yağ yakımı odaklı tasarlandı.",
                personalizedScore = 95,
                exercises = generateHIITExercises(level),
                focusAreas = listOf("Yağ Yakımı", "Kardiyovasküler", "Metabolizma")
            ),
            AIWorkoutRecommendation(
                id = "weight_loss_cardio_${userProfile.email.hashCode()}",
                title = "Kalp Atışı Kontrollü Kardiyovasküler",
                description = "Yaşınız ve fitness seviyenize uygun kalp atış hızında sürdürülebilir kardiyovasküler antrenman.",
                category = "Cardio",
                difficulty = level.toString(),
                duration = when(level) {
                    FitnessLevel.BEGINNER -> 25
                    FitnessLevel.INTERMEDIATE -> 35
                    FitnessLevel.ADVANCED -> 45
                },
                estimatedCalories = targetCalories,
                equipment = listOf("Hiçbiri"),
                aiReason = "Yaş grubunuz için ideal kalp atış aralığında kardiyovasküler gelişim.",
                personalizedScore = 88,
                exercises = generateCardioExercises(level),
                focusAreas = listOf("Kardiyovasküler", "Dayanıklılık", "Yağ Yakımı")
            )
        )
    }
    
    private fun generateMuscleGainWorkouts(userProfile: UserProfile, level: FitnessLevel): List<AIWorkoutRecommendation> {
        return listOf(
            AIWorkoutRecommendation(
                id = "muscle_gain_upper_${userProfile.email.hashCode()}",
                title = "Üst Vücut Kas Geliştirme",
                description = "Vücut kompozisyonunuza göre tasarlanmış üst vücut kas geliştirme programı.",
                category = "Strength",
                difficulty = level.toString(),
                duration = when(level) {
                    FitnessLevel.BEGINNER -> 35
                    FitnessLevel.INTERMEDIATE -> 45
                    FitnessLevel.ADVANCED -> 55
                },
                estimatedCalories = 280,
                equipment = listOf("Dumbbell", "Barbell", "Bench"),
                aiReason = "Kas kütlenizi artırmak için progressive overload prensibiyle tasarlandı.",
                personalizedScore = 93,
                exercises = generateUpperBodyExercises(level),
                focusAreas = listOf("Göğüs", "Omuz", "Kol", "Sırt")
            ),
            AIWorkoutRecommendation(
                id = "muscle_gain_lower_${userProfile.email.hashCode()}",
                title = "Alt Vücut Güç ve Kas",
                description = "Bacak ve kalça kaslarınızı güçlendiren, vücut tipinize özel kas geliştirme rutini.",
                category = "Strength",
                difficulty = level.toString(),
                duration = when(level) {
                    FitnessLevel.BEGINNER -> 30
                    FitnessLevel.INTERMEDIATE -> 40
                    FitnessLevel.ADVANCED -> 50
                },
                estimatedCalories = 320,
                equipment = listOf("Barbell", "Dumbbell"),
                aiReason = "Alt vücut gücünüzü artırmak ve dengeli kas gelişimi için optimize edildi.",
                personalizedScore = 90,
                exercises = generateLowerBodyExercises(level),
                focusAreas = listOf("Bacak", "Kalça", "Core")
            )
        )
    }
    
    private fun generateCardioWorkouts(userProfile: UserProfile, level: FitnessLevel): List<AIWorkoutRecommendation> {
        return listOf(
            AIWorkoutRecommendation(
                id = "cardio_endurance_${userProfile.email.hashCode()}",
                title = "Dayanıklılık Geliştirme Programı",
                description = "Kardiyovasküler kapasitenizi artırmak için yaşınıza ve seviyenize uygun tasarlanmış program.",
                category = "Endurance",
                difficulty = level.toString(),
                duration = when(level) {
                    FitnessLevel.BEGINNER -> 25
                    FitnessLevel.INTERMEDIATE -> 35
                    FitnessLevel.ADVANCED -> 45
                },
                estimatedCalories = 250,
                equipment = listOf("Hiçbiri"),
                aiReason = "Mevcut kondisyon seviyenizden başlayarak kademeli gelişim için tasarlandı.",
                personalizedScore = 87,
                exercises = generateEnduranceExercises(level),
                focusAreas = listOf("Kardiyovasküler", "Dayanıklılık", "Nefes Kontrolü")
            )
        )
    }
    
    private fun generateStrengthWorkouts(userProfile: UserProfile, level: FitnessLevel): List<AIWorkoutRecommendation> {
        return listOf(
            AIWorkoutRecommendation(
                id = "strength_functional_${userProfile.email.hashCode()}",
                title = "Fonksiyonel Kuvvet Antrenmanı",
                description = "Günlük yaşamınızda kullanacağınız fonksiyonel kuvveti geliştiren bileşik egzersizler.",
                category = "Functional",
                difficulty = level.toString(),
                duration = when(level) {
                    FitnessLevel.BEGINNER -> 30
                    FitnessLevel.INTERMEDIATE -> 40
                    FitnessLevel.ADVANCED -> 50
                },
                estimatedCalories = 300,
                equipment = listOf("Kettlebell", "Dumbbell", "Mat"),
                aiReason = "Günlük aktivitelerinizde performansınızı artırmak için tasarlandı.",
                personalizedScore = 91,
                exercises = generateFunctionalExercises(level),
                focusAreas = listOf("Fonksiyonel Kuvvet", "Stabilite", "Koordinasyon")
            )
        )
    }
    
    private fun generateFlexibilityWorkouts(userProfile: UserProfile, level: FitnessLevel): List<AIWorkoutRecommendation> {
        return listOf(
            AIWorkoutRecommendation(
                id = "flexibility_yoga_${userProfile.email.hashCode()}",
                title = "Kişiselleştirilmiş Esneklik Programı",
                description = "Yaşınıza ve esneklik seviyenize uygun tasarlanmış yoga ve germe egzersizleri.",
                category = "Flexibility",
                difficulty = level.toString(),
                duration = when(level) {
                    FitnessLevel.BEGINNER -> 20
                    FitnessLevel.INTERMEDIATE -> 30
                    FitnessLevel.ADVANCED -> 40
                },
                estimatedCalories = 120,
                equipment = listOf("Mat"),
                aiReason = "Mevcut esneklik seviyenizi iyileştirmek ve yaralanma riskini azaltmak için tasarlandı.",
                personalizedScore = 89,
                exercises = generateFlexibilityExercises(level),
                focusAreas = listOf("Esneklik", "Mobilite", "Rahatlatma")
            )
        )
    }
    
    private fun generateWellnessWorkouts(userProfile: UserProfile, level: FitnessLevel): List<AIWorkoutRecommendation> {
        return listOf(
            AIWorkoutRecommendation(
                id = "wellness_recovery_${userProfile.email.hashCode()}",
                title = "Dinlenme ve Toparlanma Seansı",
                description = "Yaşam tarzınıza uygun aktif dinlenme ve kas toparlanması odaklı hafif antrenman.",
                category = "Recovery",
                difficulty = "Başlangıç",
                duration = 25,
                estimatedCalories = 100,
                equipment = listOf("Mat"),
                aiReason = "Dengeli bir fitness rutini için aktif dinlenme günleri önemlidir.",
                personalizedScore = 85,
                exercises = generateRecoveryExercises(),
                focusAreas = listOf("Toparlanma", "Gevşeme", "Zihinsel Sağlık")
            )
        )
    }
    
    // Exercise generation functions
    private fun generateHIITExercises(level: FitnessLevel): List<AIExercise> {
        val baseExercises = listOf(
            AIExercise("Jumping Jacks", "30 saniye yoğun tempoda", if(level == FitnessLevel.BEGINNER) 20 else 30, 1, 30),
            AIExercise("Burpees", "Tam vücut hareketi", if(level == FitnessLevel.BEGINNER) 5 else 8, 1, 45),
            AIExercise("Mountain Climbers", "Hızlı alternatif diz çekme", if(level == FitnessLevel.BEGINNER) 20 else 30, 1, 30),
            AIExercise("High Knees", "Dizleri yüksek kaldırarak koş", if(level == FitnessLevel.BEGINNER) 20 else 30, 1, 30)
        )
        
        return when(level) {
            FitnessLevel.BEGINNER -> baseExercises.take(3)
            FitnessLevel.INTERMEDIATE -> baseExercises
            FitnessLevel.ADVANCED -> baseExercises + listOf(
                AIExercise("Tuck Jumps", "Dizleri göğse çekerek zıpla", 12, 1, 45),
                AIExercise("Spider Push-ups", "Alternatif diz çekme ile push-up", 10, 1, 30)
            )
        }
    }
    
    private fun generateCardioExercises(level: FitnessLevel): List<AIExercise> {
        return listOf(
            AIExercise("Yerinde Yürüyüş", "Orta tempoda yürüyüş", if(level == FitnessLevel.BEGINNER) 5 else 10, 1, 60),
            AIExercise("Step Touch", "Yan adımlar ve dokunma", if(level == FitnessLevel.BEGINNER) 30 else 60, 1, 30),
            AIExercise("Arm Circles", "Kol çevirme hareketi", if(level == FitnessLevel.BEGINNER) 20 else 30, 1, 15),
            AIExercise("Hafif Jogging", "Yerinde hafif koşu", if(level == FitnessLevel.BEGINNER) 2 else 5, 1, 60)
        )
    }
    
    private fun generateUpperBodyExercises(level: FitnessLevel): List<AIExercise> {
        return listOf(
            AIExercise("Push-ups", "Göğüs ve kol çalışması", if(level == FitnessLevel.BEGINNER) 8 else 15, 3, 90),
            AIExercise("Dumbbell Press", "Göğüs geliştirme", if(level == FitnessLevel.BEGINNER) 8 else 12, 3, 90),
            AIExercise("Bent-over Rows", "Sırt kasları", if(level == FitnessLevel.BEGINNER) 8 else 12, 3, 90),
            AIExercise("Shoulder Press", "Omuz geliştirme", if(level == FitnessLevel.BEGINNER) 8 else 12, 3, 60)
        )
    }
    
    private fun generateLowerBodyExercises(level: FitnessLevel): List<AIExercise> {
        return listOf(
            AIExercise("Squats", "Bacak ve kalça", if(level == FitnessLevel.BEGINNER) 10 else 15, 3, 90),
            AIExercise("Lunges", "Bacak ve denge", if(level == FitnessLevel.BEGINNER) 8 else 12, 3, 60),
            AIExercise("Deadlifts", "Arka bacak ve kalça", if(level == FitnessLevel.BEGINNER) 6 else 10, 3, 120),
            AIExercise("Calf Raises", "Baldır kasları", if(level == FitnessLevel.BEGINNER) 12 else 20, 3, 45)
        )
    }
    
    private fun generateEnduranceExercises(level: FitnessLevel): List<AIExercise> {
        return listOf(
            AIExercise("Yürüyüş", "Tempo kontrolü ile", if(level == FitnessLevel.BEGINNER) 10 else 20, 1, 120),
            AIExercise("Stationary Bike", "Orta yoğunlukta pedal", if(level == FitnessLevel.BEGINNER) 10 else 15, 1, 60),
            AIExercise("Step Ups", "Basamak çıkma", if(level == FitnessLevel.BEGINNER) 20 else 30, 2, 60),
            AIExercise("Marching", "Yerinde yürüyüş", if(level == FitnessLevel.BEGINNER) 60 else 120, 1, 30)
        )
    }
    
    private fun generateFunctionalExercises(level: FitnessLevel): List<AIExercise> {
        return listOf(
            AIExercise("Kettlebell Swings", "Kalça ve core gücü", if(level == FitnessLevel.BEGINNER) 10 else 15, 3, 90),
            AIExercise("Turkish Get-ups", "Tüm vücut koordinasyon", if(level == FitnessLevel.BEGINNER) 3 else 5, 2, 120),
            AIExercise("Farmer's Walk", "Grip ve core gücü", if(level == FitnessLevel.BEGINNER) 20 else 40, 3, 60),
            AIExercise("Single-leg Deadlifts", "Denge ve stabilite", if(level == FitnessLevel.BEGINNER) 6 else 10, 2, 60)
        )
    }
    
    private fun generateFlexibilityExercises(level: FitnessLevel): List<AIExercise> {
        return listOf(
            AIExercise("Cat-Cow Stretch", "Omurga esnekliği", if(level == FitnessLevel.BEGINNER) 10 else 15, 2, 30),
            AIExercise("Hip Flexor Stretch", "Kalça esnekliği", if(level == FitnessLevel.BEGINNER) 30 else 60, 1, 15),
            AIExercise("Shoulder Rolls", "Omuz gevşetme", if(level == FitnessLevel.BEGINNER) 10 else 20, 2, 15),
            AIExercise("Hamstring Stretch", "Arka bacak esnekliği", if(level == FitnessLevel.BEGINNER) 30 else 60, 1, 15)
        )
    }
    
    private fun generateRecoveryExercises(): List<AIExercise> {
        return listOf(
            AIExercise("Deep Breathing", "Nefes egzersizi", 10, 3, 60),
            AIExercise("Gentle Stretching", "Hafif germe", 5, 1, 30),
            AIExercise("Foam Rolling", "Kas gevşetme", 5, 1, 30),
            AIExercise("Meditation", "Zihinsel dinlenme", 5, 1, 0)
        )
    }
    
    private fun calculateBMI(height: Double, weight: Double): Double {
        return weight / ((height / 100) * (height / 100))
    }
    
    private fun extractDuration(durationStr: String): Int {
        return try {
            // Extract number from strings like "30-45", "30", "45 dakika"
            val numbers = Regex("\\d+").findAll(durationStr).map { it.value.toInt() }.toList()
            if (numbers.isNotEmpty()) {
                numbers.first() // Take the first number
            } else {
                30 // Default duration
            }
        } catch (e: Exception) {
            30 // Default duration on error
        }
    }
    
    enum class FitnessLevel {
        BEGINNER, INTERMEDIATE, ADVANCED
    }
}

// AI-specific data classes
data class AIWorkoutRecommendation(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val duration: Int,
    val estimatedCalories: Int,
    val equipment: List<String>,
    val aiReason: String,
    val personalizedScore: Int,
    val exercises: List<AIExercise>,
    val focusAreas: List<String>,
    val tips: List<String> = emptyList()
)

data class AIExercise(
    val name: String,
    val instruction: String,
    val reps: Int,
    val sets: Int,
    val restTime: Int
) 