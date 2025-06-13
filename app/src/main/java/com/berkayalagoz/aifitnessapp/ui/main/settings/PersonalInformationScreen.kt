package com.berkayalagoz.aifitnessapp.ui.main.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.berkayalagoz.aifitnessapp.model.UserProfile

@Composable
fun PersonalInformationScreen(onBackClick: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    // Form state
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    
    // Load user profile on first composition
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val profile = loadUserProfile()
                profile?.let {
                    userProfile = it
                    name = it.name
                    email = it.email
                    location = it.location
                    height = it.height.toString()
                    weight = it.weight.toString()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            PersonalInfoHeader(onBackClick = onBackClick)
        }
        
        if (isLoading) {
            item {
                LoadingSection()
            }
        } else {
            // Profile Photo Section
            item {
                ProfilePhotoSection()
            }
            
            // Personal Info Form
            item {
                PersonalInfoForm(
                    name = name,
                    email = email,
                    location = location,
                    height = height,
                    weight = weight,
                    onNameChange = { name = it },
                    onEmailChange = { email = it },
                    onLocationChange = { location = it },
                    onHeightChange = { height = it },
                    onWeightChange = { weight = it }
                )
            }
            
            // Save Button
            item {
                SaveButton(
                    isSaving = isSaving,
                    onSaveClick = {
                        scope.launch {
                            isSaving = true
                            try {
                                                                val updatedProfile = UserProfile(
                                    userId = userProfile?.userId ?: "",
                                    name = name,
                                    email = email,
                                    location = location,
                                    height = height.toFloatOrNull() ?: 0f,
                                    weight = weight.toFloatOrNull() ?: 0f,
                                    membershipType = userProfile?.membershipType ?: "Basic Member",
                                    profileImageUrl = userProfile?.profileImageUrl ?: "",
                                    fitnessGoal = userProfile?.fitnessGoal ?: "",
                                    gender = userProfile?.gender ?: "",
                                    age = userProfile?.age ?: 0,
                                    hasPreviousFitnessExperience = userProfile?.hasPreviousFitnessExperience ?: false,
                                    fitnessLevel = userProfile?.fitnessLevel ?: 0,
                                    activityLevel = userProfile?.activityLevel ?: 0,
                                    physicalLimitations = userProfile?.physicalLimitations ?: emptyList(),
                                    medicalConditions = userProfile?.medicalConditions ?: "",
                                    dietPreference = userProfile?.dietPreference ?: "",
                                    dietaryPreferences = userProfile?.dietaryPreferences ?: "",
                                    weeklyWorkoutDays = userProfile?.weeklyWorkoutDays ?: 0,
                                    exercisePreferences = userProfile?.exercisePreferences ?: emptyList(),
                                    supplements = userProfile?.supplements ?: emptyList(),
                                    dailyCalorieGoal = userProfile?.dailyCalorieGoal ?: 0,
                                    sleepQuality = userProfile?.sleepQuality ?: "",
                                    sleepHours = userProfile?.sleepHours ?: 8,
                                    waterIntake = userProfile?.waterIntake ?: 8,
                                    createdAt = userProfile?.createdAt ?: System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )
                                updateUserProfile(updatedProfile)
                                // Show success message
                            } catch (e: Exception) {
                                // Handle error
                            } finally {
                                isSaving = false
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PersonalInfoHeader(onBackClick: () -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Kişisel Bilgiler",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFFF6B35)
        )
    }
}

@Composable
private fun ProfilePhotoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color.Gray.copy(alpha = 0.2f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Gray
                        )
                    }
                }
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clickable { },
                    shape = CircleShape,
                    color = Color(0xFFFF6B35)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Camera",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Profil Fotoğrafını Değiştir",
                fontSize = 14.sp,
                color = Color(0xFFFF6B35),
                fontWeight = FontWeight.Medium
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun PersonalInfoForm(
    name: String,
    email: String,
    location: String,
    height: String,
    weight: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onWeightChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Kişisel Bilgiler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PersonalInfoField(
                label = "İsim",
                value = name,
                onValueChange = onNameChange,
                icon = Icons.Default.Person
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PersonalInfoField(
                label = "E-posta",
                value = email,
                onValueChange = onEmailChange,
                icon = Icons.Default.Email
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PersonalInfoField(
                label = "Konum",
                value = location,
                onValueChange = onLocationChange,
                icon = Icons.Default.LocationOn
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PersonalInfoField(
                    label = "Boy (cm)",
                    value = height,
                    onValueChange = onHeightChange,
                    icon = Icons.Default.Height,
                    modifier = Modifier.weight(1f)
                )
                
                PersonalInfoField(
                    label = "Kilo (kg)",
                    value = weight,
                    onValueChange = onWeightChange,
                    icon = Icons.Default.MonitorWeight,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PersonalInfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF6B35),
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                focusedLabelColor = Color(0xFFFF6B35)
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
private fun SaveButton(
    isSaving: Boolean,
    onSaveClick: () -> Unit
) {
    Spacer(modifier = Modifier.height(32.dp))
    Button(
        onClick = onSaveClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isSaving,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6B35)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = "Kaydet",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Firebase helper functions
private suspend fun loadUserProfile(): UserProfile? {
    return try {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return null
        val document = FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .await()
        
        document.toObject(UserProfile::class.java)
    } catch (e: Exception) {
        null
    }
}

private suspend fun updateUserProfile(userProfile: UserProfile) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    FirebaseFirestore.getInstance()
        .collection("users")
        .document(userId)
        .set(userProfile)
        .await()
} 