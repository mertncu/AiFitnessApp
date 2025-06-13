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
fun WeeklyWorkoutDaysScreen(
    currentStep: Int,
    totalSteps: Int,
    onDaysSelected: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    var sliderPosition by remember { mutableStateOf(3f) }

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
            question = "How many days per week do you want to work out?"
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${sliderPosition.toInt()} days",
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryOrange
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when (sliderPosition.toInt()) {
                    1, 2 -> "Light commitment, perfect for beginners"
                    3, 4 -> "Balanced schedule for steady progress"
                    5 -> "Dedicated routine for optimal results"
                    else -> "Intense schedule for maximum gains"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 1f..7f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryOrange,
                    activeTrackColor = PrimaryOrange
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AssessmentButton(
            text = "Continue",
            onClick = { onDaysSelected(sliderPosition.toInt()) },
            modifier = Modifier.padding(16.dp)
        )
    }
} 