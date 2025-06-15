package com.berkayalagoz.aifitnessapp.ui.diet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.berkayalagoz.aifitnessapp.service.DietRecommendation
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietScreen(
    viewModel: DietViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    var showAddFoodDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(state.error) {
        if (state.error != null) {
            // Error handling
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFFFF6B35)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "AI kişiselleştirilmiş diyet planınız hazırlanıyor...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Header
                item {
                    DietHeader()
                }
                
                // Daily Progress Cards
                item {
                    DailyProgressSection(
                        progress = state.dailyProgress,
                        onWaterAdd = { amount -> viewModel.addWaterIntake(amount) }
                    )
                }
                
                // Quick Add Food Section
                item {
                    QuickAddFoodSection(
                        onAddFoodClick = { showAddFoodDialog = true }
                    )
                }
                
                // Today's Meals
                item {
                    TodaysMealsSection(
                        foodEntries = state.todaysFoodEntries,
                        onRemoveEntry = { entryId -> viewModel.removeFoodEntry(entryId) }
                    )
                }
                
                // AI Diet Recommendations
                state.aiRecommendations?.let { recommendations ->
                    item {
                        AIDietRecommendationsSection(
                            recommendations = recommendations.dietPlan
                        )
                    }
                }
                
                // Diet Tips
                state.aiRecommendations?.let { recommendations ->
                    item {
                        DietTipsSection(
                            tips = recommendations.tips.filter { 
                                it.contains("🥗") || it.contains("💧") || it.contains("🔥") 
                            }
                        )
                    }
                }
            }
        }
        
        // Add Food Dialog
        if (showAddFoodDialog) {
            AddFoodDialog(
                onDismiss = { showAddFoodDialog = false },
                onAddFood = { foodEntry ->
                    viewModel.addFoodEntry(foodEntry)
                    showAddFoodDialog = false
                }
            )
        }
        
        // Error Snackbar
        state.error?.let { error ->
            LaunchedEffect(error) {
                viewModel.clearError()
            }
        }
    }
}

@Composable
private fun DietHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFFF6B35),
                RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Diyet Takibi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("tr"))),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            Icon(
                Icons.Default.Restaurant,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun DailyProgressSection(
    progress: DailyProgress,
    onWaterAdd: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "Günlük Hedeflerim",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ProgressCard(
                    title = "Kalori",
                    current = progress.totalCalories,
                    target = progress.targetCalories,
                    unit = "kcal",
                    color = Color(0xFFFF6B35),
                    icon = Icons.Default.LocalFireDepartment
                )
            }
            
            item {
                ProgressCard(
                    title = "Protein",
                    current = progress.totalProtein.toInt(),
                    target = progress.targetProtein,
                    unit = "g",
                    color = Color(0xFFE65100),
                    icon = Icons.Default.FitnessCenter
                )
            }
            
            item {
                ProgressCard(
                    title = "Karbonhidrat",
                    current = progress.totalCarbs.toInt(),
                    target = progress.targetCarbs,
                    unit = "g",
                    color = Color(0xFFFF8A50),
                    icon = Icons.Default.Grain
                )
            }
            
            item {
                ProgressCard(
                    title = "Yağ",
                    current = progress.totalFat.toInt(),
                    target = progress.targetFat,
                    unit = "g",
                    color = Color(0xFFFFAB40),
                    icon = Icons.Default.Palette
                )
            }
            
            item {
                WaterProgressCard(
                    current = progress.waterProgress,
                    target = progress.targetWater,
                    onWaterAdd = onWaterAdd
                )
            }
        }
    }
}

@Composable
private fun ProgressCard(
    title: String,
    current: Int,
    target: Int,
    unit: String,
    color: Color,
    icon: ImageVector
) {
    val progress = if (target > 0) (current.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "$current / $target",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Text(
                    text = unit,
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun WaterProgressCard(
    current: Int,
    target: Int,
    onWaterAdd: (Int) -> Unit
) {
    val progress = if (target > 0) (current.toFloat() / target.toFloat()).coerceIn(0f, 1f) else 0f
    
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B35).copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = Color(0xFFFF6B35),
                    modifier = Modifier.size(20.dp)
                )
                IconButton(
                    onClick = { onWaterAdd(250) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Su ekle",
                        tint = Color(0xFFFF6B35),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Su",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Text(
                    text = "${current}ml",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Text(
                    text = "/${target}ml",
                    fontSize = 8.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFFFF6B35),
                trackColor = Color(0xFFFF6B35).copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
private fun QuickAddFoodSection(
    onAddFoodClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onAddFoodClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B35).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                tint = Color(0xFFFF6B35),
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(0xFFFF6B35).copy(alpha = 0.2f),
                        CircleShape
                    )
                    .padding(8.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Yemek Ekle",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF6B35)
                )
                Text(
                    text = "Tükettiğiniz yemeği kaydedin",
                    fontSize = 12.sp,
                    color = Color(0xFFFF6B35).copy(alpha = 0.8f)
                )
            }
            
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFFF6B35)
            )
        }
    }
}

