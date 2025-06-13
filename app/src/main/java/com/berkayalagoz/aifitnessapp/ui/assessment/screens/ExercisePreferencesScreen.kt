package com.berkayalagoz.aifitnessapp.ui.assessment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.berkayalagoz.aifitnessapp.model.ExerciseType
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentButton
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentQuestion
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentTopBar
import com.berkayalagoz.aifitnessapp.ui.assessment.components.SelectableOption

@Composable
fun ExercisePreferencesScreen(
    currentStep: Int,
    totalSteps: Int,
    onPreferencesSelected: (List<String>) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedExercises by remember { mutableStateOf(setOf<ExerciseType>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AssessmentTopBar(
            currentStep = currentStep,
            totalSteps = totalSteps,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        AssessmentQuestion(
            question = "Hangi spor türlerini tercih ediyorsunuz?"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExerciseType.values().forEach { exercise ->
                SelectableOption(
                    text = when (exercise) {
                        ExerciseType.FOOTBALL -> "Futbol"
                        ExerciseType.BASKETBALL -> "Basketbol"
                        ExerciseType.MARTIAL_ARTS -> "Dövüş Sanatları"
                        ExerciseType.TENNIS -> "Tenis"
                        ExerciseType.VOLLEYBALL -> "Voleybol"
                        ExerciseType.BADMINTON -> "Badminton"
                        ExerciseType.WRESTLING -> "Amerikan Güreşi"
                        ExerciseType.FITNESS -> "Fitness"
                    },
                    isSelected = selectedExercises.contains(exercise),
                    onClick = {
                        selectedExercises = if (selectedExercises.contains(exercise)) {
                            selectedExercises - exercise
                        } else {
                            selectedExercises + exercise
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AssessmentButton(
            text = "Devam Et",
            onClick = { onPreferencesSelected(selectedExercises.map { it.name }) },
            enabled = selectedExercises.isNotEmpty(),
            modifier = Modifier.padding(16.dp)
        )
    }
} 