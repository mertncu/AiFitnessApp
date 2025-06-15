package com.berkayalagoz.aifitnessapp.ui.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.berkayalagoz.aifitnessapp.model.UserProfile
import com.berkayalagoz.aifitnessapp.ui.main.settings.SettingsScreen
import com.berkayalagoz.aifitnessapp.ui.main.settings.Base64Image
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    onSignOutClick: () -> Unit
) {
    var showSettings by remember { mutableStateOf(false) }
    var showCoverImageSelector by remember { mutableStateOf(false) }
    var currentUserProfile by remember { mutableStateOf(userProfile) }
    
    // Update currentUserProfile when userProfile changes
    LaunchedEffect(userProfile) {
        currentUserProfile = userProfile
    }
    
    if (showSettings) {
        SettingsScreen(
            onBackClick = { showSettings = false },
            onSignOutClick = onSignOutClick
        )
    } else if (showCoverImageSelector) {
        CoverImageSelectorScreen(
            onBackClick = { showCoverImageSelector = false },
            onCoverImageSelected = { coverImageId ->
                // Update local state immediately for UI responsiveness
                currentUserProfile = currentUserProfile.copy(coverImageId = coverImageId)
                // Save to Firebase
                updateUserCoverImage(currentUserProfile.userId, coverImageId)
                showCoverImageSelector = false
            }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Cover Image Header
            item {
                ProfileCoverSection(
                    userProfile = currentUserProfile,
                    onCoverImageClick = { showCoverImageSelector = true }
                )
            }
            
            // Profile Info Section
            item {
                ProfileInfoSection(currentUserProfile)
            }
            
            // Settings Section
            item {
                ProfileSettingsSection(onSettingsClick = { showSettings = true })
            }
            
            // Sign Out Section
            item {
                Spacer(modifier = Modifier.height(16.dp))
                ProfileSignOutSection(onSignOutClick)
            }
        }
    }
}

@Composable
private fun ProfileCoverSection(
    userProfile: UserProfile,
    onCoverImageClick: () -> Unit
) {
    // Get cover image template based on user's selection
    val coverTemplate = getCoverImageTemplate(userProfile.coverImageId)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Cover image with gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            coverTemplate.primaryColor,
                            coverTemplate.secondaryColor
                        )
                    )
                )
        ) {
            // Decorative fitness icon pattern
            Icon(
                coverTemplate.icon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(32.dp)
                    .size(120.dp)
                    .alpha(coverTemplate.iconAlpha),
                tint = Color.White
            )
            
            // Cover image change button
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(40.dp)
                    .clickable { onCoverImageClick() },
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Change Cover Image",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        
        // Profile photo - positioned to overlap with content below
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = 25.dp) // Moved up from 40.dp to 25.dp
                .zIndex(1f) // Ensure profile photo is above other content
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Base64Image(
                    base64String = userProfile.profileImageUrl,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    clipToCircle = true
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoSection(userProfile: UserProfile) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(45.dp)) // Space for overlapping profile photo (reduced from 60.dp)
        
        // Email with flag
        Text(
            text = if (userProfile.email.isNotEmpty()) {
                "${userProfile.email} 🇹🇷"
            } else {
                "kullanici@email.com 🇹🇷"
            },
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Location and membership
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
            Text(
                text = userProfile.location.ifEmpty { "Türkiye" },
                fontSize = 14.sp,
                color = Color.Gray
            )
            Text(
                text = "•",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.Gray
            )
            Text(
                text = userProfile.membershipType,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileSettingsSection(onSettingsClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onSettingsClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color(0xFF666666),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Ayarlar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun ProfileSignOutSection(onSignOutClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { onSignOutClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ExitToApp,
                contentDescription = "Sign Out",
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Çıkış Yap",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFD32F2F)
            )
        }
    }
}

// Function to update user cover image in Firebase
private fun updateUserCoverImage(userId: String, coverImageId: String) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "coverImageId" to coverImageId,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .addOnSuccessListener {
                    println("Cover image updated successfully")
                }
                .addOnFailureListener { e ->
                    println("Error updating cover image: ${e.message}")
                }
        } catch (e: Exception) {
            println("Error updating cover image: ${e.message}")
        }
    }
}

// Helper function to get cover image template
private fun getCoverImageTemplate(coverImageId: String): CoverImageTemplate {
    return when (coverImageId) {
        "orange_gradient" -> CoverImageTemplate(
            id = "orange_gradient",
            name = "Orange Power",
            primaryColor = Color(0xFFFF6B35),
            secondaryColor = Color(0xFFFF8A50),
            icon = Icons.Default.LocalFireDepartment
        )
        "blue_ocean" -> CoverImageTemplate(
            id = "blue_ocean",
            name = "Ocean Blue",
            primaryColor = Color(0xFF1976D2),
            secondaryColor = Color(0xFF42A5F5),
            icon = Icons.Default.Pool
        )
        "purple_energy" -> CoverImageTemplate(
            id = "purple_energy",
            name = "Purple Energy",
            primaryColor = Color(0xFF7B1FA2),
            secondaryColor = Color(0xFFAB47BC),
            icon = Icons.Default.Bolt
        )
        "green_nature" -> CoverImageTemplate(
            id = "green_nature",
            name = "Nature Green",
            primaryColor = Color(0xFF388E3C),
            secondaryColor = Color(0xFF66BB6A),
            icon = Icons.Default.Eco
        )
        else -> CoverImageTemplate( // Default: fitness_dark
            id = "fitness_dark",
            name = "Fitness Dark",
            primaryColor = Color(0xFF2C2C2E),
            secondaryColor = Color(0xFF1C1C1E),
            icon = Icons.Default.FitnessCenter
        )
    }
} 