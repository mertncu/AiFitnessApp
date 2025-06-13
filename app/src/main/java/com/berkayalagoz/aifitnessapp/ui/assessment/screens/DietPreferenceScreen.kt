package com.berkayalagoz.aifitnessapp.ui.assessment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.berkayalagoz.aifitnessapp.model.DietPreference
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentButton
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentQuestion
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentTopBar
import com.berkayalagoz.aifitnessapp.ui.assessment.components.SelectableOption

@Composable
fun DietPreferenceScreen(
    currentStep: Int,
    totalSteps: Int,
    onDietSelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedDiet by remember { mutableStateOf<DietPreference?>(null) }

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
            question = "What's your diet preference?"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DietPreference.values().forEach { diet ->
                SelectableOption(
                    text = when (diet) {
                        DietPreference.PLANT_BASED -> "Plant-Based Diet"
                        DietPreference.CARBS_ONE -> "High-Carb Diet"
                        DietPreference.SPECIALIZED -> "Specialized Diet (Keto, etc.)"
                        DietPreference.TRADITIONAL -> "Traditional Balanced Diet"
                    },
                    isSelected = selectedDiet == diet,
                    onClick = { selectedDiet = diet }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AssessmentButton(
            text = "Continue",
            onClick = { selectedDiet?.let { onDietSelected(it.name) } },
            enabled = selectedDiet != null,
            modifier = Modifier.padding(16.dp)
        )
    }
} 