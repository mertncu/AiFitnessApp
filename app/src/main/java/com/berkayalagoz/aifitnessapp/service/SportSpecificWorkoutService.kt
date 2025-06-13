package com.berkayalagoz.aifitnessapp.service

import com.berkayalagoz.aifitnessapp.model.ExerciseType
import com.berkayalagoz.aifitnessapp.model.UserProfile

data class SportSpecificWorkout(
    val id: String,
    val title: String,
    val description: String,
    val sportType: ExerciseType,
    val difficulty: String,
    val duration: Int,
    val calories: Int,
    val equipment: List<String>,
    val exercises: List<SportExercise>,
    val focusAreas: List<String>
)

data class SportExercise(
    val name: String,
    val instruction: String,
    val sets: Int,
    val reps: Int,
    val restTime: Int,
    val sportSpecific: Boolean = true
)

class SportSpecificWorkoutService {

    fun generateSportSpecificWorkouts(userProfile: UserProfile): List<SportSpecificWorkout> {
        val workouts = mutableListOf<SportSpecificWorkout>()
        
        // If user has exercise preferences, use them
        if (userProfile.exercisePreferences.isNotEmpty()) {
            userProfile.exercisePreferences.forEach { preference ->
                val sportType = mapPreferenceToSportType(preference)
                sportType?.let {
                    workouts.addAll(getWorkoutsForSport(it, userProfile))
                }
            }
        } else {
            // If no preferences, show default popular sports
            val defaultSports = listOf(
                ExerciseType.FITNESS,
                ExerciseType.FOOTBALL,
                ExerciseType.BASKETBALL
            )
            defaultSports.forEach { sportType ->
                workouts.addAll(getWorkoutsForSport(sportType, userProfile))
            }
        }
        
        return workouts
    }
    
