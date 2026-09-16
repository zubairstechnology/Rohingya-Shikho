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
import androidx.compose.ui.draw.shadow
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
import kotlin.math.absoluteValue

private val KidFriendlyColors = listOf(
    Color(0xFFFFE5D9), // Peach
    Color(0xFFFBFAF0), // Cream
    Color(0xFFFFCAD4), // Pink
    Color(0xFFB9FBC0), // Mint
    Color(0xFFCFBAF0), // Lavender
    Color(0xFFA3C4F3), // Sky Blue
    Color(0xFF90DBF4), // Cyan
    Color(0xFFF1C0E8)  // Mauve
)

private val AccentColors = listOf(
    Color(0xFFD8572A),
    Color(0xFFB5A442),
    Color(0xFFC94C68),
    Color(0xFF388E3C),
    Color(0xFF673AB7),
    Color(0xFF1976D2),
    Color(0xFF0097A7),
    Color(0xFF8E24AA)
)

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
                        // Thicker Progress bar for kids
                        LinearProgressIndicator(
                            progress = 0.5f, // Mock progress
                            modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                            color = AppPrimary,
                            trackColor = AppPrimary.copy(alpha = 0.1f)
                        )
                        
                        Spacer(modifier = Modifier.height(40.dp))

                        // Animated Content for switching items
                        AnimatedContent(
                            targetState = item,
                            transitionSpec = {
                                (fadeIn(tween(400)) + scaleIn(initialScale = 0.9f)) togetherWith
                                (fadeOut(tween(400)) + scaleOut(targetScale = 1.1f))
                            },
                            label = "item_transition"
                        ) { targetItem ->
                            val colorIndex = targetItem.id.hashCode().absoluteValue % KidFriendlyColors.size
                            val bgColor = KidFriendlyColors[colorIndex]
                            val accentColor = AccentColors[colorIndex]

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Card(
                                    modifier = Modifier
                                        .size(280.dp)
                                        .shadow(16.dp, RoundedCornerShape(48.dp), clip = false),
                                    shape = RoundedCornerShape(48.dp),
                                    colors = CardDefaults.cardColors(containerColor = bgColor),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                            Text(
                                                text = targetItem.scriptText,
                                                fontSize = 120.sp,
                                                fontWeight = FontWeight.Black,
                                                color = accentColor,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(32.dp))

                                Text(
                                    text = targetItem.transliteration,
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accentColor
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Word Row with Icons
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .background(Color(0xFFF8F9FF), RoundedCornerShape(20.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFD700),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Text(
                                        text = targetItem.englishMeaning,
                                        fontSize = 22.sp,
                                        color = Color.DarkGray,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    Spacer(modifier = Modifier.width(12.dp))
                                    
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Speak",
                                        tint = accentColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // Practice Writing Button
                        Button(
                            onClick = { onPracticeWriting(item.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.height(60.dp).padding(horizontal = 32.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("Practice Writing", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Audio Controls
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Listen Button
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                FloatingActionButton(
                                    onClick = { viewModel.playAudio() },
                                    containerColor = Color(0xFFE8F5E9),
                                    contentColor = Color(0xFF2E7D32),
                                    shape = CircleShape,
                                    modifier = Modifier.size(72.dp),
                                    elevation = FloatingActionButtonDefaults.elevation(0.dp)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Play", modifier = Modifier.size(32.dp))
                                }
                                Text("Listen", modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold, color = Color.Gray)
                            }

                            // Record Button with Pulse
                            val infiniteTransition = rememberInfiniteTransition(label = "recording")
                            val scale by infiniteTransition.animateFloat(
                                initialValue = 1f,
                                targetValue = if (uiState.isRecording) 1.2f else 1f,
                                animationSpec = infiniteRepeatable(tween(1000), RepeatMode.Reverse),
                                label = "pulse"
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(contentAlignment = Alignment.Center) {
                                    if (uiState.isRecording) {
                                        Box(
                                            modifier = Modifier.size(72.dp).scale(scale).clip(CircleShape).background(MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
                                        )
                                    }
                                    FloatingActionButton(
                                        onClick = {
                                            if (uiState.isRecording) viewModel.stopRecording()
                                            else viewModel.startRecording(context.cacheDir)
                                        },
                                        containerColor = if (uiState.isRecording) MaterialTheme.colorScheme.error else AppPrimary,
                                        contentColor = Color.White,
                                        shape = CircleShape,
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        Icon(
                                            if (uiState.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                                            contentDescription = "Record",
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }
                                Text(if (uiState.isRecording) "Stop" else "Speak", modifier = Modifier.padding(top = 8.dp), fontWeight = FontWeight.Bold, color = Color.Gray)
                            }
                        }

                        Button(
                            onClick = { viewModel.moveToNextItem() },
                            modifier = Modifier.fillMaxWidth().height(64.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppPrimary)
                        ) {
                            Text("Continue", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
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
