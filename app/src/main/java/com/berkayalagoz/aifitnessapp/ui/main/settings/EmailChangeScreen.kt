package com.berkayalagoz.aifitnessapp.ui.main.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun EmailChangeScreen(onBackClick: () -> Unit) {
    var currentEmail by remember { mutableStateOf("") }
    var newEmail by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    
    // Load current email
    LaunchedEffect(Unit) {
        currentEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        isLoading = false
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
            EmailChangeHeader(onBackClick = onBackClick)
        }
        
        if (isLoading) {
            item {
                LoadingSection()
            }
        } else {
            // Current Email Section
            item {
                CurrentEmailSection(currentEmail = currentEmail)
            }
            
            // New Email Form
            item {
                NewEmailForm(
                    newEmail = newEmail,
                    password = password,
                    onNewEmailChange = { newEmail = it },
                    onPasswordChange = { password = it }
                )
            }
            
            // Error/Success Messages
            if (errorMessage.isNotEmpty()) {
                item {
                    ErrorMessage(errorMessage)
                }
            }
            
            if (successMessage.isNotEmpty()) {
                item {
                    SuccessMessage(successMessage)
                }
            }
            
            // Update Button
            item {
                UpdateEmailButton(
                    isSaving = isSaving,
                    enabled = newEmail.isNotEmpty() && password.isNotEmpty() && newEmail != currentEmail,
                    onUpdateClick = {
                        scope.launch {
                            isSaving = true
                            errorMessage = ""
                            successMessage = ""
                            
                            try {
                                updateUserEmail(currentEmail, newEmail, password)
                                successMessage = "E-posta adresiniz başarıyla güncellendi!"
                                currentEmail = newEmail
                                newEmail = ""
                                password = ""
                            } catch (e: Exception) {
                                errorMessage = when {
                                    e.message?.contains("wrong-password") == true -> "Şifreniz yanlış!"
                                    e.message?.contains("email-already-in-use") == true -> "Bu e-posta adresi zaten kullanımda!"
                                    e.message?.contains("invalid-email") == true -> "Geçersiz e-posta adresi!"
                                    e.message?.contains("requires-recent-login") == true -> "Güvenlik nedeniyle tekrar giriş yapmanız gerekiyor!"
                                    else -> "E-posta güncellenirken bir hata oluştu: ${e.message}"
                                }
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
private fun EmailChangeHeader(onBackClick: () -> Unit) {
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
            text = "E-posta Değiştir",
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
private fun CurrentEmailSection(currentEmail: String) {
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
                text = "Mevcut E-posta",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Email,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = currentEmail,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun NewEmailForm(
    newEmail: String,
    password: String,
    onNewEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
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
                text = "Yeni E-posta Bilgileri",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // New Email Field
            Text(
                text = "Yeni E-posta Adresi",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newEmail,
                onValueChange = onNewEmailChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                placeholder = { Text("yeni@email.com") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedLabelColor = Color(0xFFFF6B35)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password Field
            Text(
                text = "Mevcut Şifreniz",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                placeholder = { Text("Şifrenizi girin") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF6B35),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedLabelColor = Color(0xFFFF6B35)
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFD32F2F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFFD32F2F)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun SuccessMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color(0xFF4CAF50)
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun UpdateEmailButton(
    isSaving: Boolean,
    enabled: Boolean,
    onUpdateClick: () -> Unit
) {
    Button(
        onClick = onUpdateClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isSaving,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6B35),
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
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
                text = "E-postayı Güncelle",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Firebase helper function
private suspend fun updateUserEmail(currentEmail: String, newEmail: String, password: String) {
    val user = FirebaseAuth.getInstance().currentUser ?: throw Exception("User not authenticated")
    val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(currentEmail, password)
    
    // Re-authenticate user
    user.reauthenticate(credential).await()
    
    // Update email
    user.updateEmail(newEmail).await()
    
    // Update email in Firestore
    val userId = user.uid
    FirebaseFirestore.getInstance()
        .collection("users")
        .document(userId)
        .update("email", newEmail)
        .await()
} 