    private fun mapPreferenceToSportType(preference: String): ExerciseType? {
        return when (preference.uppercase()) {
            "FOOTBALL", "FUTBOL" -> ExerciseType.FOOTBALL
            "BASKETBALL", "BASKETBOL" -> ExerciseType.BASKETBALL
            "MARTIAL_ARTS", "DÖVÜŞ SANATLARI", "DOVUS SANATLARI" -> ExerciseType.MARTIAL_ARTS
            "TENNIS", "TENİS", "TENIS" -> ExerciseType.TENNIS
            "VOLLEYBALL", "VOLEYBOL" -> ExerciseType.VOLLEYBALL
            "BADMINTON", "BADMİNTON" -> ExerciseType.BADMINTON
            "WRESTLING", "GÜREŞ", "GURES", "AMERIKAN GÜREŞI", "AMERIKAN GURESI" -> ExerciseType.WRESTLING
            "FITNESS", "FİTNESS" -> ExerciseType.FITNESS
            else -> {
                // Try exact enum match as fallback
                try {
                    ExerciseType.valueOf(preference.uppercase())
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    private fun getWorkoutsForSport(sportType: ExerciseType, userProfile: UserProfile): List<SportSpecificWorkout> {
        return when (sportType) {
            ExerciseType.FOOTBALL -> getFootballWorkouts(userProfile)
            ExerciseType.BASKETBALL -> getBasketballWorkouts(userProfile)
            ExerciseType.MARTIAL_ARTS -> getMartialArtsWorkouts(userProfile)
            ExerciseType.TENNIS -> getTennisWorkouts(userProfile)
            ExerciseType.VOLLEYBALL -> getVolleyballWorkouts(userProfile)
            ExerciseType.BADMINTON -> getBadmintonWorkouts(userProfile)
            ExerciseType.WRESTLING -> getWrestlingWorkouts(userProfile)
            ExerciseType.FITNESS -> getFitnessWorkouts(userProfile)
        }
    }

    private fun getFootballWorkouts(userProfile: UserProfile): List<SportSpecificWorkout> {
        return listOf(
            SportSpecificWorkout(
                id = "football_basics",
                title = "Futbol Temel Antrenmanı",
                description = "Futbol becerilerinizi geliştiren temel antrenman programı",
                sportType = ExerciseType.FOOTBALL,
                difficulty = "Başlangıç",
                duration = 60,
                calories = 450,
                equipment = listOf("Futbol Topu", "Koniler", "Mat"),
                exercises = listOf(
                    SportExercise("Top Kontrolü", "Topu ayak üstünde 30 saniye tut", 3, 10, 60),
                    SportExercise("Pas Çalışması", "Duvara pas atıp kontrol et", 4, 15, 45),
                    SportExercise("Dribling", "Koniler arası zigzag koşu", 3, 8, 90),
                    SportExercise("Sprint", "20 metre hızlı koşu", 5, 5, 120),
                    SportExercise("Şut Çalışması", "Farklı açılardan şut atma", 4, 10, 60)
                ),
                focusAreas = listOf("Teknik", "Hız", "Koordinasyon", "Kuvvet")
            ),
            SportSpecificWorkout(
                id = "football_advanced",
                title = "İleri Seviye Futbol",
                description = "Futbol performansınızı üst seviyeye taşıyan antrenman",
                sportType = ExerciseType.FOOTBALL,
                difficulty = "İleri",
                duration = 75,
                calories = 550,
                equipment = listOf("Futbol Topu", "Çıta", "Koniler"),
                exercises = listOf(
                    SportExercise("Hızlı Ayak Çalışması", "Ladder ile çeviklik", 4, 20, 60),
                    SportExercise("Çift Ayak Şut", "Her iki ayakla şut", 3, 12, 90),
                    SportExercise("Kafa Vuruşu", "Havadan gelen topları kafayla", 4, 8, 75),
                    SportExercise("Pliyometrik Sıçrama", "Kutu üzerine sıçrama", 4, 10, 120),
                    SportExercise("1v1 Dribling", "Savunmayı geçme tekniği", 5, 6, 150)
                ),
                focusAreas = listOf("İleri Teknik", "Patlayıcı Güç", "Maç Durumları")
            )
        )
    }

    private fun getBasketballWorkouts(userProfile: UserProfile): List<SportSpecificWorkout> {
        return listOf(
            SportSpecificWorkout(
                id = "basketball_basics",
                title = "Basketbol Temel Antrenmanı",
                description = "Basketbol becerilerinizi geliştiren temel çalışmalar",
                sportType = ExerciseType.BASKETBALL,
                difficulty = "Başlangıç",
                duration = 55,
                calories = 400,
                equipment = listOf("Basketbol Topu", "Pota", "Koniler"),
                exercises = listOf(
                    SportExercise("Dribbling", "Her iki elle dribling", 4, 30, 60),
                    SportExercise("Shooting Form", "Serbest atış formu", 5, 10, 45),
                    SportExercise("Layup Drill", "Sağ-sol layup çalışması", 4, 8, 90),
                    SportExercise("Defensive Stance", "Savunma duruşu", 3, 45, 75),
                    SportExercise("Rebound Jump", "Ribaund için sıçrama", 4, 12, 120)
                ),
                focusAreas = listOf("Top Hakimiyeti", "Şut", "Sıçrama", "Savunma")
            ),
            SportSpecificWorkout(
                id = "basketball_power",
                title = "Basketbol Güç Antrenmanı",
                description = "Basketbol için güç ve atletizm geliştirme",
                sportType = ExerciseType.BASKETBALL,
                difficulty = "Orta",
                duration = 65,
                calories = 480,
                equipment = listOf("Basketbol Topu", "Agility Ladder", "Weights"),
                exercises = listOf(
                    SportExercise("Vertical Jump", "Dikey sıçrama çalışması", 4, 8, 90),
                    SportExercise("Crossover Drill", "Hızlı crossover", 5, 15, 60),
                    SportExercise("Post Moves", "Post pozisyonu hareketleri", 4, 10, 75),
                    SportExercise("Fast Break", "Hızlı hücum koşusu", 6, 6, 120),
                    SportExercise("3-Point Shooting", "3 sayılık atış", 5, 12, 90)
                ),
                focusAreas = listOf("Patlayıcı Güç", "Hız", "İleri Teknik")
            )
        )
    }

    private fun getMartialArtsWorkouts(userProfile: UserProfile): List<SportSpecificWorkout> {
        return listOf(
            SportSpecificWorkout(
                id = "martial_arts_basics",
                title = "Dövüş Sanatları Temelleri",
                description = "Dövüş sanatları için temel teknik ve güç",
                sportType = ExerciseType.MARTIAL_ARTS,
                difficulty = "Başlangıç",
                duration = 50,
                calories = 350,
                equipment = listOf("Mat", "Temas Yastığı"),
                exercises = listOf(
                    SportExercise("Temel Duruş", "Savaş duruşu pratiği", 5, 60, 45),
                    SportExercise("Punch Combo", "Yumruk kombinasyonları", 4, 20, 60),
                    SportExercise("Kick Practice", "Temel tekme teknikleri", 4, 15, 75),
                    SportExercise("Block & Counter", "Savunma ve karşı saldırı", 3, 12, 90),
                    SportExercise("Shadow Boxing", "Gölge boks", 4, 120, 60)
                ),
                focusAreas = listOf("Teknik", "Koordinasyon", "Güç", "Refleks")
            )
        )
    }

    private fun getTennisWorkouts(userProfile: UserProfile): List<SportSpecificWorkout> {
        return listOf(
            SportSpecificWorkout(
                id = "tennis_basics",
                title = "Tenis Temel Antrenmanı",
                description = "Tenis vuruş teknikleri ve kondisyon",
                sportType = ExerciseType.TENNIS,
                difficulty = "Başlangıç",
                duration = 45,
                calories = 300,
                equipment = listOf("Tenis Raketi", "Tenis Topu", "Duvar"),
                exercises = listOf(
                    SportExercise("Forehand Practice", "Forehand vuruş çalışması", 4, 20, 60),
                    SportExercise("Backhand Practice", "Backhand vuruş çalışması", 4, 20, 60),
                    SportExercise("Servis Practice", "Servis atma çalışması", 3, 15, 90),
                    SportExercise("Footwork Drill", "Ayak çalışması", 4, 30, 45),
                    SportExercise("Net Play", "File önü oyunu", 3, 12, 75)
                ),
                focusAreas = listOf("Teknik", "Ayak Çalışması", "Koordinasyon")
            )
        )
    }

    private fun getVolleyballWorkouts(userProfile: UserProfile): List<SportSpecificWorkout> {
        return listOf(
            SportSpecificWorkout(
                id = "volleyball_basics",
                title = "Voleybol Temel Antrenmanı",
                description = "Voleybol temel teknikleri ve sıçrama",
                sportType = ExerciseType.VOLLEYBALL,
                difficulty = "Başlangıç",
                duration = 50,
                calories = 320,
                equipment = listOf("Voleybol Topu", "File"),
                exercises = listOf(
                    SportExercise("Passing Drill", "Pas verme çalışması", 4, 25, 60),
                    SportExercise("Spike Practice", "Smaç çalışması", 4, 15, 90),
                    SportExercise("Block Jump", "Blok sıçraması", 5, 10, 75),
                    SportExercise("Serve Practice", "Servis çalışması", 3, 20, 60),
                    SportExercise("Dig Practice", "Savunma çalışması", 4, 30, 45)
                ),
                focusAreas = listOf("Sıçrama", "Koordinasyon", "Timing")
            )
        )
    }

    private fun getBadmintonWorkouts(userProfile: UserProfile): List<SportSpecificWorkout> {
        return listOf(
            SportSpecificWorkout(
                id = "badminton_basics",
                title = "Badminton Temel Antrenmanı",
                description = "Badminton teknik ve çeviklik",
                sportType = ExerciseType.BADMINTON,
                difficulty = "Başlangıç",
                duration = 40,
                calories = 280,
                equipment = listOf("Badminton Raketi", "Shuttlecock"),
                exercises = listOf(
                    SportExercise("Clear Shot", "Uzun vuruş çalışması", 4, 20, 60),
                    SportExercise("Smash Practice", "Smaç vuruşu", 3, 15, 75),
                    SportExercise("Net Shot", "File önü vuruşları", 4, 25, 45),
                    SportExercise("Footwork", "Kort içi ayak çalışması", 5, 30, 60),
                    SportExercise("Drop Shot", "Kısa vuruş tekniği", 3, 20, 60)
                ),
                focusAreas = listOf("Çeviklik", "Reaksiyon", "Teknik")
            )
        )
    }

    private fun getWrestlingWorkouts(userProfile: UserProfile): List<SportSpecificWorkout> {
        return listOf(
            SportSpecificWorkout(
                id = "wrestling_basics",
                title = "Güreş Temel Antrenmanı",
                description = "Güreş için güç ve teknik",
                sportType = ExerciseType.WRESTLING,
                difficulty = "Orta",
                duration = 55,
                calories = 450,
                equipment = listOf("Mat", "Resistance Band"),
                exercises = listOf(
                    SportExercise("Takedown Drill", "Düşürme teknikleri", 4, 8, 90),
                    SportExercise("Bridge Exercise", "Köprü egzersizi", 4, 30, 60),
                    SportExercise("Sprawl Practice", "Sprawl savunması", 5, 12, 75),
                    SportExercise("Ground Control", "Yer kontrolü", 3, 60, 90),
                    SportExercise("Escape Drill", "Kaçış teknikleri", 4, 10, 120)
                ),
                focusAreas = listOf("Güç", "Teknik", "Denge", "Kuvvet")
            )
        )
    }

    private fun getFitnessWorkouts(userProfile: UserProfile): List<SportSpecificWorkout> {
        return listOf(
            SportSpecificWorkout(
                id = "fitness_strength",
                title = "Genel Fitness Kuvvet",
                description = "Genel fitness ve kuvvet antrenmanı",
                sportType = ExerciseType.FITNESS,
                difficulty = "Orta",
                duration = 45,
                calories = 350,
                equipment = listOf("Dumbbell", "Barbell", "Mat"),
                exercises = listOf(
                    SportExercise("Squat", "Klasik squat hareketi", 4, 12, 90),
                    SportExercise("Push-up", "Şınav çekme", 3, 15, 60),
                    SportExercise("Deadlift", "Ölü kaldırma", 4, 8, 120),
                    SportExercise("Plank", "Plank duruşu", 3, 60, 60),
                    SportExercise("Pull-up", "Çene çekme", 3, 8, 90)
                ),
                focusAreas = listOf("Genel Kuvvet", "Kas Gelişimi", "Kondisyon")
            )
        )
    }
} 