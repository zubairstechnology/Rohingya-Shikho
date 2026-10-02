package com.zubtech.rohingyashikho.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.*
import com.zubtech.rohingyashikho.R

@Composable
fun QuizVictoryDialog(
    score: Int,
    total: Int,
    onDismiss: () -> Unit
) {
    var animateTrigger by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animateTrigger = true
    }

    val dialogScale by animateFloatAsState(
        targetValue = if (animateTrigger) 1f else 0.85f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "dialog_scale"
    )

    // Dynamic Status Logic based on performance
    val incorrectCount = total - score
    val (statusText, statusColor, statusBgColor) = when {
        score == total -> Triple("PERFECT SCORE!", Color(0xFFD97706), Color(0xFFFEF3C7))
        incorrectCount < 5 -> Triple("EXCELLENT EFFORT!", Color(0xFF16A34A), Color(0xFFDCFCE7))
        incorrectCount < 7 -> Triple("GOOD EFFORT!", Color(0xFF2563EB), Color(0xFFDBEAFE))
        else -> Triple("KEEP LEARNING!", Color(0xFFDC2626), Color(0xFFFEE2E2))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .scale(dialogScale),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                statusBgColor.copy(alpha = 0.3f),
                                Color.White
                            )
                        )
                    )
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebration Header Animation
                Box(contentAlignment = Alignment.Center, modifier = Modifier.height(130.dp)) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(statusColor.copy(alpha = 0.2f), Color.Transparent)
                                ),
                                CircleShape
                            )
                    )
                    
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.student_animation))
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(150.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Quiz Completed!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B),
                    letterSpacing = (-0.5).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Dynamic Status Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .background(statusBgColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Rounded.EmojiEvents,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        color = statusColor,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Beautiful Dynamic Progress Ring Container
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(130.dp)
                        .background(Color(0xFFF8FAFC), CircleShape)
                        .padding(12.dp)
                ) {
                    val progressFactor = if (total > 0) (score.toFloat() / total.toFloat()) else 0f
                    val animatedProgress by animateFloatAsState(
                        targetValue = progressFactor,
                        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                        label = "circular_progress"
                    )
                    
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 10.dp,
                        color = statusColor,
                        trackColor = Color(0xFFE2E8F0),
                        strokeCap = StrokeCap.Round
                    )
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "$score",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "/$total",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(bottom = 6.dp, start = 1.dp)
                            )
                        }
                        Text(
                            text = "CORRECT ANSWERS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF94A3B8),
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = statusColor.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = statusColor
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Rounded.Star, null, modifier = Modifier.size(20.dp), tint = Color.White)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Continue Journey",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}