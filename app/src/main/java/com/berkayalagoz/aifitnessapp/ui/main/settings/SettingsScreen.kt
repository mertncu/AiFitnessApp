package com.berkayalagoz.aifitnessapp.ui.main.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.berkayalagoz.aifitnessapp.ui.main.settings.components.SettingsSection
import com.berkayalagoz.aifitnessapp.ui.main.settings.components.SettingsItem
import com.berkayalagoz.aifitnessapp.ui.main.settings.components.SettingsItemRow

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    var showPersonalInfo by remember { mutableStateOf(false) }
    
    if (showPersonalInfo) {
        PersonalInformationScreen(
            onBackClick = { showPersonalInfo = false }
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Header
            item {
                SettingsHeader(onBackClick = onBackClick)
            }
            
            // Account Section
            item {
                SettingsSection(title = "Hesap") {
                    SettingsItemRow(
                        SettingsItem.NavigationItem(
                            icon = Icons.Default.Person,
                            title = "Kişisel Bilgiler",
                            subtitle = "Profilinizi düzenleyin",
                            onClick = { showPersonalInfo = true }
                        )
                    )
                    SettingsItemRow(
                        SettingsItem.NavigationItem(
                            icon = Icons.Default.Security,
                            title = "Güvenlik",
                            subtitle = "Şifre ve güvenlik ayarları"
                        )
                    )
                    SettingsItemRow(
                        SettingsItem.NavigationItem(
                            icon = Icons.Default.Notifications,
                            title = "Bildirimler",
                            subtitle = "Bildirim tercihlerinizi yönetin"
                        )
                    )
                }
            }
            
            // App Settings Section
            item {
                SettingsSection(title = "Uygulama") {
                    SettingsItemRow(
                        SettingsItem.NavigationItem(
                            icon = Icons.Default.Language,
                            title = "Dil",
                            subtitle = "Türkçe"
                        )
                    )
                    SettingsItemRow(
                        SettingsItem.SwitchItem(
                            icon = Icons.Default.DarkMode,
                            title = "Karanlık Mod",
                            subtitle = "Koyu tema kullan",
                            checked = false,
                            onCheckedChange = { }
                        )
                    )
                    SettingsItemRow(
                        SettingsItem.NavigationItem(
                            icon = Icons.Default.Storage,
                            title = "Veri Kullanımı",
                            subtitle = "Depolama ve veri ayarları"
                        )
                    )
                }
            }
            
            // Support Section
            item {
                SettingsSection(title = "Destek") {
                    SettingsItemRow(
                        SettingsItem.NavigationItem(
                            icon = Icons.Default.Help,
                            title = "Yardım Merkezi",
                            subtitle = "SSS ve destek"
                        )
                    )
                    SettingsItemRow(
                        SettingsItem.NavigationItem(
                            icon = Icons.Default.Feedback,
                            title = "Geri Bildirim",
                            subtitle = "Görüşlerinizi paylaşın"
                        )
                    )
                    SettingsItemRow(
                        SettingsItem.NavigationItem(
                            icon = Icons.Default.Info,
                            title = "Hakkında",
                            subtitle = "Versiyon 1.0.0"
                        )
                    )
                }
            }
            
            // Sign Out Section
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SignOutCard(onSignOutClick = onSignOutClick)
            }
        }
    }
}

@Composable
private fun SettingsHeader(onBackClick: () -> Unit) {
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
            text = "Ayarlar",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
}

@Composable
private fun SignOutCard(onSignOutClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSignOutClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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