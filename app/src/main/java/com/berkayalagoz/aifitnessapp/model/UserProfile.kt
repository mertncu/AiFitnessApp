package com.berkayalagoz.aifitnessapp.model

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val location: String = "",
    val membershipType: String = "Basic Member",
    val profileImageUrl: String = "",
    val fitnessGoal: String = "",
    val gender: String = "",
    val weight: Float = 0f,
    val height: Float = 0f,
    val age: Int = 0,
    val hasPreviousFitnessExperience: Boolean = false,
    val fitnessLevel: Int = 0,
    val activityLevel: Int = 0,
    val physicalLimitations: List<String> = emptyList(),
    val medicalConditions: String = "",
    val dietPreference: String = "",
    val dietaryPreferences: String = "",
    val weeklyWorkoutDays: Int = 0,
    val exercisePreferences: List<String> = emptyList(),
    val supplements: List<String> = emptyList(),
    val dailyCalorieGoal: Int = 0,
    val sleepQuality: String = "",
    val sleepHours: Int = 8,
    val waterIntake: Int = 8,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class FitnessGoal {
    LOSE_WEIGHT,
    TRY_AI_COACH,
    GET_BULK,
    GAIN_ENDURANCE,
    TRYING_APP
}

enum class Gender {
    MALE,
    FEMALE,
    PREFER_NOT_TO_SAY
}

enum class DietPreference {
    PLANT_BASED,
    CARBS_ONE,
    SPECIALIZED,
    TRADITIONAL
}

enum class ExerciseType {
    FOOTBALL,
    BASKETBALL,
    MARTIAL_ARTS,
    TENNIS,
    VOLLEYBALL,
    BADMINTON,
    WRESTLING,
    FITNESS
}

enum class SleepQuality {
    EXCELLENT,
    GREAT,
    NORMAL,
    BAD,
    INSOMNIAC
}

enum class PhysicalLimitation {
    ARTHRITIS,
    BACK_PAIN,
    KNEE_PAIN,
    OBESITY,
    NONE
} 