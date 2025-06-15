package com.berkayalagoz.aifitnessapp.ui.main.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkayalagoz.aifitnessapp.service.AIWorkoutRecommendation
import com.berkayalagoz.aifitnessapp.service.AIExercise
import kotlinx.coroutines.launch

@Composable
fun AIWorkoutDetailScreen(
    workout: AIWorkoutRecommendation,
    onBackClick: () -> Unit
) {
    var isStarted by remember { mutableStateOf(false) }

    if (isStarted) {
        AIActiveWorkoutScreen(
            workout = workout,
            onFinish = { isStarted = false },
            onPause = { }
        )
    } else {
        AIWorkoutDetailContent(
            workout = workout,
            onBackClick = onBackClick,
            onStartWorkout = { isStarted = true }
        )
    }
}

@Composable
private fun AIWorkoutDetailContent(
    workout: AIWorkoutRecommendation,
    onBackClick: () -> Unit,
    onStartWorkout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header with AI indication
        item {
            AIWorkoutDetailHeader(
                workout = workout,
                onBackClick = onBackClick
            )
        }

        // AI Personalization Card
        item {
            AIPersonalizationCard(workout = workout)
        }

        // Workout info section
        item {
            AIWorkoutInfoSection(workout = workout)
        }

        // Focus Areas section
        item {
            FocusAreasSection(focusAreas = workout.focusAreas)
        }

        // Equipment section
        item {
            EquipmentSection(equipment = workout.equipment)
        }

        // Exercises section
        item {
            AIExercisesSection(exercises = workout.exercises)
        }

        // Start workout button
        item {
            AIStartWorkoutButton(onStartWorkout = onStartWorkout)
        }
    }
}

@Composable
private fun AIWorkoutDetailHeader(
    workout: AIWorkoutRecommendation,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        // Background gradient with AI theme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
        )

        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.3f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // AI Badge
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.2f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Psychology,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "AI POWERED",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Workout info overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFF6B35)
            ) {
                Text(
                    text = workout.category.uppercase(),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = workout.title,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = "${workout.duration} dk",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = " • ",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = workout.difficulty,
                    color = Color(0xFFFFD700),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AIPersonalizationCard(workout: AIWorkoutRecommendation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Psychology,
                        contentDescription = "AI",
                        tint = Color(0xFF667eea),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Kişiselleştirmesi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "%${workout.personalizedScore} UYUMLU",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = workout.aiReason,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Personalization indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                PersonalizationIndicator(
                    icon = Icons.Default.FitnessCenter,
                    label = "Seviye Uyumu",
                    value = "${workout.personalizedScore}%"
                )
                PersonalizationIndicator(
                    icon = Icons.Default.Schedule,
                    label = "Süre Optimizasyonu",
                    value = "Ideal"
                )
                PersonalizationIndicator(
                    icon = Icons.Default.LocalFireDepartment,
                    label = "Kalori Hedefi",
                    value = "${workout.estimatedCalories}"
                )
            }
        }
    }
}

@Composable
private fun PersonalizationIndicator(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF667eea),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 9.sp,
            color = Color.Gray,
            maxLines = 2
        )
    }
}

