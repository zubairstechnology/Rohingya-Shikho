package com.zubtech.rohingyashikho.presentation.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.presentation.ui.theme.AppPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onAnimationFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }
    
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    
    // Main float for center hero
    val floatCenter by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatCenter"
    )
    
    // Float and rotate for small decorative element (graphic2) at top-left
    val floatSmall1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatSmall1"
    )
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(3500)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFFFFF), Color(0xFFE8F0FF), Color(0xFFD1E4FF))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative Circular PNG 2 (Top Left)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 80.dp, start = 30.dp)
                .offset(y = floatSmall1.dp)
                .size(90.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, Color.White, CircleShape)
                .alpha(if (startAnimation) 0.9f else 0f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.graphic2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().rotate(-rotation),
                contentScale = ContentScale.Crop
            )
        }

        // The central content (Hero + Text)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.offset(y = floatCenter.dp)
            ) {
                // Soft glow background behind hero
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .scale(if (startAnimation) 1.1f else 0.8f)
                        .alpha(if (startAnimation) 0.12f else 0f)
                        .background(AppPrimary, CircleShape)
                )
                
                // Main Character (graphic1)
                Image(
                    painter = painterResource(id = R.drawable.graphic1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(360.dp)
                        .scale(if (startAnimation) 1f else 0.6f)
                        .alpha(if (startAnimation) 1f else 0f),
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(tween(1200)) + expandVertically(tween(1200))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Rohingya Shikho",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    
                    Text(
                        text = "Empowering Through Language",
                        style = MaterialTheme.typography.titleMedium,
                        color = AppPrimary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}
