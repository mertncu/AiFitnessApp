package com.berkayalagoz.aifitnessapp.ui.assessment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.berkayalagoz.aifitnessapp.R
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentButton
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentQuestion
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeightScreen(
    currentStep: Int,
    totalSteps: Int,
    onWeightEntered: (Float) -> Unit,
    onBackClick: () -> Unit
) {
    var weightText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

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
            question = stringResource(R.string.weight_question)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            TextField(
                value = weightText,
                onValueChange = {
                    weightText = it
                    isError = false
                },
                label = { Text(stringResource(R.string.weight_hint)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = isError,
                supportingText = if (isError) {
                    { Text(stringResource(R.string.weight_error)) }
                } else null,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AssessmentButton(
            text = stringResource(R.string.continue_button),
            onClick = {
                val weight = weightText.toFloatOrNull()
                if (weight != null && weight in 30f..300f) {
                    onWeightEntered(weight)
                } else {
                    isError = true
                }
            },
            enabled = weightText.isNotEmpty(),
            modifier = Modifier.padding(16.dp)
        )
    }
} 