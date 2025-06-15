package com.berkayalagoz.aifitnessapp.ui.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
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
import kotlinx.coroutines.launch

@Composable
fun WorkoutDetailScreen(
    workoutId: String,
    onBackClick: () -> Unit
) {
    var workout by remember { mutableStateOf<WorkoutDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isStarted by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Load workout detail
    LaunchedEffect(workoutId) {
        scope.launch {
            isLoading = true
            workout = loadWorkoutDetail(workoutId)
            isLoading = false
        }
    }

    if (isLoading) {
        LoadingWorkoutDetail()
    } else {
        workout?.let { workoutDetail ->
            if (isStarted) {
                ActiveWorkoutScreen(
                    workout = workoutDetail,
                    onFinish = { isStarted = false },
                    onPause = { }
                )
            } else {
                WorkoutDetailContent(
                    workout = workoutDetail,
                    onBackClick = onBackClick,
                    onStartWorkout = { isStarted = true }
                )
            }
        }
    }
}

@Composable
private fun LoadingWorkoutDetail() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFFF6B35),
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun WorkoutDetailContent(
    workout: WorkoutDetail,
    onBackClick: () -> Unit,
    onStartWorkout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header with back button and workout image
        item {
            WorkoutDetailHeader(
                workout = workout,
                onBackClick = onBackClick
            )
        }

        // Workout info section
        item {
            WorkoutInfoSection(workout = workout)
        }

        // Equipment section
        item {
            EquipmentSection(equipment = workout.equipment)
        }

        // Exercises section
        item {
            ExercisesSection(exercises = workout.exercises)
        }

        // Start workout button
        item {
            StartWorkoutButton(onStartWorkout = onStartWorkout)
        }
    }
}

