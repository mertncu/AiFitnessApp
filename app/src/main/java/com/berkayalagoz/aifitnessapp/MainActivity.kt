package com.berkayalagoz.aifitnessapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.berkayalagoz.aifitnessapp.ui.auth.AuthScreen
import com.berkayalagoz.aifitnessapp.ui.auth.AuthState
import com.berkayalagoz.aifitnessapp.ui.auth.AuthViewModel
import com.berkayalagoz.aifitnessapp.ui.assessment.screens.*
import com.berkayalagoz.aifitnessapp.ui.assessment.AssessmentViewModel
import com.berkayalagoz.aifitnessapp.ui.assessment.AssessmentState
import com.berkayalagoz.aifitnessapp.ui.main.MainScreenWithAI
import com.berkayalagoz.aifitnessapp.ui.main.MainViewModel
import com.berkayalagoz.aifitnessapp.ui.theme.AiFitnessAppTheme

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val assessmentViewModel: AssessmentViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiFitnessAppTheme {
                val navController = rememberNavController()
                val authState by authViewModel.authState.collectAsState()
                var showExitDialog by remember { mutableStateOf(false) }

                NavHost(
                    navController = navController,
                    startDestination = "auth"
                ) {
                    composable("auth") {
                        when (val state = authState) {
                            is AuthState.Success -> {
                                LaunchedEffect(state) {
                                    if (state.isAssessmentCompleted) {
                                        // Assessment tamamlanmış -> Ana sayfaya git
                                        navController.navigate("main") {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                    } else {
                                        // Assessment tamamlanmamış -> Assessment'e git
                                        navController.navigate("assessment") {
                                            popUpTo("auth") { inclusive = true }
                                        }
                                    }
                                }
                            }
                            is AuthState.Error -> {
                                Toast.makeText(this@MainActivity, state.message, Toast.LENGTH_SHORT).show()
                                AuthScreen(
                                    onSignInClick = { email, password ->
                                        authViewModel.signIn(email, password)
                                    },
                                    onSignUpClick = { email, password ->
                                        authViewModel.signUp(email, password)
                                    },
                                    onForgotPasswordClick = {
                                        // TODO: Implement forgot password
                                    }
                                )
                            }
                            else -> {
                                AuthScreen(
                                    onSignInClick = { email, password ->
                                        authViewModel.signIn(email, password)
                                    },
                                    onSignUpClick = { email, password ->
                                        authViewModel.signUp(email, password)
                                    },
                                    onForgotPasswordClick = {
                                        // TODO: Implement forgot password
                                    }
                                )
                            }
                        }
                    }
                    
                    composable("assessment") {
                        val currentStep = assessmentViewModel.currentStep
                        val assessmentState by assessmentViewModel.state.collectAsState()
                        
                        when (currentStep) {
                            1 -> FitnessGoalScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onGoalSelected = { goal ->
                                    assessmentViewModel.updateFitnessGoal(goal)
                                },
                                onBackClick = {
                                    showExitDialog = true
                                }
                            )
                            2 -> GenderScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onGenderSelected = { gender ->
                                    assessmentViewModel.updateGender(gender)
                                },
                                onBackClick = {
                                    assessmentViewModel.moveToPreviousStep()
                                }
                            )
                            3 -> WeightScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onWeightEntered = { weight ->
                                    assessmentViewModel.updateWeight(weight)
                                },
                                onBackClick = {
                                    assessmentViewModel.moveToPreviousStep()
                                }
                            )
                            4 -> AgeScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onAgeEntered = { age ->
                                    assessmentViewModel.updateAge(age)
                                },
                                onBackClick = {
                                    assessmentViewModel.moveToPreviousStep()
                                }
                            )
                            5 -> FitnessExperienceScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onExperienceSelected = { hasExperience ->
                                    assessmentViewModel.updateFitnessExperience(hasExperience)
                                },
                                onBackClick = {
                                    assessmentViewModel.moveToPreviousStep()
                                }
                            )
                            6 -> FitnessLevelScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onLevelSelected = { level ->
                                    assessmentViewModel.updateFitnessLevel(level)
                                },
                                onBackClick = {
                                    assessmentViewModel.moveToPreviousStep()
                                }
                            )
                            7 -> PhysicalLimitationsScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onLimitationsSelected = { limitations ->
                                    assessmentViewModel.updatePhysicalLimitations(limitations)
                                },
                                onBackClick = {
                                    assessmentViewModel.moveToPreviousStep()
                                }
                            )
                            8 -> DietPreferenceScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onDietSelected = { diet ->
                                    assessmentViewModel.updateDietPreference(diet)
                                },
                                onBackClick = {
                                    assessmentViewModel.moveToPreviousStep()
                                }
                            )
                            9 -> WeeklyWorkoutDaysScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onDaysSelected = { days ->
                                    assessmentViewModel.updateWeeklyWorkoutDays(days)
                                },
                                onBackClick = {
                                    assessmentViewModel.moveToPreviousStep()
                                }
                            )
                            10 -> ExercisePreferencesScreen(
                                currentStep = currentStep,
                                totalSteps = AssessmentViewModel.TOTAL_STEPS,
                                onPreferencesSelected = { preferences ->
                                    assessmentViewModel.updateExercisePreferences(preferences)
                                },
                                onBackClick = {
                                    assessmentViewModel.moveToPreviousStep()
                                }
                            )
                            else -> {
                                // Assessment completed
                                LaunchedEffect(Unit) {
                                    assessmentViewModel.saveUserProfile()
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Assessment completed! Saving profile...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                // Show loading or success/error state
                                when (assessmentState) {
                                    is AssessmentState.Loading -> {
                                        // Show loading indicator
                                    }
                                    is AssessmentState.Success -> {
                                        LaunchedEffect(Unit) {
                                            navController.navigate("main") {
                                                popUpTo("assessment") { inclusive = true }
                                            }
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Profile saved successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    is AssessmentState.Error -> {
                                        val error = (assessmentState as AssessmentState.Error).message
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Error: $error",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    else -> {}
                                }
                            }
                        }

                        if (showExitDialog) {
                            AlertDialog(
                                onDismissRequest = { showExitDialog = false },
                                title = { Text("Çıkış Yap") },
                                text = { Text("Değerlendirmeyi yarıda bırakmak istediğinize emin misiniz?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showExitDialog = false
                                            authViewModel.signOut()
                                            navController.navigate("auth") {
                                                popUpTo("assessment") { inclusive = true }
                                            }
                                        }
                                    ) {
                                        Text("Evet, Çıkış Yap")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showExitDialog = false }
                                    ) {
                                        Text("İptal")
                                    }
                                }
                            )
                        }
                    }
                    
                    composable("main") {
                        MainScreenWithAI(
                            viewModel = mainViewModel,
                            onSignOutClick = {
                                authViewModel.signOut()
                                navController.navigate("auth") {
                                    popUpTo("main") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}