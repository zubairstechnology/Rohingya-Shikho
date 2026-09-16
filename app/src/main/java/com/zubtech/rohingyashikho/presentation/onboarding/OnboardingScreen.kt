package com.zubtech.rohingyashikho.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.presentation.ui.theme.AppPrimary

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onStartLearning: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Floating and Pulse animation for every_day.png
    val infiniteTransition = rememberInfiniteTransition(label = "hero_animation")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Responsive Decorative Blobs ---
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-100).dp, y = (-50).dp)
                .size(320.dp)
                .clip(CircleShape)
                .background(AppPrimary.copy(alpha = 0.05f))
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 120.dp)
                .size(380.dp)
                .clip(CircleShape)
                .background(AppPrimary.copy(alpha = 0.08f))
                .blur(100.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Main hero image with Animation
            Image(
                painter = painterResource(id = R.drawable.every_day),
                contentDescription = "Every Day is a Journey",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 400.dp, max = 650.dp)
                    .offset(y = offsetY.dp)
                    .scale(scale),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1200, 400)) + slideInVertically(tween(1200, 400)) { 40 }
            ) {
                Text(
                    text = "Master the Hanifi script and connect with Rohingya culture through an interactive experience.",
                    fontSize = 18.sp,
                    color = Color.Gray.copy(alpha = 0.8f),
                    lineHeight = 28.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1200, 800)) + slideInHorizontally(tween(1200, 800)) { 40 }
            ) {
                Button(
                    onClick = {
                        viewModel.completeOnboarding(onStartLearning)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                    shape = RoundedCornerShape(34.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Start Your Journey",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
