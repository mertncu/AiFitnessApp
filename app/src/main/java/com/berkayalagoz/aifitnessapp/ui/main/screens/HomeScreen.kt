package com.berkayalagoz.aifitnessapp.ui.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.berkayalagoz.aifitnessapp.model.UserProfile
import com.berkayalagoz.aifitnessapp.service.AIRecommendations
import com.berkayalagoz.aifitnessapp.service.WeatherService
import com.berkayalagoz.aifitnessapp.service.WeatherData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

// Gerçek veri modelleri
data class DailyStats(
    val completedWorkouts: Int = 0,
    val totalCaloriesBurned: Int = 0,
    val waterIntake: Int = 0,
    val sleepHours: Int = 0,
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
)

data class WeeklyProgress(
    val workoutsCompleted: Int = 0,
    val totalWorkouts: Int = 0,
    val caloriesBurned: Int = 0,
    val averageWorkoutDuration: Int = 0
)

data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",
    val unlockedAt: Long = 0L,
    val isNew: Boolean = false
)

data class RecentWorkout(
    val id: String = "",
    val name: String = "",
    val duration: Int = 0,
    val caloriesBurned: Int = 0,
    val completedAt: Long = 0L,
    val difficulty: String = ""
)

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    aiRecommendations: AIRecommendations,
    onRefreshClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onCreateDemoData: () -> Unit = {}
) {
    val currentDate = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale("tr", "TR")).format(Date())
    }
    
    var weatherData by remember { mutableStateOf<WeatherData?>(null) }
    var isWeatherLoading by remember { mutableStateOf(true) }
    var dailyStats by remember { mutableStateOf(DailyStats()) }
    var weeklyProgress by remember { mutableStateOf(WeeklyProgress()) }
    var recentAchievements by remember { mutableStateOf<List<Achievement>>(emptyList()) }
    var recentWorkouts by remember { mutableStateOf<List<RecentWorkout>>(emptyList()) }
    var isDataLoading by remember { mutableStateOf(true) }
    
    val weatherService = remember { WeatherService() }
    val scope = rememberCoroutineScope()
    
    // Verileri yükle
    LaunchedEffect(userProfile.userId) {
        scope.launch {
            // Hava durumu
            weatherData = weatherService.getCurrentWeather()
            isWeatherLoading = false
            
            // Firestore verilerini yükle
            loadUserDashboardData(
                userId = userProfile.userId,
                onDailyStats = { dailyStats = it },
                onWeeklyProgress = { weeklyProgress = it },
                onAchievements = { recentAchievements = it },
                onRecentWorkouts = { recentWorkouts = it }
            )
            isDataLoading = false
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HomeHeader(
                date = currentDate,
                userName = userProfile.name,
                onRefreshClick = {
                    onRefreshClick()
                    scope.launch {
                        isWeatherLoading = true
                        isDataLoading = true
                        weatherData = weatherService.getCurrentWeather()
                        isWeatherLoading = false
                        
                        loadUserDashboardData(
                            userId = userProfile.userId,
                            onDailyStats = { dailyStats = it },
                            onWeeklyProgress = { weeklyProgress = it },
                            onAchievements = { recentAchievements = it },
                            onRecentWorkouts = { recentWorkouts = it }
                        )
                        isDataLoading = false
                    }
                }
            )
        }
        
        // Demo veri oluşturma kartı (eğer veri yoksa göster)
        if (recentWorkouts.isEmpty() && recentAchievements.isEmpty() && !isDataLoading) {
            item {
                DemoDataCard(onCreateDemoData = onCreateDemoData)
            }
        }
        
        // Hava durumu kartı (değişmeden kalacak)
        item {
            WeatherCard(
                weatherData = weatherData,
                isLoading = isWeatherLoading
            )
        }
        
        // Günlük istatistikler
        item {
            DailyStatsCard(
                dailyStats = dailyStats,
                isLoading = isDataLoading
            )
        }
        
        // Haftalık ilerleme
        item {
            WeeklyProgressCard(
                weeklyProgress = weeklyProgress,
                isLoading = isDataLoading
            )
        }
        
        // Son başarımlar
        if (recentAchievements.isNotEmpty()) {
            item {
                RecentAchievementsCard(
                    achievements = recentAchievements,
                    isLoading = isDataLoading
                )
            }
        }
        
        // Son antrenmanlar
        if (recentWorkouts.isNotEmpty()) {
            item {
                RecentWorkoutsCard(
                    workouts = recentWorkouts,
                    isLoading = isDataLoading
                )
            }
        }
        
        // Kişiselleştirilmiş öneriler
        item {
            PersonalizedRecommendationsCard(
                userProfile = userProfile,
                aiRecommendations = aiRecommendations
            )
        }
        
        // Motivasyon kartı
        item {
            MotivationCard(userProfile = userProfile)
        }
    }
}

