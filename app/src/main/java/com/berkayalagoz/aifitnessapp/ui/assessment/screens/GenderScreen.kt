package com.berkayalagoz.aifitnessapp.ui.assessment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.berkayalagoz.aifitnessapp.model.Gender
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentButton
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentQuestion
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentTopBar
import com.berkayalagoz.aifitnessapp.ui.assessment.components.SelectableOption

@Composable
fun GenderScreen(
    currentStep: Int,
    totalSteps: Int,
    onGenderSelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedGender by remember { mutableStateOf<Gender?>(null) }

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
            question = "What's your gender?"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Gender.values().forEach { gender ->
                SelectableOption(
                    text = when (gender) {
                        Gender.MALE -> "Erkek"
                        Gender.FEMALE -> "Kadin"
                        Gender.PREFER_NOT_TO_SAY -> "Belirtmek istemiyorum"
                    },
                    isSelected = selectedGender == gender,
                    onClick = { selectedGender = gender }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AssessmentButton(
            text = "Continue",
            onClick = { selectedGender?.let { onGenderSelected(it.name) } },
            enabled = selectedGender != null,
            modifier = Modifier.padding(16.dp)
        )
    }
} 