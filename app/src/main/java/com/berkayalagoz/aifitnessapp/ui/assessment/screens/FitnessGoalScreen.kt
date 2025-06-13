package com.berkayalagoz.aifitnessapp.ui.assessment.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.berkayalagoz.aifitnessapp.R
import com.berkayalagoz.aifitnessapp.model.FitnessGoal
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentButton
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentQuestion
import com.berkayalagoz.aifitnessapp.ui.assessment.components.AssessmentTopBar
import com.berkayalagoz.aifitnessapp.ui.assessment.components.SelectableOption

@Composable
fun FitnessGoalScreen(
    currentStep: Int,
    totalSteps: Int,
    onGoalSelected: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedGoal by remember { mutableStateOf<FitnessGoal?>(null) }

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
            question = stringResource(R.string.fitness_goal_question)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FitnessGoal.values().forEach { goal ->
                SelectableOption(
                    text = when (goal) {
                        FitnessGoal.LOSE_WEIGHT -> stringResource(R.string.goal_lose_weight)
                        FitnessGoal.TRY_AI_COACH -> stringResource(R.string.goal_try_ai_coach)
                        FitnessGoal.GET_BULK -> stringResource(R.string.goal_get_bulk)
                        FitnessGoal.GAIN_ENDURANCE -> stringResource(R.string.goal_gain_endurance)
                        FitnessGoal.TRYING_APP -> stringResource(R.string.goal_trying_app)
                    },
                    isSelected = selectedGoal == goal,
                    onClick = { selectedGoal = goal }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        AssessmentButton(
            text = stringResource(R.string.continue_button),
            onClick = { selectedGoal?.let { onGoalSelected(it.name) } },
            enabled = selectedGoal != null,
            modifier = Modifier.padding(16.dp)
        )
    }
} 