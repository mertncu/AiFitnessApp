package com.berkayalagoz.aifitnessapp.ui.main.settings

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

@Composable
fun Base64Image(
    base64String: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    clipToCircle: Boolean = false
) {
    var bitmap by remember(base64String) { mutableStateOf<android.graphics.Bitmap?>(null) }
    
    LaunchedEffect(base64String) {
        if (base64String.isNotEmpty()) {
            try {
                // Base64 string'den "data:image/jpeg;base64," prefix'ini kaldır
                val cleanBase64 = if (base64String.startsWith("data:image")) {
                    base64String.substringAfter("base64,")
                } else {
                    base64String
                }
                
                val decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT)
                bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                println("Base64Image: Successfully decoded bitmap")
            } catch (e: Exception) {
                println("Base64Image Error: ${e.message}")
                bitmap = null
            }
        }
    }
    
    bitmap?.let { bmp ->
        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = if (clipToCircle) modifier.clip(CircleShape) else modifier,
            contentScale = contentScale
        )
    } ?: run {
        // Fallback icon
        Box(
            modifier = modifier.background(Color.Gray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = contentDescription,
                modifier = Modifier.size(40.dp),
                tint = Color.Gray
            )
        }
    }
} 