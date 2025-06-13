package com.berkayalagoz.aifitnessapp.ui.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkayalagoz.aifitnessapp.service.*
import com.berkayalagoz.aifitnessapp.model.UserProfile
import com.berkayalagoz.aifitnessapp.model.ExerciseType
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

@Composable
fun WorkoutScreen(
    workoutPlan: List<WorkoutRecommendation>,
    onSignOutClick: () -> Unit
) {
    var selectedWorkoutId by remember { mutableStateOf<String?>(null) }
    
    selectedWorkoutId?.let { workoutId ->
        WorkoutDetailScreen(
            workoutId = workoutId,
            onBackClick = { selectedWorkoutId = null }
        )
    } ?: run {
        WorkoutListContent(
            onWorkoutClick = { workoutId -> selectedWorkoutId = workoutId }
        )
    }
}

@Composable
fun WorkoutScreen(
    userProfile: UserProfile,
    onSignOutClick: () -> Unit
) {
    var selectedWorkoutId by remember { mutableStateOf<String?>(null) }
    
    selectedWorkoutId?.let { workoutId ->
        WorkoutDetailScreen(
            workoutId = workoutId,
            onBackClick = { selectedWorkoutId = null }
        )
    } ?: run {
        ModernWorkoutListContent(
            userProfile = userProfile,
            onWorkoutClick = { workoutId -> selectedWorkoutId = workoutId }
        )
    }
}

@Composable
private fun WorkoutListContent(
    onWorkoutClick: (String) -> Unit
) {
    val aiWorkoutService = remember { AIWorkoutService() }
    val scope = rememberCoroutineScope()
    
    var aiWorkouts by remember { mutableStateOf<List<AIWorkoutRecommendation>>(emptyList()) }
    var selectedAIWorkout by remember { mutableStateOf<AIWorkoutRecommendation?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    selectedAIWorkout?.let { workout ->
        AIWorkoutDetailScreen(
            workout = workout,
            onBackClick = { selectedAIWorkout = null }
        )
        return
    }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val dummyProfile = UserProfile(
                    name = "Test User",
                    email = "test@example.com",
                    age = 25,
                    gender = "Erkek", 
                    height = 175f,
                    weight = 70f,
                    fitnessGoal = "LOSE_WEIGHT",
                    location = "Istanbul"
                )
                aiWorkouts = aiWorkoutService.generatePersonalizedWorkouts(dummyProfile)
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFFFF6B35),
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    ModernWorkoutListContent(
        userProfile = UserProfile(),
        onWorkoutClick = onWorkoutClick
    )
}

