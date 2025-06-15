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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun SecurityScreen(onBackClick: () -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Header
        item {
            SecurityHeader(onBackClick = onBackClick)
        }
        
        // Security Info Card
        item {
            SecurityInfoCard()
        }
        
        // Password Change Form
        item {
            PasswordChangeForm(
                currentPassword = currentPassword,
                newPassword = newPassword,
                confirmPassword = confirmPassword,
                showCurrentPassword = showCurrentPassword,
                showNewPassword = showNewPassword,
                showConfirmPassword = showConfirmPassword,
                onCurrentPasswordChange = { currentPassword = it },
                onNewPasswordChange = { newPassword = it },
                onConfirmPasswordChange = { confirmPassword = it },
                onToggleCurrentPassword = { showCurrentPassword = !showCurrentPassword },
                onToggleNewPassword = { showNewPassword = !showNewPassword },
                onToggleConfirmPassword = { showConfirmPassword = !showConfirmPassword }
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
        
        // Update Password Button
        item {
            UpdatePasswordButton(
                isLoading = isLoading,
                enabled = currentPassword.isNotEmpty() && 
                         newPassword.isNotEmpty() && 
                         confirmPassword.isNotEmpty() &&
                         newPassword == confirmPassword &&
                         newPassword.length >= 6,
                onUpdateClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = ""
                        successMessage = ""
                        
                        try {
                            updateUserPassword(currentPassword, newPassword)
                            successMessage = "Şifreniz başarıyla güncellendi!"
                            currentPassword = ""
                            newPassword = ""
                            confirmPassword = ""
                        } catch (e: Exception) {
                            errorMessage = when {
                                e.message?.contains("wrong-password") == true -> "Mevcut şifreniz yanlış!"
                                e.message?.contains("weak-password") == true -> "Yeni şifre çok zayıf! En az 6 karakter olmalı."
                                e.message?.contains("requires-recent-login") == true -> "Güvenlik nedeniyle tekrar giriş yapmanız gerekiyor!"
                                else -> "Şifre güncellenirken bir hata oluştu: ${e.message}"
                            }
                        } finally {
                            isLoading = false
                        }
                    }
                }
            )
        }
        
        // Security Tips
        item {
            SecurityTipsCard()
        }
    }
}

@Composable
private fun SecurityHeader(onBackClick: () -> Unit) {
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
            text = "Güvenlik",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun SecurityInfoCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Hesap Güvenliği",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
                Text(
                    text = "Şifrenizi düzenli olarak güncelleyerek hesabınızı güvende tutun.",
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun PasswordChangeForm(
    currentPassword: String,
    newPassword: String,
    confirmPassword: String,
    showCurrentPassword: Boolean,
    showNewPassword: Boolean,
    showConfirmPassword: Boolean,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onToggleCurrentPassword: () -> Unit,
    onToggleNewPassword: () -> Unit,
    onToggleConfirmPassword: () -> Unit
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
                text = "Şifre Değiştir",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Current Password
            PasswordField(
                label = "Mevcut Şifre",
                value = currentPassword,
                onValueChange = onCurrentPasswordChange,
                showPassword = showCurrentPassword,
                onToggleVisibility = onToggleCurrentPassword,
                placeholder = "Mevcut şifrenizi girin"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // New Password
            PasswordField(
                label = "Yeni Şifre",
                value = newPassword,
                onValueChange = onNewPasswordChange,
                showPassword = showNewPassword,
                onToggleVisibility = onToggleNewPassword,
                placeholder = "Yeni şifrenizi girin"
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Confirm Password
            PasswordField(
                label = "Yeni Şifre Tekrar",
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                showPassword = showConfirmPassword,
                onToggleVisibility = onToggleConfirmPassword,
                placeholder = "Yeni şifrenizi tekrar girin"
            )
            
            // Password validation info
            if (newPassword.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                PasswordValidationInfo(
                    password = newPassword,
                    confirmPassword = confirmPassword
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showPassword: Boolean,
    onToggleVisibility: () -> Unit,
    placeholder: String
) {
    Column {
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
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showPassword) "Şifreyi gizle" else "Şifreyi göster",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            placeholder = { Text(placeholder) },
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
private fun PasswordValidationInfo(
    password: String,
    confirmPassword: String
) {
    Column {
        ValidationItem(
            text = "En az 6 karakter",
            isValid = password.length >= 6
        )
        ValidationItem(
            text = "Şifreler eşleşiyor",
            isValid = password == confirmPassword && confirmPassword.isNotEmpty()
        )
    }
}

@Composable
private fun ValidationItem(
    text: String,
    isValid: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            if (isValid) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isValid) Color(0xFF4CAF50) else Color(0xFFFF5722),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isValid) Color(0xFF4CAF50) else Color(0xFFFF5722)
        )
    }
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
private fun UpdatePasswordButton(
    isLoading: Boolean,
    enabled: Boolean,
    onUpdateClick: () -> Unit
) {
    Button(
        onClick = onUpdateClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF6B35),
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = "Şifreyi Güncelle",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun SecurityTipsCard() {
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
                text = "Güvenlik İpuçları",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            SecurityTip(
                icon = Icons.Default.Security,
                text = "Güçlü bir şifre kullanın (en az 6 karakter)"
            )
            
            SecurityTip(
                icon = Icons.Default.Update,
                text = "Şifrenizi düzenli olarak güncelleyin"
            )
            
            SecurityTip(
                icon = Icons.Default.VisibilityOff,
                text = "Şifrenizi kimseyle paylaşmayın"
            )
            
            SecurityTip(
                icon = Icons.Default.PhonelinkLock,
                text = "Güvenilir cihazlarda oturum açın"
            )
        }
    }
}

@Composable
private fun SecurityTip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFFFF6B35),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

// Firebase helper function
private suspend fun updateUserPassword(currentPassword: String, newPassword: String) {
    val user = FirebaseAuth.getInstance().currentUser ?: throw Exception("User not authenticated")
    val email = user.email ?: throw Exception("User email not found")
    val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPassword)
    
    // Re-authenticate user
    user.reauthenticate(credential).await()
    
    // Update password
    user.updatePassword(newPassword).await()
    
    // Log security event in Firestore
    val userId = user.uid
    val securityLog = mapOf(
        "event" to "password_changed",
        "timestamp" to System.currentTimeMillis(),
        "userId" to userId
    )
    
    FirebaseFirestore.getInstance()
        .collection("security_logs")
        .add(securityLog)
        .await()
} 