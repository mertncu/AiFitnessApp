package com.berkayalagoz.aifitnessapp.ui.assessment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.berkayalagoz.aifitnessapp.model.PhysicalLimitation
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentButton
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentQuestion
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentTopBar
import com.berkayalagoz.aifitnessapp.ui.assessment.components.SelectableOption

@Composable
fun PhysicalLimitationsScreen(
    currentStep: Int,
    totalSteps: Int,
    onLimitationsSelected: (List<String>) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedLimitations by remember { mutableStateOf(setOf<PhysicalLimitation>()) }

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
            question = "Do you have any physical limitations?"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PhysicalLimitation.values().forEach { limitation ->
                SelectableOption(
                    text = when (limitation) {
                        PhysicalLimitation.ARTHRITIS -> "Arthritis"
                        PhysicalLimitation.BACK_PAIN -> "Back Pain"
                        PhysicalLimitation.KNEE_PAIN -> "Knee Pain"
                        PhysicalLimitation.OBESITY -> "Obesity"
                        PhysicalLimitation.NONE -> "No Limitations"
                    },
                    isSelected = selectedLimitations.contains(limitation),
                    onClick = {
                        selectedLimitations = if (limitation == PhysicalLimitation.NONE) {
                            if (selectedLimitations.contains(PhysicalLimitation.NONE)) {
                                emptySet()
                            } else {
                                setOf(PhysicalLimitation.NONE)
                            }
                        } else {
                            if (selectedLimitations.contains(limitation)) {
                                selectedLimitations - limitation
                            } else {
                                (selectedLimitations + limitation) - PhysicalLimitation.NONE
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AssessmentButton(
            text = "Continue",
            onClick = { 
                onLimitationsSelected(selectedLimitations.map { it.name })
            },
            enabled = selectedLimitations.isNotEmpty(),
            modifier = Modifier.padding(16.dp)
        )
    }
} 