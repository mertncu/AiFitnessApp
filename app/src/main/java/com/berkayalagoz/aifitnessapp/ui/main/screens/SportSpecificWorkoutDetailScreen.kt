package com.berkayalagoz.aifitnessapp.ui.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.berkayalagoz.aifitnessapp.service.SportSpecificWorkout
import com.berkayalagoz.aifitnessapp.service.SportExercise
import com.berkayalagoz.aifitnessapp.model.ExerciseType

@Composable
fun SportSpecificWorkoutDetailScreen(
    workout: SportSpecificWorkout,
    onBackClick: () -> Unit
) {
    var isStarted by remember { mutableStateOf(false) }

    if (isStarted) {
        SportSpecificActiveWorkoutScreen(
            workout = workout,
            onFinish = { isStarted = false },
            onPause = { }
        )
    } else {
        SportSpecificWorkoutDetailContent(
            workout = workout,
            onBackClick = onBackClick,
            onStartWorkout = { isStarted = true }
        )
    }
}

@Composable
private fun SportSpecificWorkoutDetailContent(
    workout: SportSpecificWorkout,
    onBackClick: () -> Unit,
    onStartWorkout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header with sport theme
        item {
            SportSpecificWorkoutDetailHeader(
                workout = workout,
                onBackClick = onBackClick
            )
        }

        // Sport specialization card
        item {
            SportSpecializationCard(workout = workout)
        }

        // Workout info section
        item {
            SportSpecificWorkoutInfoSection(workout = workout)
        }

        // Focus Areas section
        item {
            SportSpecificFocusAreasSection(focusAreas = workout.focusAreas)
        }

        // Equipment section
        item {
            SportSpecificEquipmentSection(equipment = workout.equipment)
        }

        // Exercises section
        item {
            SportSpecificExercisesSection(exercises = workout.exercises)
        }

        // Start workout button
        item {
            SportSpecificStartWorkoutButton(
                sportType = workout.sportType,
                onStartWorkout = onStartWorkout
            )
        }
    }
}

@Composable
private fun SportSpecificWorkoutDetailHeader(
    workout: SportSpecificWorkout,
    onBackClick: () -> Unit
) {
    val sportColors = getSportColors(workout.sportType)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        // Background gradient with sport theme
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = sportColors
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

        // Sport Badge
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
                Text(
                    text = getSportEmoji(workout.sportType),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = getSportName(workout.sportType),
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
                    text = workout.difficulty.uppercase(),
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
                    text = "${workout.calories} kalori",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun SportSpecializationCard(workout: SportSpecificWorkout) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getSportEmoji(workout.sportType),
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Spor Özel Antrenmanı",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${getSportName(workout.sportType)} için optimize edilmiş",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
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
private fun SportSpecificWorkoutInfoSection(workout: SportSpecificWorkout) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SportSpecificInfoItem(
                icon = Icons.Default.Timer,
                value = "${workout.duration}",
                label = "Dakika"
            )
            SportSpecificInfoItem(
                icon = Icons.Default.LocalFireDepartment,
                value = "${workout.calories}",
                label = "Kalori"
            )
            SportSpecificInfoItem(
                icon = Icons.Default.TrendingUp,
                value = workout.difficulty,
                label = "Seviye"
            )
            SportSpecificInfoItem(
                icon = Icons.Default.FitnessCenter,
                value = "${workout.exercises.size}",
                label = "Egzersiz"
            )
        }
    }
}