@Composable
private fun HomeHeader(
    date: String,
    userName: String,
    onRefreshClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = date,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "Merhaba, $userName!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        IconButton(
            onClick = onRefreshClick
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = "Refresh",
                tint = Color(0xFFFF6B35)
            )
        }
    }
}

@Composable
private fun WeatherCard(
    weatherData: WeatherData?,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF00BCD4)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    Text(
                        text = "🌤️ Hava Durumu Yükleniyor...",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Lütfen bekleyin...",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                } else if (weatherData != null) {
                    Text(
                        text = "${weatherData.weatherEmoji} ${weatherData.weatherDescription}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = getWeatherWorkoutSuggestion(weatherData.weatherCode, weatherData.temperature),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "${weatherData.temperature.roundToInt()}°C - ${weatherData.cityName}",
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Text(
                        text = "🌤️ Hava Durumu",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Hava durumu bilgisi alınamadı. Yine de harika bir antrenman günü!",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                }
            }
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            getWeatherIcon(weatherData?.weatherCode ?: 0),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyStatsCard(
    dailyStats: DailyStats,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Bugünkü İstatistikler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color(0xFFFF6B35),
                        strokeWidth = 2.dp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DailyStatItem(
                    value = if (isLoading) "-" else "${dailyStats.completedWorkouts}",
                    label = "Antrenman",
                    icon = Icons.Default.FitnessCenter,
                    color = Color(0xFF4CAF50)
                )
                DailyStatItem(
                    value = if (isLoading) "-" else "${dailyStats.totalCaloriesBurned}",
                    label = "Kalori",
                    icon = Icons.Default.LocalFireDepartment,
                    color = Color(0xFFFF6B35)
                )
                DailyStatItem(
                    value = if (isLoading) "-" else "${dailyStats.waterIntake}L",
                    label = "Su",
                    icon = Icons.Default.WaterDrop,
                    color = Color(0xFF2196F3)
                )
                DailyStatItem(
                    value = if (isLoading) "-" else "${dailyStats.sleepHours}h",
                    label = "Uyku",
                    icon = Icons.Default.Bedtime,
                    color = Color(0xFF9C27B0)
                )
            }
        }
    }
}

@Composable
private fun DailyStatItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = color.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
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
private fun WeeklyProgressCard(
    weeklyProgress: WeeklyProgress,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📈 Haftalık İlerleme",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                if (!isLoading && weeklyProgress.totalWorkouts > 0) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "${weeklyProgress.workoutsCompleted}/${weeklyProgress.totalWorkouts} Tamamlandı",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFF6B35),
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                // İlerleme çubuğu
                val progress = if (weeklyProgress.totalWorkouts > 0) {
                    weeklyProgress.workoutsCompleted.toFloat() / weeklyProgress.totalWorkouts.toFloat()
                } else 0f
                
                Column {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFFFF6B35),
                        trackColor = Color(0xFFFF6B35).copy(alpha = 0.2f)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${weeklyProgress.caloriesBurned} kalori yakıldı",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "Ort. ${weeklyProgress.averageWorkoutDuration} dk",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentWorkoutsCard(
    workouts: List<RecentWorkout>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "💪 Son Antrenmanlar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFF6B35),
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                workouts.take(3).forEach { workout ->
                    RecentWorkoutItem(workout = workout)
                    if (workout != workouts.take(3).last()) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentWorkoutItem(workout: RecentWorkout) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = Color(0xFFFF6B35).copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = workout.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = "${workout.duration} dk • ${workout.caloriesBurned} kalori",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = getDifficultyColor(workout.difficulty).copy(alpha = 0.1f)
        ) {
            Text(
                text = workout.difficulty,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = getDifficultyColor(workout.difficulty),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun PersonalizedRecommendationsCard(
    userProfile: UserProfile,
    aiRecommendations: AIRecommendations
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B35)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🤖 Kişisel AI Önerileriniz",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = getPersonalizedMessage(userProfile, aiRecommendations),
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Antrenman Planı",
                        color = Color(0xFFFF6B35),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Beslenme",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AIRecommendationsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFF6B35))
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🤖 AI Önerileri",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Bugün sizin için özel olarak hazırladığımız antrenman ve beslenme önerilerinizi inceleyin.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Önerileri Gör",
                    color = Color(0xFFFF6B35),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun QuickStatsCard(userProfile: UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Hızlı Bakış",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickStatItem(
                    value = "${userProfile.height.toInt()}cm",
                    label = "Boy"
                )
                QuickStatItem(
                    value = "${userProfile.weight.toInt()}kg",
                    label = "Kilo"
                )
                QuickStatItem(
                    value = "5",
                    label = "Günlük Seri"
                )
            }
        }
    }
}

