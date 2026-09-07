package com.zubtech.rohingyashikho.presentation.lesson

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    onNavigateBack: () -> Unit,
    onPracticeWriting: (String) -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.lesson?.title ?: "Lesson", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppPrimary)
                }
            } else if (uiState.isCompleted) {
                LessonCompletionScreen(onNavigateBack)
            } else {
                uiState.currentItem?.let { item ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Progress bar
                        LinearProgressIndicator(
                            progress = 0.5f, // Mock progress
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = AppPrimary,
                            trackColor = AppPrimary.copy(alpha = 0.1f)
                        )
                        
                        Spacer(modifier = Modifier.height(40.dp))

                        // Animated Content for switching items
                        AnimatedContent(
                            targetState = item,
                            transitionSpec = {
                                fadeIn(tween(400)) + scaleIn(initialScale = 0.9f) togetherWith
                                fadeOut(tween(400)) + scaleOut(targetScale = 1.1f)
                            },
                            label = "item_transition"
                        ) { targetItem ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Card(
                                    modifier = Modifier.size(240.dp),
                                    shape = RoundedCornerShape(32.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FF)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                            Text(
                                                text = targetItem.scriptText,
                                                fontSize = 100.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Text(
                                    text = targetItem.transliteration,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AppPrimary
                                )

                                Text(
                                    text = targetItem.englishMeaning,
                                    fontSize = 18.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Practice Writing Button
                        Button(
                            onClick = { onPracticeWriting(item.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(56.dp).padding(horizontal = 16.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Practice Writing", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Audio Controls
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Listen Button
                            FloatingActionButton(
                                onClick = { viewModel.playAudio() },
                                containerColor = AppPrimary,
                                contentColor = Color.White,
                                shape = CircleShape,
                                modifier = Modifier.size(80.dp)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Play", modifier = Modifier.size(36.dp))
                            }

                            // Record Button with Pulse
                            val infiniteTransition = rememberInfiniteTransition(label = "recording")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = if (uiState.isRecording) 1.2f else 1f,
                                animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                                label = "pulse"
                            )

                            Box(contentAlignment = Alignment.Center) {
                                if (uiState.isRecording) {
                                    Box(
                                        modifier = Modifier.size(80.dp).scale(scale).clip(CircleShape).background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                                    )
                                }
                                FloatingActionButton(
                                    onClick = {
                                        if (uiState.isRecording) viewModel.stopRecording()
                                        else viewModel.startRecording(context.cacheDir)
                                    },
                                    containerColor = if (uiState.isRecording) MaterialTheme.colorScheme.error else Color.White,
                                    contentColor = if (uiState.isRecording) Color.White else Color.Black,
                                    shape = CircleShape,
                                    modifier = Modifier.size(80.dp).border(2.dp, if (uiState.isRecording) Color.Transparent else Color.LightGray, CircleShape)
                                ) {
                                    Icon(
                                        if (uiState.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                        contentDescription = "Record",
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.moveToNextItem() },
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppPrimary)
                        ) {
                            Text("Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonCompletionScreen(onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Rounded.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = AppPrimary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Lesson Completed!",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "You've successfully mastered Hanifi script for today.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(60.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text("Back to Home", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