@Composable
private fun ModernWorkoutListContent(
    userProfile: UserProfile,
    onWorkoutClick: (String) -> Unit
) {
    val aiWorkoutService = remember { AIWorkoutService() }
    val sportSpecificWorkoutService = remember { SportSpecificWorkoutService() }
    val scope = rememberCoroutineScope()
    
    var aiWorkouts by remember { mutableStateOf<List<AIWorkoutRecommendation>>(emptyList()) }
    var sportSpecificWorkouts by remember { mutableStateOf<List<SportSpecificWorkout>>(emptyList()) }
    var selectedAIWorkout by remember { mutableStateOf<AIWorkoutRecommendation?>(null) }
    var selectedSportWorkout by remember { mutableStateOf<SportSpecificWorkout?>(null) }
    var isAILoading by remember { mutableStateOf(true) }
    var isSportLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    selectedAIWorkout?.let { workout ->
        AIWorkoutDetailScreen(
            workout = workout,
            onBackClick = { selectedAIWorkout = null }
        )
        return
    }
    
    selectedSportWorkout?.let { workout ->
        SportSpecificWorkoutDetailScreen(
            workout = workout,
            onBackClick = { selectedSportWorkout = null }
        )
        return
    }
    
    LaunchedEffect(userProfile.userId) {
        // Launch both data loading operations in parallel
        val aiWorkoutsJob = scope.async {
            try {
                val result = aiWorkoutService.generatePersonalizedWorkouts(userProfile)
                isAILoading = false
                result
            } catch (e: Exception) {
                println("DEBUG: AI workouts failed: ${e.message}")
                isAILoading = false
                emptyList()
            }
        }
        
        val sportWorkoutsJob = scope.async {
            try {
                val result = sportSpecificWorkoutService.generateSportSpecificWorkouts(userProfile)
                isSportLoading = false
                result
            } catch (e: Exception) {
                println("DEBUG: Sport workouts failed: ${e.message}")
                isSportLoading = false
                emptyList()
            }
        }
        
        try {
            errorMessage = null
            
            // Update state as each operation completes
            aiWorkouts = aiWorkoutsJob.await()
            sportSpecificWorkouts = sportWorkoutsJob.await()
            
            // Debug: Log the loaded workouts
            println("DEBUG: Loaded ${aiWorkouts.size} AI workouts")
            println("DEBUG: Loaded ${sportSpecificWorkouts.size} sport-specific workouts")
            println("DEBUG: User exercise preferences: ${userProfile.exercisePreferences}")
            
        } catch (e: Exception) {
            errorMessage = "Antrenmanlar yüklenemedi: ${e.message}"
            // Fallback to empty lists
            aiWorkouts = emptyList()
            sportSpecificWorkouts = emptyList()
            isAILoading = false
            isSportLoading = false
        }
    }

    // Show partial loading - sport workouts can appear before AI workouts
    if (isAILoading && isSportLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFFFF6B35),
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color(0xFFE3F2FD)
                    )
                )
            )
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Featured Section - Show first AI workout as featured with loading state
        item {
            FeaturedSection(
                workout = if (isAILoading) null else aiWorkouts.firstOrNull(),
                isLoading = isAILoading,
                onWorkoutClick = { aiWorkouts.firstOrNull()?.let { selectedAIWorkout = it } }
            )
        }
        
        // Strength Section - Show sport-specific workouts with loading state
        item {
            StrengthSection(
                sportWorkouts = if (isSportLoading) emptyList() else sportSpecificWorkouts,
                totalCount = if (isSportLoading) 0 else sportSpecificWorkouts.size,
                isLoading = isSportLoading,
                onWorkoutClick = { workout -> selectedSportWorkout = workout }
            )
        }
        
        // AI Suggestion Section - Show second AI workout with loading state
        item {
            AISuggestionSection(
                workout = if (isAILoading) null else aiWorkouts.getOrNull(1),
                isLoading = isAILoading,
                onWorkoutClick = { aiWorkouts.getOrNull(1)?.let { selectedAIWorkout = it } }
            )
        }
        
        // My Workouts Section - Mix of AI and sport workouts with loading states
        item {
            MyWorkoutsSection(
                aiWorkouts = if (isAILoading) emptyList() else aiWorkouts,
                sportWorkouts = if (isSportLoading) emptyList() else sportSpecificWorkouts,
                isAILoading = isAILoading,
                isSportLoading = isSportLoading,
                onAIWorkoutClick = { workout -> selectedAIWorkout = workout },
                onSportWorkoutClick = { workout -> selectedSportWorkout = workout }
            )
        }
        
        // Debug info if no workouts loaded
        if (aiWorkouts.isEmpty() && sportSpecificWorkouts.isEmpty() && !isAILoading && !isSportLoading) {
            item {
                DebugInfoCard(userProfile = userProfile)
            }
        }
    }
}

