package com.zubtech.rohingyashikho.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.presentation.ui.theme.AppPrimary

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onStartLearning: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.student_animation))
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFF8F9FF))
                )
            )
    ) {
        // Decorative background element
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-20).dp)
                .size(240.dp)
                .clip(CircleShape)
                .background(AppPrimary.copy(alpha = 0.05f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 60.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { -40 }
            ) {
                Column {
                    Text(
                        text = "Make every day\na learning\njourney",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 48.sp,
                        color = Color.Black,
                        letterSpacing = (-1).sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(AppPrimary)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier.size(320.dp),
                    iterations = LottieConstants.IterateForever
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(1200, 400)) + slideInVertically(tween(1200, 400)) { 40 }
                ) {
                    Text(
                        text = "Master the Hanifi script and connect with Rohingya culture through an interactive experience.",
                        fontSize = 17.sp,
                        color = Color.Gray.copy(alpha = 0.8f),
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

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
                        shape = RoundedCornerShape(22.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 2.dp
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Start Your Journey",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
