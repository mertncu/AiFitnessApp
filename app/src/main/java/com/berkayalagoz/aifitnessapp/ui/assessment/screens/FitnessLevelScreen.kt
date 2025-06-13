package com.berkayalagoz.aifitnessapp.ui.assessment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentButton
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentQuestion
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentTopBar
import com.berkayalagoz.aifitnessapp.ui.theme.PrimaryOrange
import com.berkayalagoz.aifitnessapp.ui.theme.TextSecondary

@Composable
fun FitnessLevelScreen(
    currentStep: Int,
    totalSteps: Int,
    onLevelSelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    var sliderPosition by remember { mutableStateOf(0f) }

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
            question = "What's your current fitness level?"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (sliderPosition.toInt()) {
                    0 -> "Beginner"
                    1 -> "Novice"
                    2 -> "Intermediate"
                    3 -> "Advanced"
                    else -> "Expert"
                },
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryOrange
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (sliderPosition.toInt()) {
                    0 -> "Just starting my fitness journey"
                    1 -> "Have some basic knowledge"
                    2 -> "Regular workout routine"
                    3 -> "Consistent training for years"
                    else -> "Professional level fitness"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 0f..4f,
                steps = 3,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryOrange,
                    activeTrackColor = PrimaryOrange
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AssessmentButton(
            text = "Continue",
            onClick = { onLevelSelected(sliderPosition.toInt()) },
            modifier = Modifier.padding(16.dp)
        )
    }
} 