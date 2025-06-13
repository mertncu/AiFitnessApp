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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status bar with workout info
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.1f)
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
                                .width(120.dp)
                                .height(6.dp),
                            color = Color(0xFFFF6B35),
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

            // Exercise demonstration image
            Card(
                modifier = Modifier
                    .size(200.dp, 150.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
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
                        // Exercise icon based on type
                        val exerciseIcon = when {
                            workout.exercises[currentExercise].name.contains("Push", ignoreCase = true) -> "💪"
                            workout.exercises[currentExercise].name.contains("Squat", ignoreCase = true) -> "🏋️"
                            workout.exercises[currentExercise].name.contains("Pull", ignoreCase = true) -> "🤸"
                            workout.exercises[currentExercise].name.contains("Lunge", ignoreCase = true) -> "🏃"
                            workout.exercises[currentExercise].name.contains("Plank", ignoreCase = true) -> "🧘"
                            workout.exercises[currentExercise].name.contains("Burpee", ignoreCase = true) -> "⚡"
                            workout.exercises[currentExercise].name.contains("Jump", ignoreCase = true) -> "🤾"
                            workout.exercises[currentExercise].name.contains("Futbol", ignoreCase = true) -> "⚽"
                            workout.exercises[currentExercise].name.contains("Basketbol", ignoreCase = true) -> "🏀"
                            workout.exercises[currentExercise].name.contains("Dövüş", ignoreCase = true) -> "🥋"
                            workout.exercises[currentExercise].name.contains("Tenis", ignoreCase = true) -> "🎾"
                            workout.exercises[currentExercise].name.contains("Voleybol", ignoreCase = true) -> "🏐"
                            workout.exercises[currentExercise].name.contains("Badminton", ignoreCase = true) -> "🏸"
                            workout.exercises[currentExercise].name.contains("Güreş", ignoreCase = true) -> "🤼"
                            workout.exercises[currentExercise].name.contains("Fitness", ignoreCase = true) -> "💪"
                            else -> "🏋️‍♀️"
                        }
                        
                        Text(
                            text = exerciseIcon,
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hareket Gösterimi",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Exercise instruction
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFF6B35).copy(alpha = 0.2f)
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
                            tint = Color(0xFFFF6B35),
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

            // Timer with modern design
            Card(
                modifier = Modifier.size(160.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (timeRemaining <= 5) Color(0xFFFF4757) else Color(0xFFFF6B35)
                ),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
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
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        Text(
                            text = String.format("%02d:%02d", timeRemaining / 60, timeRemaining % 60),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isPaused) "DURAKLATILDI" else "KALAN SÜRE",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Modern control buttons
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
                        containerColor = Color.White.copy(alpha = 0.15f)
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
                        containerColor = Color(0xFFFF6B35)
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
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentExercise == workout.exercises.size - 1 && currentSet == workout.exercises[currentExercise].sets) 
                                    "Bitir" else "Sonraki",
                                color = Color.White,
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