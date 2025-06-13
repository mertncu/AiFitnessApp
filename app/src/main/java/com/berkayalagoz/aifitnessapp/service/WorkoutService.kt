package com.berkayalagoz.aifitnessapp.service

import kotlinx.coroutines.delay

class WorkoutService {
    
    suspend fun getPopularWorkouts(): List<PopularWorkout> {
        // Simulate API call
        delay(500)
        return listOf(
            PopularWorkout(
                id = "popular_1",
                title = "Morning Power Boost",
                category = "Cardio",
                duration = 25,
                difficulty = "Orta",
                imageUrl = "",
                participants = 1240,
                rating = 4.8f
            ),
            PopularWorkout(
                id = "popular_2", 
                title = "Upper Body Blast",
                category = "Kuvvet",
                duration = 35,
                difficulty = "İleri",
                imageUrl = "",
                participants = 890,
                rating = 4.7f
            ),
            PopularWorkout(
                id = "popular_3",
                title = "Yoga Flow",
                category = "Yoga",
                duration = 45,
                difficulty = "Başlangıç",
                imageUrl = "",
                participants = 2150,
                rating = 4.9f
            )
        )
    }
    
    suspend fun getTrendingWorkouts(): List<TrendingWorkout> {
        delay(300)
        return listOf(
            TrendingWorkout(
                id = "trending_1",
                title = "HIIT Explosion",
                category = "HIIT",
                duration = 20,
                difficulty = "İleri",
                calories = 300,
                equipment = listOf("Mat", "Dumbbell"),
                trending = true
            ),
            TrendingWorkout(
                id = "trending_2",
                title = "Core Crusher",
                category = "Abs",
                duration = 15,
                difficulty = "Orta", 
                calories = 150,
                equipment = listOf("Mat"),
                trending = true
            ),
            TrendingWorkout(
                id = "trending_3",
                title = "Flexibility Focus",
                category = "Stretching",
                duration = 30,
                difficulty = "Başlangıç",
                calories = 80,
                equipment = listOf("Mat"),
                trending = false
            )
        )
    }
    
    suspend fun getWorkoutsByCategory(category: String): List<CategoryWorkout> {
        delay(400)
        return when (category.lowercase()) {
            "kuvvet" -> listOf(
                CategoryWorkout(
                    id = "strength_1",
                    title = "Chest & Triceps",
                    duration = 40,
                    exercises = 8,
                    difficulty = "Orta",
                    description = "Göğüs ve triceps kaslarına odaklanan güçlü antrenman"
                ),
                CategoryWorkout(
                    id = "strength_2", 
                    title = "Back & Biceps",
                    duration = 45,
                    exercises = 10,
                    difficulty = "İleri",
                    description = "Sırt ve biceps geliştiren profesyonel program"
                )
            )
            "cardio" -> listOf(
                CategoryWorkout(
                    id = "cardio_1",
                    title = "Fat Burning Circuit",
                    duration = 30,
                    exercises = 12,
                    difficulty = "Orta",
                    description = "Yağ yakım odaklı yoğun cardio antrenmanı"
                ),
                CategoryWorkout(
                    id = "cardio_2",
                    title = "Sprint Intervals", 
                    duration = 25,
                    exercises = 8,
                    difficulty = "İleri",
                    description = "Hızlı koşu interval antrenman programı"
                )
            )
            else -> listOf(
                CategoryWorkout(
                    id = "general_1",
                    title = "Full Body Workout",
                    duration = 50,
                    exercises = 15,
                    difficulty = "Orta",
                    description = "Tüm vücut kaslarını çalıştıran kapsamlı program"
                )
            )
        }
    }
    
    suspend fun getPersonalizedWorkouts(userId: String): List<PersonalizedWorkout> {
        delay(600)
        // Simulate AI-based personalized recommendations
        return listOf(
            PersonalizedWorkout(
                id = "personal_1",
                title = "Your Perfect Match",
                reason = "Hedeflerinize uygun",
                duration = 35,
                calories = 280,
                difficulty = "Orta",
                matchPercentage = 95,
                categories = listOf("Kuvvet", "Cardio")
            ),
            PersonalizedWorkout(
                id = "personal_2",
                title = "Progress Booster", 
                reason = "İlerleme kaydınıza uygun",
                duration = 40,
                calories = 320,
                difficulty = "İleri",
                matchPercentage = 88,
                categories = listOf("HIIT", "Kuvvet")
            ),
            PersonalizedWorkout(
                id = "personal_3",
                title = "Recovery Session",
                reason = "Dinlenme gününüz için ideal",
                duration = 25,
                calories = 120,
                difficulty = "Başlangıç",
                matchPercentage = 92,
                categories = listOf("Yoga", "Stretching")
            )
        )
    }
    
    suspend fun searchWorkouts(query: String): List<SearchResult> {
        delay(400)
        // Simulate search functionality
        return if (query.isNotEmpty()) {
            listOf(
                SearchResult(
                    id = "search_1",
                    title = "Push-up Challenge",
                    category = "Kuvvet",
                    duration = 20,
                    difficulty = "Orta",
                    relevanceScore = 0.95f
                ),
                SearchResult(
                    id = "search_2",
                    title = "Cardio Blast",
                    category = "Cardio", 
                    duration = 30,
                    difficulty = "İleri",
                    relevanceScore = 0.85f
                )
            ).filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.category.contains(query, ignoreCase = true) 
            }
        } else {
            emptyList()
        }
    }
}

// Data classes for different workout types
data class PopularWorkout(
    val id: String,
    val title: String,
    val category: String,
    val duration: Int,
    val difficulty: String,
    val imageUrl: String,
    val participants: Int,
    val rating: Float
)

data class TrendingWorkout(
    val id: String,
    val title: String,
    val category: String,
    val duration: Int,
    val difficulty: String,
    val calories: Int,
    val equipment: List<String>,
    val trending: Boolean
)

data class CategoryWorkout(
    val id: String,
    val title: String,
    val duration: Int,
    val exercises: Int,
    val difficulty: String,
    val description: String
)

data class PersonalizedWorkout(
    val id: String,
    val title: String,
    val reason: String,
    val duration: Int,
    val calories: Int,
    val difficulty: String,
    val matchPercentage: Int,
    val categories: List<String>
)

data class SearchResult(
    val id: String,
    val title: String,
    val category: String,
    val duration: Int,
    val difficulty: String,
    val relevanceScore: Float
) 