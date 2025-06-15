package com.berkayalagoz.aifitnessapp.ui.main.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CoverImageTemplate(
    val id: String,
    val name: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val icon: ImageVector,
    val iconAlpha: Float = 0.3f
)

@Composable
fun CoverImageSelectorScreen(
    onBackClick: () -> Unit,
    onCoverImageSelected: (String) -> Unit
) {
    val coverImageTemplates = remember {
        listOf(
            CoverImageTemplate(
                id = "fitness_dark",
                name = "Fitness Dark",
                primaryColor = Color(0xFF2C2C2E),
                secondaryColor = Color(0xFF1C1C1E),
                icon = Icons.Default.FitnessCenter
            ),
            CoverImageTemplate(
                id = "orange_gradient",
                name = "Orange Power",
                primaryColor = Color(0xFFFF6B35),
                secondaryColor = Color(0xFFFF8A50),
                icon = Icons.Default.LocalFireDepartment
            ),
            CoverImageTemplate(
                id = "blue_ocean",
                name = "Ocean Blue",
                primaryColor = Color(0xFF1976D2),
                secondaryColor = Color(0xFF42A5F5),
                icon = Icons.Default.Pool
            ),
            CoverImageTemplate(
                id = "purple_energy",
                name = "Purple Energy",
                primaryColor = Color(0xFF7B1FA2),
                secondaryColor = Color(0xFFAB47BC),
                icon = Icons.Default.Bolt
            ),
            CoverImageTemplate(
                id = "green_nature",
                name = "Nature Green",
                primaryColor = Color(0xFF388E3C),
                secondaryColor = Color(0xFF66BB6A),
                icon = Icons.Default.Eco
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header
        CoverImageSelectorHeader(onBackClick = onBackClick)
        
        // Grid of cover image templates
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(coverImageTemplates) { template ->
                CoverImageTemplateCard(
                    template = template,
                    onSelected = { onCoverImageSelected(template.id) }
                )
            }
        }
    }
}

@Composable
private fun CoverImageSelectorHeader(onBackClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                text = "Kapak Resmi Seç",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun CoverImageTemplateCard(
    template: CoverImageTemplate,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onSelected() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            template.primaryColor,
                            template.secondaryColor
                        )
                    )
                )
        ) {
            // Background icon
            Icon(
                template.icon,
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(40.dp)
                    .alpha(template.iconAlpha),
                tint = Color.White
            )
            
            // Template name
            Text(
                text = template.name,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
} 