@Composable
private fun AIWorkoutInfoSection(workout: AIWorkoutRecommendation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Antrenman Detayları",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AIInfoItem(
                    icon = Icons.Default.Schedule,
                    value = "${workout.duration} dk",
                    label = "Süre"
                )
                AIInfoItem(
                    icon = Icons.Default.LocalFireDepartment,
                    value = "${workout.estimatedCalories}",
                    label = "Tahmini Kalori"
                )
                AIInfoItem(
                    icon = Icons.Default.FitnessCenter,
                    value = "${workout.exercises.size}",
                    label = "Egzersiz"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = workout.description,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun FocusAreasSection(focusAreas: List<String>) {
    if (focusAreas.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Odak Alanları",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                // Focus areas as chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    focusAreas.take(3).forEach { area ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF667eea).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = area,
                                color = Color(0xFF667eea),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun EquipmentSection(equipment: List<String>) {
    if (equipment.isNotEmpty() && !equipment.contains("Hiçbiri")) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Gerekli Ekipmanlar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                equipment.forEach { item ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AIExercisesSection(exercises: List<AIExercise>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Kişiselleştirilmiş Egzersizler (${exercises.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            exercises.forEachIndexed { index, exercise ->
                AIExerciseItem(
                    exercise = exercise,
                    index = index + 1
                )
                if (index < exercises.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun AIExerciseItem(exercise: AIExercise, index: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = Color(0xFF667eea).copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = index.toString(),
                    color = Color(0xFF667eea),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exercise.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = exercise.instruction,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${exercise.sets}x${exercise.reps}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF667eea)
            )
            if (exercise.restTime > 0) {
                Text(
                    text = "${exercise.restTime}s dinlenme",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun AIStartWorkoutButton(onStartWorkout: () -> Unit) {
    Button(
        onClick = onStartWorkout,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF667eea)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            Icons.Default.Psychology,
            contentDescription = "AI",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "AI Antrenmanını Başlat",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun AIActiveWorkoutScreen(
    workout: AIWorkoutRecommendation,
    onFinish: () -> Unit,
    onPause: () -> Unit
) {
    var currentExercise by remember { mutableStateOf(0) }
    var currentSet by remember { mutableStateOf(1) }
    var timeRemaining by remember { mutableStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }

    // Timer effect
    LaunchedEffect(isTimerRunning, timeRemaining) {
        if (isTimerRunning && timeRemaining > 0) {
            kotlinx.coroutines.delay(1000)
            timeRemaining--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2)
                    )
                )
            )
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // AI Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        color = Color(0xFFFFD700),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🤖",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GEMINI AI COACHING",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Egzersiz ${currentExercise + 1}/${workout.exercises.size}",
                        fontSize = 13.sp,
                        color = Color(0xFF2E2E2E),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Set ${currentSet}/${workout.exercises[currentExercise].sets}",
                        fontSize = 13.sp,
                        color = Color(0xFF666666)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { (currentExercise + 1).toFloat() / workout.exercises.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF667eea),
                    trackColor = Color(0xFFF0F0F0)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${workout.exercises[currentExercise].reps} tekrar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E2E2E)
                )
            }
        }
        
        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Exercise name
            Text(
                text = workout.exercises[currentExercise].name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Exercise icon
            Surface(
                modifier = Modifier.size(120.dp),
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val exerciseIcon = when {
                            workout.exercises[currentExercise].name.contains("Push", ignoreCase = true) -> "💪"
                            workout.exercises[currentExercise].name.contains("Squat", ignoreCase = true) -> "🏋️"
                            workout.exercises[currentExercise].name.contains("Pull", ignoreCase = true) -> "🤸"
                            workout.exercises[currentExercise].name.contains("Lunge", ignoreCase = true) -> "🏃"
                            workout.exercises[currentExercise].name.contains("Plank", ignoreCase = true) -> "🧘"
                            workout.exercises[currentExercise].name.contains("Burpee", ignoreCase = true) -> "⚡"
                            workout.exercises[currentExercise].name.contains("Jump", ignoreCase = true) -> "🤾"
                            else -> "🏋️‍♀️"
                        }
                        
                        Text(
                            text = exerciseIcon,
                            fontSize = 40.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🤖",
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Optimized",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // AI instruction
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🤖",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Rehberlik:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = workout.exercises[currentExercise].instruction,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "💡 Gemini AI Önerisi: Bu egzersiz hedeflerinize göre optimize edilmiştir",
                        fontSize = 11.sp,
                        color = Color(0xFFFFD700).copy(alpha = 0.9f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            // Clock-style Timer
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer circle (clock face)
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2 - 20.dp.toPx()
                    
                    // Clock face background
                    drawCircle(
                        color = Color.White,
                        radius = radius,
                        center = center
                    )
                    
                    // Clock face border
                    drawCircle(
                        color = Color(0xFF667eea),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 4.dp.toPx())
                    )
                    
                    // Progress arc
                    val sweepAngle = (timeRemaining.toFloat() / 30f) * 360f
                    drawArc(
                        color = if (timeRemaining <= 5) Color(0xFFFF4757) else Color(0xFF667eea),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round),
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                    
                    // Clock marks (12, 3, 6, 9)
                    for (i in 0..3) {
                        val angle = i * 90.0
                        val markRadius = radius - 15.dp.toPx()
                        val x = center.x + markRadius * cos(Math.toRadians(angle - 90)).toFloat()
                        val y = center.y + markRadius * sin(Math.toRadians(angle - 90)).toFloat()
                        
                        drawCircle(
                            color = Color(0xFF667eea),
                            radius = 3.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
                
                // Timer text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format("%02d:%02d", timeRemaining / 60, timeRemaining % 60),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (timeRemaining <= 5) Color(0xFFFF4757) else Color(0xFF2E2E2E)
                    )
                    Text(
                        text = if (isPaused) "Duraklatıldı" else "AI TIMER",
                        fontSize = 11.sp,
                        color = Color(0xFF667eea),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }
        
        // Bottom controls
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Pause/Resume button
                    Button(
                        onClick = {
                            isPaused = !isPaused
                            isTimerRunning = !isPaused
                            if (!isPaused) onPause() else {}
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPaused) Color(0xFF4CAF50) else Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            tint = if (isPaused) Color.White else Color(0xFF2E2E2E),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPaused) "Devam" else "Duraklat",
                            color = if (isPaused) Color.White else Color(0xFF2E2E2E),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                    
                    // Next/Finish button
                    Button(
                        onClick = {
                            if (currentSet < workout.exercises[currentExercise].sets) {
                                currentSet++
                                timeRemaining = workout.exercises[currentExercise].restTime
                                isPaused = false
                                isTimerRunning = true
                            } else if (currentExercise < workout.exercises.size - 1) {
                                currentExercise++
                                currentSet = 1
                                timeRemaining = workout.exercises[currentExercise].restTime
                                isPaused = false
                                isTimerRunning = true
                            } else {
                                onFinish()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF667eea)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            if (currentExercise == workout.exercises.size - 1 && currentSet == workout.exercises[currentExercise].sets) 
                                Icons.Default.Check else Icons.Default.SkipNext,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentExercise == workout.exercises.size - 1 && currentSet == workout.exercises[currentExercise].sets) 
                                "Bitir" else "Sonraki",
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Stop workout button
                OutlinedButton(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF4757)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFF4757)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Stop,
                        contentDescription = null,
                        tint = Color(0xFFFF4757),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Antrenmanını Sonlandır",
                        color = Color(0xFFFF4757),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AIInfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF667eea),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
} 