@Composable
private fun QuickStatItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF6B35)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun TodayActivitiesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Bugünkü Aktiviteler",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "3/5 Tamamlandı",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActivityItem(
                    icon = Icons.Default.DirectionsRun,
                    value = "6,543",
                    label = "Adım",
                    color = Color(0xFF2196F3),
                    isCompleted = true
                )
                ActivityItem(
                    icon = Icons.Default.LocalFireDepartment,
                    value = "320",
                    label = "Kalori",
                    color = Color(0xFFFF6B35),
                    isCompleted = true
                )
                ActivityItem(
                    icon = Icons.Default.Schedule,
                    value = "25",
                    label = "Dakika",
                    color = Color(0xFF9C27B0),
                    isCompleted = false
                )
                ActivityItem(
                    icon = Icons.Default.LocalDrink,
                    value = "6/8",
                    label = "Su",
                    color = Color(0xFF00BCD4),
                    isCompleted = true
                )
            }
        }
    }
}

@Composable
private fun ActivityItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    isCompleted: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = if (isCompleted) color.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = if (isCompleted) color else Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isCompleted) Color.Black else Color.Gray
        )
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun WeeklyGoalsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Haftalık Hedefler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            WeeklyGoalItem(
                title = "Antrenman Günleri",
                current = 4,
                target = 5,
                color = Color(0xFFFF6B35)
            )
            Spacer(modifier = Modifier.height(12.dp))
            WeeklyGoalItem(
                title = "Aktif Dakika",
                current = 180,
                target = 250,
                color = Color(0xFF2196F3)
            )
            Spacer(modifier = Modifier.height(12.dp))
            WeeklyGoalItem(
                title = "Kalori Yakımı",
                current = 1800,
                target = 2500,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
private fun WeeklyGoalItem(
    title: String,
    current: Int,
    target: Int,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                text = "$current/$target",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = (current.toFloat() / target).coerceAtMost(1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun DailyStepsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = Color(0xFF2196F3).copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        Icons.Default.DirectionsRun,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "6,543 Adım",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Hedef: 10,000 adım",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 0.65f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp),
                    color = Color(0xFF2196F3),
                    trackColor = Color(0xFF2196F3).copy(alpha = 0.2f)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "65%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2196F3)
                )
                Text(
                    text = "tamamlandı",
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun RecentAchievementsCard(
    achievements: List<Achievement>,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Son Başarılar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFD700).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "🏆 ${achievements.size} Rozet",
                        color = Color(0xFFFF8F00),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                achievements.forEach { achievement ->
                    AchievementBadge(
                        icon = achievement.icon,
                        title = achievement.title,
                        isNew = achievement.isNew
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementBadge(
    icon: String,
    title: String,
    isNew: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                color = if (isNew) Color(0xFFFFD700).copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.1f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = icon,
                        fontSize = 24.sp
                    )
                }
            }
            if (isNew) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    shape = CircleShape,
                    color = Color(0xFFFF6B35)
                ) {
                    Box(
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            color = if (isNew) Color.Black else Color.Gray,
            fontWeight = if (isNew) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun MotivationCard(userProfile: UserProfile) {
    val motivationMessages = listOf(
        "Bugün harika görünüyorsun! 💪",
        "Her adım seni hedefe yaklaştırıyor! 🎯",
        "Dün yapamadığını bugün yapabilirsin! 🚀",
        "Kendine inan, sen çok güçlüsün! ⭐",
        "Küçük adımlar, büyük değişimler! 🌟"
    )
    
    val randomMessage = remember { motivationMessages.random() }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF667eea)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "Günlük Motivasyon",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = randomMessage,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { /* TODO: Share motivation */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Paylaş",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoDataCard(onCreateDemoData: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2196F3)
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "🚀 Demo Veriler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Henüz antrenman veriniz bulunmuyor. Demo veriler oluşturarak uygulamanın tüm özelliklerini keşfedebilirsiniz!",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onCreateDemoData,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Demo Veriler Oluştur",
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Yardımcı fonksiyonlar
private fun getWeatherWorkoutSuggestion(weatherCode: Int, temperature: Double): String {
    return when {
        weatherCode == 0 && temperature > 15 -> "Dışarıda antrenman yapmak için mükemmel hava! Koşu veya bisiklet öneriyoruz."
        weatherCode in 1..3 && temperature > 10 -> "Güzel hava! Dış mekan aktiviteleri için uygun."
        weatherCode in 61..67 || weatherCode in 80..82 -> "Yağmurlu hava. İç mekan antrenmanları tercih edin."
        weatherCode in 71..77 || weatherCode in 85..86 -> "Karlı hava. Evde yoga veya kuvvet antrenmanı yapabilirsiniz."
        temperature < 5 -> "Soğuk hava. İç mekan antrenmanları daha uygun."
        temperature > 30 -> "Sıcak hava. Erken saatlerde veya iç mekanda antrenman yapın."
        else -> "Her türlü antrenman için uygun hava koşulları."
    }
}

private fun getWeatherIcon(weatherCode: Int): androidx.compose.ui.graphics.vector.ImageVector {
    return when (weatherCode) {
        0 -> Icons.Default.WbSunny
        in 1..3 -> Icons.Default.Cloud
        in 45..48 -> Icons.Default.Cloud
        in 51..67, in 80..82 -> Icons.Default.Umbrella
        in 71..77, in 85..86 -> Icons.Default.AcUnit
        in 95..99 -> Icons.Default.FlashOn
        else -> Icons.Default.WbSunny
    }
}

private fun getDifficultyColor(difficulty: String): Color {
    return when (difficulty.lowercase()) {
        "kolay", "başlangıç", "beginner" -> Color(0xFF4CAF50)
        "orta", "intermediate" -> Color(0xFFFF9800)
        "zor", "ileri", "advanced" -> Color(0xFFF44336)
        else -> Color(0xFF757575)
    }
}

private fun getPersonalizedMessage(userProfile: UserProfile, aiRecommendations: AIRecommendations): String {
    val goal = userProfile.fitnessGoal
    val workoutDays = userProfile.weeklyWorkoutDays
    
    return when (goal) {
        "LOSE_WEIGHT" -> "Kilo verme hedefiniz için bugün ${aiRecommendations.dailyCalories} kalori hedefi ve yüksek yoğunluklu antrenman öneriyoruz."
        "GET_BULK" -> "Kas geliştirme hedefiniz için protein ağırlıklı beslenme ve kuvvet antrenmanları planladık."
        "GAIN_ENDURANCE" -> "Dayanıklılık hedefiniz için kardiyovasküler antrenmanlar ve dengeli beslenme öneriyoruz."
        else -> "Haftalık $workoutDays gün antrenman planınıza uygun özel önerileriniz hazır!"
    }
}

private fun getFitnessGoalText(goal: String): String {
    return when (goal) {
        "LOSE_WEIGHT" -> "Kilo Vermek"
        "GET_BULK" -> "Kas Geliştirmek"
        "GAIN_ENDURANCE" -> "Dayanıklılık Artırmak"
        "TRY_AI_COACH" -> "AI Koçluk Denemek"
        else -> "Fitness Yapmak"
    }
}

// Firestore veri yükleme fonksiyonu
private suspend fun loadUserDashboardData(
    userId: String,
    onDailyStats: (DailyStats) -> Unit,
    onWeeklyProgress: (WeeklyProgress) -> Unit,
    onAchievements: (List<Achievement>) -> Unit,
    onRecentWorkouts: (List<RecentWorkout>) -> Unit
) {
    val firestore = FirebaseFirestore.getInstance()
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    
    try {
        // Günlük istatistikler
        val dailyStatsDoc = firestore.collection("users")
            .document(userId)
            .collection("daily_stats")
            .document(today)
            .get()
            .await()
        
        val dailyStats = if (dailyStatsDoc.exists()) {
            DailyStats(
                completedWorkouts = dailyStatsDoc.getLong("completedWorkouts")?.toInt() ?: 0,
                totalCaloriesBurned = dailyStatsDoc.getLong("totalCaloriesBurned")?.toInt() ?: 0,
                waterIntake = dailyStatsDoc.getLong("waterIntake")?.toInt() ?: 0,
                sleepHours = dailyStatsDoc.getLong("sleepHours")?.toInt() ?: 8,
                date = today
            )
        } else {
            // Varsayılan değerler veya rastgele demo veriler
            DailyStats(
                completedWorkouts = (0..2).random(),
                totalCaloriesBurned = (150..450).random(),
                waterIntake = (1..3).random(),
                sleepHours = (6..9).random(),
                date = today
            )
        }
        onDailyStats(dailyStats)
        
        // Haftalık ilerleme
        val weeklyProgressDoc = firestore.collection("users")
            .document(userId)
            .collection("weekly_progress")
            .document("current_week")
            .get()
            .await()
        
        val weeklyProgress = if (weeklyProgressDoc.exists()) {
            WeeklyProgress(
                workoutsCompleted = weeklyProgressDoc.getLong("workoutsCompleted")?.toInt() ?: 0,
                totalWorkouts = weeklyProgressDoc.getLong("totalWorkouts")?.toInt() ?: 5,
                caloriesBurned = weeklyProgressDoc.getLong("caloriesBurned")?.toInt() ?: 0,
                averageWorkoutDuration = weeklyProgressDoc.getLong("averageWorkoutDuration")?.toInt() ?: 0
            )
        } else {
            // Demo veriler
            val completed = (2..5).random()
            WeeklyProgress(
                workoutsCompleted = completed,
                totalWorkouts = 5,
                caloriesBurned = completed * (200..350).random(),
                averageWorkoutDuration = (25..45).random()
            )
        }
        onWeeklyProgress(weeklyProgress)
        
        // Son başarımlar
        val achievementsSnapshot = firestore.collection("users")
            .document(userId)
            .collection("achievements")
            .orderBy("unlockedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .await()
        
        val achievements = if (achievementsSnapshot.isEmpty) {
            // Demo başarımlar
            listOf(
                Achievement(
                    id = "first_workout",
                    title = "İlk Antrenman",
                    description = "İlk antrenmanınızı tamamladınız!",
                    icon = "🎯",
                    unlockedAt = System.currentTimeMillis() - 86400000,
                    isNew = false
                ),
                Achievement(
                    id = "week_streak",
                    title = "Haftalık Seri",
                    description = "7 gün üst üste antrenman yaptınız!",
                    icon = "🔥",
                    unlockedAt = System.currentTimeMillis() - 3600000,
                    isNew = true
                ),
                Achievement(
                    id = "calorie_burner",
                    title = "Kalori Yakıcı",
                    description = "1000 kalori yaktınız!",
                    icon = "⚡",
                    unlockedAt = System.currentTimeMillis() - 7200000,
                    isNew = false
                )
            )
        } else {
            achievementsSnapshot.documents.map { doc ->
                Achievement(
                    id = doc.id,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    icon = doc.getString("icon") ?: "🏆",
                    unlockedAt = doc.getLong("unlockedAt") ?: 0L,
                    isNew = doc.getBoolean("isNew") ?: false
                )
            }
        }
        onAchievements(achievements)
        
        // Son antrenmanlar
        val workoutsSnapshot = firestore.collection("users")
            .document(userId)
            .collection("completed_workouts")
            .orderBy("completedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .await()
        
        val recentWorkouts = if (workoutsSnapshot.isEmpty) {
            // Demo antrenmanlar
            listOf(
                RecentWorkout(
                    id = "workout1",
                    name = "HIIT Cardio",
                    duration = 25,
                    caloriesBurned = 320,
                    completedAt = System.currentTimeMillis() - 3600000,
                    difficulty = "Orta"
                ),
                RecentWorkout(
                    id = "workout2",
                    name = "Upper Body Strength",
                    duration = 35,
                    caloriesBurned = 280,
                    completedAt = System.currentTimeMillis() - 86400000,
                    difficulty = "Zor"
                ),
                RecentWorkout(
                    id = "workout3",
                    name = "Yoga Flow",
                    duration = 20,
                    caloriesBurned = 120,
                    completedAt = System.currentTimeMillis() - 172800000,
                    difficulty = "Kolay"
                )
            )
        } else {
            workoutsSnapshot.documents.map { doc ->
                RecentWorkout(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    duration = doc.getLong("duration")?.toInt() ?: 0,
                    caloriesBurned = doc.getLong("caloriesBurned")?.toInt() ?: 0,
                    completedAt = doc.getLong("completedAt") ?: 0L,
                    difficulty = doc.getString("difficulty") ?: "Orta"
                )
            }
        }
        onRecentWorkouts(recentWorkouts)
        
    } catch (e: Exception) {
        // Hata durumunda demo veriler göster
        onDailyStats(DailyStats(
            completedWorkouts = 1,
            totalCaloriesBurned = 250,
            waterIntake = 2,
            sleepHours = 7
        ))
        
        onWeeklyProgress(WeeklyProgress(
            workoutsCompleted = 3,
            totalWorkouts = 5,
            caloriesBurned = 850,
            averageWorkoutDuration = 32
        ))
        
        onAchievements(emptyList())
        onRecentWorkouts(emptyList())
    }
} 