package com.zubtech.rohingyashikho.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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

@Composable
fun GrammarPulseIndicator(color: Color, modifier: Modifier = Modifier.size(110.dp)) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Restart), label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Restart), label = "alpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .background(color.copy(alpha = 0.25f), CircleShape)
    )
}
