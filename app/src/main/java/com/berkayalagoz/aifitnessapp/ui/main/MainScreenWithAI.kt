package com.berkayalagoz.aifitnessapp.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.berkayalagoz.aifitnessapp.ui.main.screens.*
import com.berkayalagoz.aifitnessapp.ui.main.components.BottomNavigationBar
import com.berkayalagoz.aifitnessapp.ui.main.components.LoadingScreen
import com.berkayalagoz.aifitnessapp.ui.main.components.ErrorScreen
import com.berkayalagoz.aifitnessapp.ui.main.components.NoProfileScreen

// Navigation Tab enum
enum class NavigationTab(val icon: ImageVector, val label: String) {
    HOME(Icons.Default.Home, "Anasayfa"),
    ASSESSMENT(Icons.Default.Assessment, "Assessment"),
    WORKOUT(Icons.Default.FitnessCenter, "Antrenman"),
    PROFILE(Icons.Default.Person, "Profil")
}

@Composable
fun MainScreenWithAI(
    viewModel: MainViewModel,
    onSignOutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            is MainUiState.Loading -> {
                LoadingScreen()
            }
            is MainUiState.Success -> {
                when (selectedTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            userProfile = state.userProfile,
                            aiRecommendations = state.aiRecommendations,
                            onRefreshClick = { viewModel.refreshRecommendations() },
                            onSignOutClick = onSignOutClick
                        )
                    }
                    NavigationTab.ASSESSMENT -> {
                        AssessmentScreen(onSignOutClick = onSignOutClick)
                    }
                    NavigationTab.WORKOUT -> {
                        WorkoutScreen(
                            userProfile = state.userProfile,
                            onSignOutClick = onSignOutClick
                        )
                    }
                    NavigationTab.PROFILE -> {
                        ProfileScreen(
                            userProfile = state.userProfile,
                            onSignOutClick = onSignOutClick
                        )
                    }
                }
            }
            is MainUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetryClick = { viewModel.refreshRecommendations() },
                    onSignOutClick = onSignOutClick
                )
            }
            is MainUiState.NoProfile -> {
                NoProfileScreen(onSignOutClick = onSignOutClick)
            }
        }
        
        // Bottom Navigation
        BottomNavigationBar(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
} 