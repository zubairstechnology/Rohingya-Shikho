package com.zubtech.rohingyashikho.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedBeautyBackground(color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "beauty_bg")
    val animOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Canvas(modifier = Modifier.fillMaxSize().blur(100.dp)) {
        drawCircle(
            color = color.copy(alpha = 0.12f),
            center = Offset(animOffset, animOffset / 1.5f),
            radius = 450.dp.toPx()
        )
        drawCircle(
            color = color.copy(alpha = 0.08f),
            center = Offset(size.width - animOffset, size.height - animOffset / 2),
            radius = 400.dp.toPx()
        )
    }
}
