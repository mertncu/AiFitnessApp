package com.berkayalagoz.aifitnessapp.ui.main.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import com.berkayalagoz.aifitnessapp.model.UserProfile
import coil.compose.AsyncImage

@Composable
fun PersonalInformationScreen(onBackClick: () -> Unit) {
    var isLoading by remember { mutableStateOf(true) }
    var userProfile by remember { mutableStateOf<UserProfile?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var isUploadingImage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Form state - sadece boy, kilo ve profil resmi
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isUploadingImage = true
                try {
                    val downloadUrl = uploadImageToFirebase(it, context)
                    profileImageUrl = downloadUrl
                    // Immediately update the profile in Firebase
                    userProfile?.let { profile ->
                        val updatedProfile = profile.copy(
                            profileImageUrl = downloadUrl,
                            updatedAt = System.currentTimeMillis()
                        )
                        updateUserProfile(updatedProfile)
                        userProfile = updatedProfile
                    }
                } catch (e: Exception) {
                    // Handle error - you might want to show a toast or snackbar here
                } finally {
                    isUploadingImage = false
                }
            }
        }
    }
    
    // Load user profile on first composition
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val profile = loadUserProfile()
                profile?.let {
                    userProfile = it
                    height = it.height.toString()
                    weight = it.weight.toString()
                    profileImageUrl = it.profileImageUrl
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
                ProfilePhotoSection(
                    profileImageUrl = profileImageUrl,
                    isUploading = isUploadingImage,
                    onPhotoClick = { imagePickerLauncher.launch("image/*") }
                )
            }
            
            // Personal Info Form - sadece boy ve kilo
            item {
                PersonalInfoForm(
                    height = height,
                    weight = weight,
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
                                val updatedProfile = userProfile?.copy(
                                    height = height.toFloatOrNull() ?: userProfile?.height ?: 0f,
                                    weight = weight.toFloatOrNull() ?: userProfile?.weight ?: 0f,
                                    profileImageUrl = profileImageUrl,
                                    updatedAt = System.currentTimeMillis()
                                )
                                updatedProfile?.let { updateUserProfile(it) }
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
private fun ProfilePhotoSection(
    profileImageUrl: String,
    isUploading: Boolean,
    onPhotoClick: () -> Unit
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color.Gray.copy(alpha = 0.2f)
                ) {
                    Base64Image(
                        base64String = profileImageUrl,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        clipToCircle = true
                    )
                }
                
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .clickable { if (!isUploading) onPhotoClick() },
                    shape = CircleShape,
                    color = Color(0xFFFF6B35)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(12.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.CameraAlt,
                                contentDescription = "Camera",
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isUploading) "Yükleniyor..." else "Profil Fotoğrafını Değiştir",
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
    height: String,
    weight: String,
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
                text = "Fiziksel Bilgiler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
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

private suspend fun uploadImageToFirebase(uri: Uri, context: android.content.Context): String {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("User not authenticated")
    
    try {
        // Base64 encoding ile resmi Firestore'da saklayacağız
        val base64Image = ImageUtils.uriToBase64(context, uri, 512)
            ?: throw Exception("Resim işlenemedi")
        
        // Simulated delay for upload experience
        kotlinx.coroutines.delay(1500)
        
        // Base64 string'i "data:image/jpeg;base64," prefix'i ile döndürüyoruz
        // Bu şekilde AsyncImage doğrudan base64'ü gösterebilir
        return "data:image/jpeg;base64,$base64Image"
        
    } catch (e: Exception) {
        throw Exception("Image upload failed: ${e.message}")
    }
} 