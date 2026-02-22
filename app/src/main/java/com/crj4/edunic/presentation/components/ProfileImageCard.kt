package com.crj4.edunic.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ProfileImageCard(
    bitmap: Bitmap? = null,  // Imagen de perfil, null si no hay
    size: Int = 160           // Tamaño del círculo
) {
    val infiniteTransition = rememberInfiniteTransition()
    // Animación de shimmer: valor de desplazamiento horizontal
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f, // valor grande para recorrer el placeholder
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Card(
        shape = RoundedCornerShape(100.dp),
        elevation = CardDefaults.cardElevation(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .size(size.dp)
            .padding(10.dp)
    ) {
        if (bitmap != null) {
            // Mostrar imagen real
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Imagen de perfil",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder circular con shimmer tipo Facebook
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.6f),
                                Color.Gray.copy(alpha = 0.2f),
                                Color.Gray.copy(alpha = 0.6f)
                            ),
                            start = Offset(shimmerTranslate - 200f, 0f),
                            end = Offset(shimmerTranslate, shimmerTranslate)
                        )
                    )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileImageCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        ProfileImageCard() // shimmer animado
        ProfileImageCard(bitmap = null) // shimmer animado
    }
}