@Composable
private fun SportSpecificActiveWorkoutScreen(
    workout: SportSpecificWorkout,
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

    val sportColors = getSportColors(workout.sportType)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colors = sportColors)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sport indicator
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = getSportEmoji(workout.sportType),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${getSportName(workout.sportType)} ANTRENMAN",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))

            // Status bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Egzersiz ${currentExercise + 1}/${workout.exercises.size}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        LinearProgressIndicator(
                            progress = (currentExercise + 1).toFloat() / workout.exercises.size,
                            modifier = Modifier
                                .width(140.dp)
                                .height(6.dp),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Set $currentSet/${workout.exercises[currentExercise].sets}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "${workout.exercises[currentExercise].reps} tekrar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Exercise name
            Text(
                text = workout.exercises[currentExercise].name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Exercise demonstration
            Card(
                modifier = Modifier
                    .size(200.dp, 150.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = getSportEmoji(workout.sportType),
                            fontSize = 56.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Spor Özel",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Exercise instruction
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Nasıl Yapılır:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = workout.exercises[currentExercise].instruction,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Timer
            Card(
                modifier = Modifier.size(160.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (timeRemaining <= 5) Color(0xFFFF4757) else Color.White
                ),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isPaused) {
                            Icon(
                                Icons.Default.Pause,
                                contentDescription = null,
                                tint = if (timeRemaining <= 5) Color.White else sportColors[0],
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text(
                            text = String.format("%02d:%02d", timeRemaining / 60, timeRemaining % 60),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (timeRemaining <= 5) Color.White else sportColors[0]
                        )
                        Text(
                            text = if (isPaused) "DURAKLATILDI" else "KALAN SÜRE",
                            fontSize = 10.sp,
                            color = if (timeRemaining <= 5) Color.White.copy(alpha = 0.8f) else sportColors[0].copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Control buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pause/Resume button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable {
                            isPaused = !isPaused
                            isTimerRunning = !isPaused
                            if (!isPaused) onPause() else {}
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isPaused) "Devam" else "Duraklat",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                
                // Next/Finish button
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clickable {
                            if (currentSet < workout.exercises[currentExercise].sets) {
                                currentSet++
                                timeRemaining = workout.exercises[currentExercise].restTime
                            } else if (currentExercise < workout.exercises.size - 1) {
                                currentExercise++
                                currentSet = 1
                                timeRemaining = workout.exercises[currentExercise].restTime
                            } else {
                                onFinish()
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                if (currentExercise == workout.exercises.size - 1 && currentSet == workout.exercises[currentExercise].sets) 
                                    Icons.Default.Check else Icons.Default.SkipNext,
                                contentDescription = null,
                                tint = sportColors[0],
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentExercise == workout.exercises.size - 1 && currentSet == workout.exercises[currentExercise].sets) 
                                    "Bitir" else "Sonraki",
                                color = sportColors[0],
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Emergency stop button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { onFinish() },
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF4757).copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Antrenmanı Sonlandır",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SportSpecificFocusAreasSection(focusAreas: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Odak Alanları",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            focusAreas.forEach { area ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = Color(0xFFFF6B35)
                    ) {}
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = area,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun SportSpecificEquipmentSection(equipment: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Build,
                    contentDescription = null,
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Gerekli Ekipmanlar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            equipment.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = Color(0xFFFF6B35)
                    ) {}
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun SportSpecificExercisesSection(exercises: List<SportExercise>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Egzersizler",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            exercises.forEachIndexed { index, exercise ->
                SportSpecificExerciseItem(
                    exercise = exercise,
                    index = index + 1
                )
                if (index < exercises.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SportSpecificExerciseItem(
    exercise: SportExercise,
    index: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = Color(0xFFFF6B35).copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = index.toString(),
                    color = Color(0xFFFF6B35),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = exercise.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${exercise.sets} set × ${exercise.reps} tekrar",
                fontSize = 12.sp,
                color = Color.Gray
            )
            if (exercise.instruction.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = exercise.instruction,
                    fontSize = 12.sp,
                    color = Color.Gray.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun SportSpecificStartWorkoutButton(
    sportType: ExerciseType,
    onStartWorkout: () -> Unit
) {
    val sportColors = getSportColors(sportType)
    
    Button(
        onClick = onStartWorkout,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = sportColors[0]
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = getSportEmoji(sportType),
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${getSportName(sportType)} Antrenmanını Başlat",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun SportSpecificInfoItem(
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
            tint = Color(0xFFFF6B35),
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

// Helper functions
private fun getSportColors(sportType: ExerciseType): List<Color> {
    return when (sportType) {
        ExerciseType.FOOTBALL -> listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
        ExerciseType.BASKETBALL -> listOf(Color(0xFFFF9800), Color(0xFFE65100))
        ExerciseType.MARTIAL_ARTS -> listOf(Color(0xFF9C27B0), Color(0xFF4A148C))
        ExerciseType.TENNIS -> listOf(Color(0xFF8BC34A), Color(0xFF33691E))
        ExerciseType.VOLLEYBALL -> listOf(Color(0xFF03A9F4), Color(0xFF01579B))
        ExerciseType.BADMINTON -> listOf(Color(0xFFE91E63), Color(0xFF880E4F))
        ExerciseType.WRESTLING -> listOf(Color(0xFF795548), Color(0xFF3E2723))
        ExerciseType.FITNESS -> listOf(Color(0xFFFF6B35), Color(0xFFD84315))
    }
}

private fun getSportEmoji(sportType: ExerciseType): String {
    return when (sportType) {
        ExerciseType.FOOTBALL -> "⚽"
        ExerciseType.BASKETBALL -> "🏀"
        ExerciseType.MARTIAL_ARTS -> "🥋"
        ExerciseType.TENNIS -> "🎾"
        ExerciseType.VOLLEYBALL -> "🏐"
        ExerciseType.BADMINTON -> "🏸"
        ExerciseType.WRESTLING -> "🤼"
        ExerciseType.FITNESS -> "💪"
    }
}

private fun getSportName(sportType: ExerciseType): String {
    return when (sportType) {
        ExerciseType.FOOTBALL -> "FUTBOL"
        ExerciseType.BASKETBALL -> "BASKETBOL"
        ExerciseType.MARTIAL_ARTS -> "DÖVÜŞ SANATLARI"
        ExerciseType.TENNIS -> "TENİS"
        ExerciseType.VOLLEYBALL -> "VOLEYBOL"
        ExerciseType.BADMINTON -> "BADMİNTON"
        ExerciseType.WRESTLING -> "GÜREŞ"
        ExerciseType.FITNESS -> "FITNESS"
    }
} 