@Composable
private fun FeaturedSection(
    workout: AIWorkoutRecommendation?,
    isLoading: Boolean,
    onWorkoutClick: () -> Unit
) {
    Column {
        SectionHeader(title = "Featured", onSeeAllClick = { })
        Spacer(modifier = Modifier.height(20.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clickable { if (!isLoading && workout != null) onWorkoutClick() },
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            if (isLoading) {
                // Modern loading skeleton
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF0F0F0),
                                    Color(0xFFE0E0E0)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF6B35),
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "AI Antrenmanı Hazırlanıyor...",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (workout != null) {
                // Modern gradient background with content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A1A1A),
                                    Color(0xFF2E2E2E),
                                    Color(0xFF1A1A1A)
                                )
                            )
                        )
                ) {
                    // Floating category badge
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(20.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFF6B35).copy(alpha = 0.9f)
                    ) {
                        Text(
                            text = "🤖 AI Generated",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                    
                    // Main content
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            text = workout.title,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = workout.description,
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            lineHeight = 18.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Stats row
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatsBadge(
                                icon = "⏱️",
                                text = "${workout.duration} dk",
                                backgroundColor = Color(0xFF4CAF50)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            StatsBadge(
                                icon = "🔥",
                                text = "${workout.estimatedCalories} cal",
                                backgroundColor = Color(0xFFFF9800)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            StatsBadge(
                                icon = "🎯",
                                text = "${workout.personalizedScore}%",
                                backgroundColor = Color(0xFF9C27B0)
                            )
                        }
                    }
                    
                    // Play button
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(20.dp)
                            .size(56.dp),
                        shape = CircleShape,
                        color = Color(0xFFFF6B35),
                        shadowElevation = 8.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Start",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            } else {
                // No data state with modern design
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFF5F5F5),
                                    Color(0xFFEEEEEE)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎯",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Özel Antrenman Hazırlanıyor",
                            color = Color(0xFF666666),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Size özel AI antrenmanı birazdan hazır olacak",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsBadge(
    icon: String,
    text: String,
    backgroundColor: Color
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor.copy(alpha = 0.9f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StrengthSection(
    sportWorkouts: List<SportSpecificWorkout>,
    totalCount: Int,
    isLoading: Boolean,
    onWorkoutClick: (SportSpecificWorkout) -> Unit
) {
    Column {
        SectionHeader(title = "Strength", totalCount = totalCount, onSeeAllClick = { })
        Spacer(modifier = Modifier.height(20.dp))
        
        if (isLoading) {
            // Enhanced loading skeleton for horizontal scroll
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(3) { index ->
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .height(220.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFF0F0F0),
                                            Color(0xFFE0E0E0)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFFFF6B35),
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Yükleniyor...",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        } else if (sportWorkouts.isNotEmpty()) {
            // Enhanced sport workouts display
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(sportWorkouts.take(5)) { workout ->
                    ModernWorkoutCard(
                        workout = workout,
                        onWorkoutClick = { onWorkoutClick(workout) }
                    )
                }
            }
        } else {
            // Empty state with call to action
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F9FA)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💪",
                            fontSize = 48.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Spor Antrenmanları",
                            color = Color(0xFF333333),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Profil ayarlarınızdan spor tercihlerinizi seçin",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernWorkoutCard(
    workout: SportSpecificWorkout,
    onWorkoutClick: () -> Unit
) {
    val sportColors = getSportColors(workout.sportType)
    
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(220.dp)
            .clickable { onWorkoutClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = sportColors
                    )
                )
        ) {
            // Sport type badge
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    text = getSportEmoji(workout.sportType),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            // Main content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = workout.title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = getSportName(workout.sportType),
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Stats
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workout.duration}dk",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${workout.calories}cal",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Play button
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(40.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = sportColors[0],
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Helper function for sport emojis
private fun getSportEmoji(sportType: ExerciseType): String {
    return when (sportType) {
        ExerciseType.FOOTBALL -> "⚽"
        ExerciseType.BASKETBALL -> "🏀"
        ExerciseType.TENNIS -> "🎾"
        ExerciseType.MARTIAL_ARTS -> "🥋"
        ExerciseType.VOLLEYBALL -> "🏐"
        ExerciseType.BADMINTON -> "🏸"
        ExerciseType.WRESTLING -> "🤼"
        ExerciseType.FITNESS -> "💪"
    }
}

@Composable
private fun AISuggestionSection(
    workout: AIWorkoutRecommendation?,
    isLoading: Boolean,
    onWorkoutClick: () -> Unit
) {
    Column {
        SectionHeader(title = "AI Suggestion", onSeeAllClick = { })
        Spacer(modifier = Modifier.height(20.dp))
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clickable { if (!isLoading && workout != null) onWorkoutClick() },
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFE0B2),
                                    Color(0xFFFFCC80)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF6B35),
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AI Önerisi Hazırlanıyor...",
                            color = Color(0xFF666666),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (workout != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF6B35),
                                    Color(0xFFFF8A50),
                                    Color(0xFFFF6B35)
                                )
                            )
                        )
                ) {
                    // Floating AI badge
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(20.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎯",
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${workout.personalizedScore}%",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    
                    // Main content
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = workout.title,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 24.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = workout.description,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Stats row
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "⏱️", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${workout.duration} dk",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "🔥", fontSize = 12.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${workout.estimatedCalories} cal",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // Enhanced play button
                        Surface(
                            modifier = Modifier.size(52.dp),
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 8.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Start",
                                    tint = Color(0xFFFF6B35),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Enhanced no data state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFFF3E0),
                                    Color(0xFFFFE0B2)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🤖",
                            fontSize = 42.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AI Önerisi Hazırlanıyor",
                            color = Color(0xFF666666),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Size en uygun antrenman seçiliyor",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MyWorkoutsSection(
    aiWorkouts: List<AIWorkoutRecommendation>,
    sportWorkouts: List<SportSpecificWorkout>,
    isAILoading: Boolean,
    isSportLoading: Boolean,
    onAIWorkoutClick: (AIWorkoutRecommendation) -> Unit,
    onSportWorkoutClick: (SportSpecificWorkout) -> Unit
) {
    Column {
        SectionHeader(
            title = "My Workouts", 
            totalCount = aiWorkouts.size + sportWorkouts.size,
            onSeeAllClick = { }
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        // Show loading state if both are loading
        if (isAILoading && isSportLoading) {
            repeat(3) { index ->
                ModernWorkoutListItem(
                    title = "Antrenman Yükleniyor...",
                    subtitle = "Lütfen bekleyin...",
                    progress = null,
                    isLoading = true,
                    icon = "⏳",
                    onClick = { }
                )
                if (index < 2) Spacer(modifier = Modifier.height(12.dp))
            }
        } else {
            // Create a diverse mix of workouts to avoid repetition
            val combinedWorkouts = mutableListOf<WorkoutListItem>()
            
            // Add first 2 AI workouts
            aiWorkouts.take(2).forEachIndexed { index, workout ->
                combinedWorkouts.add(
                    WorkoutListItem.AIWorkout(
                        workout = workout,
                        isRecent = index == 0
                    )
                )
            }
            
            // Add first 2 sport workouts
            sportWorkouts.take(2).forEachIndexed { index, workout ->
                combinedWorkouts.add(
                    WorkoutListItem.SportWorkout(
                        workout = workout,
                        isRecent = index == 0
                    )
                )
            }
            
            // Add remaining AI workouts (skip first 2 which are shown in Featured and AI Suggestion)
            aiWorkouts.drop(2).take(2).forEach { workout ->
                combinedWorkouts.add(WorkoutListItem.AIWorkout(workout, false))
            }
            
            // Shuffle to create variety and take max 6 items
            val displayWorkouts = combinedWorkouts.shuffled().take(6)
            
            displayWorkouts.forEachIndexed { index, workoutItem ->
                when (workoutItem) {
                    is WorkoutListItem.AIWorkout -> {
                        ModernWorkoutListItem(
                            title = workoutItem.workout.title,
                            subtitle = "${workoutItem.workout.duration} dk • ${workoutItem.workout.estimatedCalories} cal",
                            progress = workoutItem.workout.personalizedScore / 100f,
                            isLoading = false,
                            icon = "🤖",
                            badge = if (workoutItem.isRecent) "Yeni" else "AI",
                            badgeColor = if (workoutItem.isRecent) Color(0xFF4CAF50) else Color(0xFFFF6B35),
                            onClick = { onAIWorkoutClick(workoutItem.workout) }
                        )
                    }
                    is WorkoutListItem.SportWorkout -> {
                        ModernWorkoutListItem(
                            title = workoutItem.workout.title,
                            subtitle = "${workoutItem.workout.duration} dk • ${workoutItem.workout.calories} cal",
                            progress = 0.75f, // Fixed progress for sport workouts
                            isLoading = false,
                            icon = getSportEmoji(workoutItem.workout.sportType),
                            badge = if (workoutItem.isRecent) "Popüler" else getSportName(workoutItem.workout.sportType),
                            badgeColor = if (workoutItem.isRecent) Color(0xFF9C27B0) else getSportColors(workoutItem.workout.sportType)[0],
                            onClick = { onSportWorkoutClick(workoutItem.workout) }
                        )
                    }
                }
                
                if (index < displayWorkouts.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            // Show partial loading for remaining items if one service is still loading
            if (isAILoading || isSportLoading) {
                if (displayWorkouts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                repeat(2) { index ->
                    ModernWorkoutListItem(
                        title = "Daha fazla antrenman yükleniyor...",
                        subtitle = "Birazdan hazır olacak",
                        progress = null,
                        isLoading = true,
                        icon = "⏳",
                        onClick = { }
                    )
                    if (index < 1) Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

// Sealed class for different workout types
private sealed class WorkoutListItem {
    data class AIWorkout(val workout: AIWorkoutRecommendation, val isRecent: Boolean) : WorkoutListItem()
    data class SportWorkout(val workout: SportSpecificWorkout, val isRecent: Boolean) : WorkoutListItem()
}

@Composable
private fun ModernWorkoutListItem(
    title: String,
    subtitle: String,
    progress: Float?,
    isLoading: Boolean,
    icon: String,
    badge: String? = null,
    badgeColor: Color = Color(0xFFFF6B35),
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!isLoading) onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLoading) Color(0xFFF5F5F5) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = if (isLoading) Color(0xFFE0E0E0) else badgeColor.copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color(0xFFFF6B35),
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = icon,
                            fontSize = 24.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLoading) Color.Gray else Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Badge
                    if (!isLoading && badge != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = badgeColor.copy(alpha = 0.9f)
                        ) {
                            Text(
                                text = badge,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (!isLoading && progress != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = badgeColor,
                        trackColor = Color.Gray.copy(alpha = 0.2f)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "${(progress * 100).toInt()}% uyumluluk",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (!isLoading) {
                Spacer(modifier = Modifier.width(12.dp))
                
                // Arrow icon
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Go",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    totalCount: Int = 0,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (totalCount > 0) "$title ($totalCount)" else title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Text(
            text = "See all",
            fontSize = 14.sp,
            color = Color(0xFFFF6B35),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onSeeAllClick() }
        )
    }
}

@Composable
private fun DebugInfoCard(userProfile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ℹ️ Debug: Antrenman Yükleme Durumu",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kullanıcı: ${userProfile.name}",
                fontSize = 12.sp,
                color = Color(0xFFE65100)
            )
            Text(
                text = "Spor tercihleri: ${userProfile.exercisePreferences}",
                fontSize = 12.sp,
                color = Color(0xFFE65100)
            )
            Text(
                text = "Profil ayarlarınızdan spor türlerini seçerek kişiselleştirilmiş antrenmanlar alabilirsiniz.",
                fontSize = 12.sp,
                color = Color(0xFFE65100)
            )
        }
    }
}

// Helper functions
private fun getSportColors(sportType: ExerciseType): List<Color> {
    return when (sportType) {
        ExerciseType.FOOTBALL -> listOf(Color(0xFF4CAF50), Color(0xFF2E7D32))
        ExerciseType.BASKETBALL -> listOf(Color(0xFFFF9800), Color(0xFFE65100))
        ExerciseType.TENNIS -> listOf(Color(0xFF2196F3), Color(0xFF0D47A1))
        ExerciseType.MARTIAL_ARTS -> listOf(Color(0xFF9C27B0), Color(0xFF4A148C))
        ExerciseType.VOLLEYBALL -> listOf(Color(0xFFE91E63), Color(0xFF880E4F))
        ExerciseType.BADMINTON -> listOf(Color(0xFF00BCD4), Color(0xFF006064))
        ExerciseType.WRESTLING -> listOf(Color(0xFF795548), Color(0xFF3E2723))
        ExerciseType.FITNESS -> listOf(Color(0xFFFF6B35), Color(0xFFD84315))
    }
}

private fun getSportName(sportType: ExerciseType): String {
    return when (sportType) {
        ExerciseType.FOOTBALL -> "Futbol"
        ExerciseType.BASKETBALL -> "Basketbol"
        ExerciseType.TENNIS -> "Tenis"
        ExerciseType.MARTIAL_ARTS -> "Dövüş Sanatları"
        ExerciseType.VOLLEYBALL -> "Voleybol"
        ExerciseType.BADMINTON -> "Badminton"
        ExerciseType.WRESTLING -> "Güreş"
        ExerciseType.FITNESS -> "Fitness"
    }
}