package com.berkayalagoz.aifitnessapp.service

import android.util.Log
import com.berkayalagoz.aifitnessapp.BuildConfig
import com.berkayalagoz.aifitnessapp.model.UserProfile
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAIService {
    
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }
    
    private val gson = Gson()
    
    suspend fun generatePersonalizedWorkout(userProfile: UserProfile): AIGeneratedWorkout? {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = createWorkoutPrompt(userProfile)
                
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                
                val responseText = response.text ?: return@withContext null
                Log.d("GeminiAI", "Raw response: $responseText")
                
                // Parse JSON response
                parseWorkoutResponse(responseText)
                
            } catch (e: Exception) {
                Log.e("GeminiAI", "Error generating workout", e)
                null
            }
        }
    }
    
    suspend fun analyzeUserFitnessLevel(userProfile: UserProfile): FitnessAnalysis? {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = createAnalysisPrompt(userProfile)
                
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                
                val responseText = response.text ?: return@withContext null
                Log.d("GeminiAI", "Analysis response: $responseText")
                
                parseFitnessAnalysis(responseText)
                
            } catch (e: Exception) {
                Log.e("GeminiAI", "Error analyzing fitness level", e)
                null
            }
        }
    }
    
    suspend fun getDailyMotivation(userProfile: UserProfile): String? {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = createMotivationPrompt(userProfile)
                
                val response = generativeModel.generateContent(
                    content {
                        text(prompt)
                    }
                )
                
                response.text?.trim()?.removePrefix("\"")?.removeSuffix("\"")
                
            } catch (e: Exception) {
                Log.e("GeminiAI", "Error generating motivation", e)
                null
            }
        }
    }
    
    private fun createWorkoutPrompt(userProfile: UserProfile): String {
        val bmi = userProfile.weight / ((userProfile.height / 100) * (userProfile.height / 100))
        val age = userProfile.age
        
        return """
        Sen bir profesyonel fitness antrenörüsün. Aşağıdaki kullanıcı bilgilerine göre kişiselleştirilmiş bir antrenman programı oluştur.
        
        Kullanıcı Bilgileri:
        - İsim: ${userProfile.name}
        - Yaş: ${age}
        - Boy: ${userProfile.height.toInt()} cm
        - Kilo: ${userProfile.weight.toInt()} kg
        - BMI: ${String.format("%.1f", bmi)}
        - Cinsiyet: ${userProfile.gender}
        - Fitness Hedefi: ${userProfile.fitnessGoal}
        
        Lütfen aşağıdaki JSON formatında yanıt ver:
        
        {
            "workoutTitle": "Antrenman başlığı",
            "description": "Antrenman açıklaması",
            "duration": "30-45",
            "difficulty": "Beginner/Intermediate/Advanced",
            "targetCalories": 250,
            "focusAreas": ["Kardiyo", "Kuvvet"],
            "aiReasoning": "Bu antrenmanı neden önerdiğinizin açıklaması",
            "compatibilityScore": 92,
            "exercises": [
                {
                    "name": "Egzersiz adı",
                    "sets": 3,
                    "reps": "12-15",
                    "duration": "30 saniye",
                    "description": "Egzersiz açıklaması",
                    "targetMuscles": ["Bacak", "Kalça"]
                }
            ],
            "equipment": ["Hiçbiri", "Dumbbell"],
            "tips": [
                "İpucu 1",
                "İpucu 2"
            ]
        }
        
        Kullanıcının yaşı, BMI'ı ve hedefine göre uygun zorluk seviyesi seç. En az 5-8 egzersiz öner. 
        Antrenman süresi 30-45 dakika arasında olsun. Türkçe yanıt ver.
        """.trimIndent()
    }
    
    private fun createAnalysisPrompt(userProfile: UserProfile): String {
        val bmi = userProfile.weight / ((userProfile.height / 100) * (userProfile.height / 100))
        
        return """
        Sen bir fitness uzmanısın. Aşağıdaki kullanıcı bilgilerini analiz et ve fitness seviyesini değerlendir.
        
        Kullanıcı: ${userProfile.name}, ${userProfile.age} yaş, ${userProfile.height.toInt()}cm, ${userProfile.weight.toInt()}kg
        BMI: ${String.format("%.1f", bmi)}, Hedef: ${userProfile.fitnessGoal}
        
        Aşağıdaki JSON formatında yanıt ver:
        
        {
            "fitnessLevel": "Beginner/Intermediate/Advanced",
            "bmiCategory": "Zayıf/Normal/Fazla Kilolu/Obez",
            "strengths": ["Güçlü yönler"],
            "improvements": ["Gelişim alanları"],
            "recommendations": ["Öneriler"],
            "weeklyGoals": {
                "workoutDays": 4,
                "activeMinutes": 180,
                "caloriesBurn": 1200
            }
        }
        
        Türkçe yanıt ver.
        """.trimIndent()
    }
    
    private fun createMotivationPrompt(userProfile: UserProfile): String {
        return """
        Sen motivasyonel bir fitness koçusun. ${userProfile.name} isimli kullanıcı için günlük motivasyon mesajı oluştur.
        
        Kullanıcı bilgileri:
        - Yaş: ${userProfile.age}
        - Hedef: ${userProfile.fitnessGoal}
        
        Kısa, güçlü ve pozitif bir motivasyon cümlesi oluştur. Emojiler kullan. En fazla 50 kelime.
        Türkçe yanıt ver. Sadece motivasyon cümlesini döndür, başka hiçbir şey ekleme.
        """.trimIndent()
    }
    
    private fun parseWorkoutResponse(response: String): AIGeneratedWorkout? {
        return try {
            // JSON'u extract et
            val jsonStart = response.indexOf('{')
            val jsonEnd = response.lastIndexOf('}') + 1
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonString = response.substring(jsonStart, jsonEnd)
                gson.fromJson(jsonString, AIGeneratedWorkout::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("GeminiAI", "Error parsing workout response", e)
            null
        }
    }
    
    private fun parseFitnessAnalysis(response: String): FitnessAnalysis? {
        return try {
            val jsonStart = response.indexOf('{')
            val jsonEnd = response.lastIndexOf('}') + 1
            
            if (jsonStart >= 0 && jsonEnd > jsonStart) {
                val jsonString = response.substring(jsonStart, jsonEnd)
                gson.fromJson(jsonString, FitnessAnalysis::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("GeminiAI", "Error parsing analysis response", e)
            null
        }
    }
}

// Data models for Gemini AI responses
data class AIGeneratedWorkout(
    @SerializedName("workoutTitle") val workoutTitle: String,
    @SerializedName("description") val description: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("targetCalories") val targetCalories: Int,
    @SerializedName("focusAreas") val focusAreas: List<String>,
    @SerializedName("aiReasoning") val aiReasoning: String,
    @SerializedName("compatibilityScore") val compatibilityScore: Int,
    @SerializedName("exercises") val exercises: List<GeminiExercise>,
    @SerializedName("equipment") val equipment: List<String>,
    @SerializedName("tips") val tips: List<String>
)

data class GeminiExercise(
    @SerializedName("name") val name: String,
    @SerializedName("sets") val sets: Int,
    @SerializedName("reps") val reps: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("description") val description: String,
    @SerializedName("targetMuscles") val targetMuscles: List<String>
)

data class FitnessAnalysis(
    @SerializedName("fitnessLevel") val fitnessLevel: String,
    @SerializedName("bmiCategory") val bmiCategory: String,
    @SerializedName("strengths") val strengths: List<String>,
    @SerializedName("improvements") val improvements: List<String>,
    @SerializedName("recommendations") val recommendations: List<String>,
    @SerializedName("weeklyGoals") val weeklyGoals: WeeklyGoals
)

data class WeeklyGoals(
    @SerializedName("workoutDays") val workoutDays: Int,
    @SerializedName("activeMinutes") val activeMinutes: Int,
    @SerializedName("caloriesBurn") val caloriesBurn: Int
) 