@Composable
private fun TodaysMealsSection(
    foodEntries: List<FoodEntry>,
    onRemoveEntry: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Bugünkü Öğünlerim",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        if (foodEntries.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz yemek eklenmedi",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            foodEntries.forEach { entry ->
                FoodEntryCard(
                    entry = entry,
                    onRemove = { onRemoveEntry(entry.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun FoodEntryCard(
    entry: FoodEntry,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = entry.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = entry.mealType,
                        fontSize = 10.sp,
                        color = Color(0xFFFF6B35),
                        modifier = Modifier
                            .background(
                                Color(0xFFFF6B35).copy(alpha = 0.1f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${entry.calories} kcal",
                        fontSize = 12.sp,
                        color = Color(0xFFFF6B35)
                    )
                    Text(
                        text = "P: ${entry.protein.toInt()}g",
                        fontSize = 12.sp,
                        color = Color(0xFFE65100)
                    )
                    Text(
                        text = "C: ${entry.carbs.toInt()}g",
                        fontSize = 12.sp,
                        color = Color(0xFFFF8A50)
                    )
                    Text(
                        text = "F: ${entry.fat.toInt()}g",
                        fontSize = 12.sp,
                        color = Color(0xFFFFAB40)
                    )
                }
                
                if (entry.time.isNotEmpty()) {
                    Text(
                        text = entry.time,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            
            IconButton(
                onClick = onRemove
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AIDietRecommendationsSection(
    recommendations: List<DietRecommendation>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.Psychology,
                contentDescription = null,
                tint = Color(0xFFFF6B35)
            )
            Text(
                text = "AI Diyet Önerileri",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        recommendations.forEach { recommendation ->
            DietRecommendationCard(recommendation)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DietRecommendationCard(
    recommendation: DietRecommendation
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF6B35).copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recommendation.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = recommendation.mealType,
                    fontSize = 10.sp,
                    color = Color(0xFFFF6B35),
                    modifier = Modifier
                        .background(
                            Color(0xFFFF6B35).copy(alpha = 0.2f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = recommendation.description,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = recommendation.calories,
                    fontSize = 12.sp,
                    color = Color(0xFFFF6B35)
                )
                Text(
                    text = "P: ${recommendation.protein}",
                    fontSize = 12.sp,
                    color = Color(0xFFE65100)
                )
                Text(
                    text = "C: ${recommendation.carbs}",
                    fontSize = 12.sp,
                    color = Color(0xFFFF8A50)
                )
                Text(
                    text = "F: ${recommendation.fat}",
                    fontSize = 12.sp,
                    color = Color(0xFFFFAB40)
                )
            }
        }
    }
}

@Composable
private fun DietTipsSection(
    tips: List<String>
) {
    if (tips.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFFF6B35)
                )
                Text(
                    text = "Beslenme İpuçları",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            tips.forEach { tip ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFF6B35).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = tip,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddFoodDialog(
    onDismiss: () -> Unit,
    onAddFood: (FoodEntry) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Kahvaltı") }
    
    val mealTypes = listOf("Kahvaltı", "Öğle", "Akşam", "Ara Öğün")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Yemek Ekle")
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Yemek Adı") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Kalori") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Karbonhidrat (g)") },
                        modifier = Modifier.weight(1f)
                    )
                    
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { fat = it },
                        label = { Text("Yağ (g)") },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Meal Type Selection
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(mealTypes) { mealType ->
                        FilterChip(
                            onClick = { selectedMealType = mealType },
                            label = { Text(mealType, fontSize = 12.sp) },
                            selected = selectedMealType == mealType
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotBlank() && calories.isNotBlank()) {
                        val foodEntry = FoodEntry(
                            id = UUID.randomUUID().toString(),
                            name = name,
                            calories = calories.toIntOrNull() ?: 0,
                            protein = protein.toDoubleOrNull() ?: 0.0,
                            carbs = carbs.toDoubleOrNull() ?: 0.0,
                            fat = fat.toDoubleOrNull() ?: 0.0,
                            mealType = selectedMealType,
                            time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                        )
                        onAddFood(foodEntry)
                    }
                }
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
} 