@Composable
private fun WorkoutDetailHeader(
    workout: WorkoutDetail,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2C2C2C),
                            Color(0xFF1C1C1C)
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
                    color = Color(0xFFFF6B35),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun WorkoutInfoSection(workout: WorkoutDetail) {
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
                text = "Antrenman Bilgileri",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InfoItem(
                    icon = Icons.Default.Schedule,
                    value = "${workout.duration} dk",
                    label = "Süre"
                )
                InfoItem(
                    icon = Icons.Default.LocalFireDepartment,
                    value = "${workout.calories}",
                    label = "Kalori"
                )
                InfoItem(
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
private fun InfoItem(
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

@Composable
private fun EquipmentSection(equipment: List<String>) {
    if (equipment.isNotEmpty()) {
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
                            tint = Color(0xFFFF6B35),
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
private fun ExercisesSection(exercises: List<Exercise>) {
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
                text = "Egzersizler (${exercises.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            exercises.forEachIndexed { index, exercise ->
                ExerciseItem(
                    exercise = exercise,
                    index = index + 1
                )
                if (index < exercises.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color.Gray.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ExerciseMetric(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun ExerciseItem(exercise: Exercise, index: Int) {
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
                color = Color(0xFFFF6B35)
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
private fun StartWorkoutButton(onStartWorkout: () -> Unit) {
    Button(
        onClick = onStartWorkout,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6B35)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            Icons.Default.PlayArrow,
            contentDescription = "Start",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Antrenmanı Başlat",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
private fun ActiveWorkoutScreen(
    workout: WorkoutDetail,
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
            .background(Color(0xFF2E2E2E))
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = workout.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E2E2E)
                    )
                    
                    IconButton(
                        onClick = onPause,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFFF5F5F5),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Pause,
                            contentDescription = "Duraklat",
                            tint = Color(0xFF2E2E2E),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LinearProgressIndicator(
                    progress = { (currentExercise + 1).toFloat() / workout.exercises.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF2E2E2E),
                    trackColor = Color(0xFFF0F0F0)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Egzersiz ${currentExercise + 1} / ${workout.exercises.size} • Set $currentSet/${workout.exercises[currentExercise].sets}",
                    fontSize = 12.sp,
                    color = Color(0xFF666666)
                )
            }
        }
        
        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "Egzersiz ${currentExercise + 1}/${workout.exercises.size}",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = workout.exercises[currentExercise].name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = workout.exercises[currentExercise].instruction,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                ExerciseMetric(
                    label = "Set",
                    value = "${currentSet}/${workout.exercises[currentExercise].sets}",
                    icon = "🔄"
                )
                ExerciseMetric(
                    label = "Tekrar",
                    value = workout.exercises[currentExercise].reps.toString(),
                    icon = "💪"
                )
                ExerciseMetric(
                    label = "Dinlenme",
                    value = "${workout.exercises[currentExercise].restTime}s",
                    icon = "⏱️"
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Timer
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = String.format("%02d:%02d", timeRemaining / 60, timeRemaining % 60),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isPaused) "Duraklatıldı" else "Dinlenme",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

        }
        
        // Bottom controls
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White
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
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = null,
                            tint = Color(0xFF2E2E2E),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isPaused) "Devam" else "Duraklat",
                            color = Color(0xFF2E2E2E),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Next/Finish button
                    Button(
                        onClick = {
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
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E2E2E)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            if (currentExercise == workout.exercises.size - 1 && currentSet == workout.exercises[currentExercise].sets) 
                                Icons.Default.Check else Icons.Default.SkipNext,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentExercise == workout.exercises.size - 1 && currentSet == workout.exercises[currentExercise].sets) 
                                "Bitir" else "Sonraki",
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Stop workout button
                TextButton(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Antrenmanı Sonlandır",
                        color = Color(0xFFFF4757),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Data classes
data class WorkoutDetail(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val difficulty: String,
    val duration: Int,
    val calories: Int,
    val equipment: List<String>,
    val exercises: List<Exercise>
)

data class Exercise(
    val name: String,
    val instruction: String,
    val sets: Int,
    val reps: Int,
    val restTime: Int,
    val imageUrl: String = ""
)

// Sample data loading function
private suspend fun loadWorkoutDetail(workoutId: String): WorkoutDetail {
    // Simulate API call
    kotlinx.coroutines.delay(1000)
    
    return when (workoutId) {
        "upper_body" -> WorkoutDetail(
            id = "upper_body",
            title = "Üst Vücut Antrenmanı",
            description = "Göğüs, omuz ve kol kaslarınızı güçlendiren kapsamlı bir antrenman programı. Hem başlangıç hem de orta seviye sporcular için uygundur.",
            category = "Kuvvet",
            difficulty = "Orta",
            duration = 45,
            calories = 350,
            equipment = listOf("Dambıl", "Barbell", "Bench"),
            exercises = listOf(
                Exercise("Push-up", "Göğüs üstünde dur ve vücut düz tut", 3, 12, 60),
                Exercise("Dumbbell Press", "Dambılları göğüs hizasından yukarı kaldır", 4, 10, 90),
                Exercise("Pull-up", "Barı kavra ve kendini yukarı çek", 3, 8, 120),
                Exercise("Shoulder Press", "Dambılları omuz hizasından yukarı kaldır", 3, 12, 60)
            )
        )
        "lower_body" -> WorkoutDetail(
            id = "lower_body",
            title = "Alt Vücut Antrenmanı",
            description = "Bacak ve kalça kaslarınızı hedefleyen etkili egzersizler içeren tam bir alt vücut rutini.",
            category = "Kuvvet",
            difficulty = "Orta",
            duration = 40,
            calories = 300,
            equipment = listOf("Barbell", "Dumbbell"),
            exercises = listOf(
                Exercise("Squat", "Ayakları omuz genişliğinde aç ve çömel", 4, 15, 90),
                Exercise("Deadlift", "Barı yerden kaldır, sırt düz", 4, 10, 120),
                Exercise("Lunges", "Bir ayağı öne at ve çömel", 3, 12, 60),
                Exercise("Calf Raises", "Ayak parmaklarında yüksel", 3, 20, 45)
            )
        )
        else -> WorkoutDetail(
            id = "full_body",
            title = "Total Body Circuit",
            description = "Tüm vücut kaslarını çalıştıran yoğun bir circuit antrenmanı. Kondisyon ve kuvvet gelişimi için ideal.",
            category = "Full Body",
            difficulty = "İleri",
            duration = 50,
            calories = 450,
            equipment = listOf("Dumbbell", "Kettlebell", "Mat"),
            exercises = listOf(
                Exercise("Burpees", "Tam vücut hareketi, atlama ve squat kombinasyonu", 3, 10, 90),
                Exercise("Mountain Climbers", "Plank pozisyonunda dizleri göğse çek", 3, 20, 60),
                Exercise("Russian Twists", "Oturur pozisyonda gövdeyi sağa sola çevir", 3, 30, 45),
                Exercise("Jumping Jacks", "Zıplayarak kol ve bacakları aç kapat", 3, 25, 45)
            )
        )
    }
} 