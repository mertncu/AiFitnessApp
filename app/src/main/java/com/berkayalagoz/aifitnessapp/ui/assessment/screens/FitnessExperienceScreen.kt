package com.berkayalagoz.aifitnessapp.ui.assessment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentButton
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentQuestion
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentTopBar
import com.berkayalagoz.aifitnessapp.ui.assessment.components.SelectableOption

@Composable
fun FitnessExperienceScreen(
    currentStep: Int,
    totalSteps: Int,
    onExperienceSelected: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    var hasExperience by remember { mutableStateOf<Boolean?>(null) }

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
            question = "Daha oncesinde fitness deneyiminiz oldu mu?"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SelectableOption(
                text = "Yes, I have worked out before",
                isSelected = hasExperience == true,
                onClick = { hasExperience = true }
            )
            
            SelectableOption(
                text = "No, I'm new to fitness",
                isSelected = hasExperience == false,
                onClick = { hasExperience = false }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AssessmentButton(
            text = "Continue",
            onClick = { hasExperience?.let { onExperienceSelected(it) } },
            enabled = hasExperience != null,
            modifier = Modifier.padding(16.dp)
        )
    }
} 