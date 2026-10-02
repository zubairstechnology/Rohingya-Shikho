package com.zubtech.rohingyashikho.presentation.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.presentation.ui.theme.AppPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                        val progress by animateFloatAsState(
                            targetValue = if (uiState.questions.isNotEmpty()) {
                                (uiState.currentQuestionIndex.toFloat() / uiState.questions.size.toFloat())
                            } else 0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
                            label = "progress"
                        )
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(CircleShape),
                            color = AppPrimary,
                            trackColor = AppPrimary.copy(alpha = 0.15f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.DarkGray)
                    }
                },
                actions = {
                    Surface(
                        color = Color(0xFFFFF7ED),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = uiState.score.toString(),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC2410C),
                                fontSize = 14.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppPrimary)
                }
            } else if (uiState.isFinished) {
                QuizResultScreen(
                    score = uiState.score,
                    total = uiState.questions.size,
                    onFinish = onNavigateBack
                )
            } else {
                uiState.currentQuestion?.let { question ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Question content with transition
                        AnimatedContent(
                            targetState = question,
                            transitionSpec = {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                            },
                            label = "question_transition",
                            modifier = Modifier.weight(1f)
                        ) { targetQuestion ->
                            QuestionView(targetQuestion)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 24.dp)
                        ) {
                            items(question.options) { option ->
                                OptionCard(
                                    text = option,
                                    isScript = question.type != QuizType.SCRIPT_TO_MEANING,
                                    onClick = { viewModel.submitAnswer(option) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionView(question: QuizQuestion) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val instruction = when (question.type) {
                    QuizType.SCRIPT_TO_MEANING -> "What is the meaning of this letter?"
                    QuizType.MEANING_TO_SCRIPT -> "Select the Hanifi script for:"
                    QuizType.AUDIO_TO_SCRIPT -> "Listen and select the correct script"
                }

                Text(
                    text = instruction,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (question.type) {
                        QuizType.SCRIPT_TO_MEANING -> {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = question.item.scriptText,
                                    fontSize = 96.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                        QuizType.MEANING_TO_SCRIPT -> {
                            Text(
                                text = question.item.englishMeaning,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AppPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                        QuizType.AUDIO_TO_SCRIPT -> {
                            var isPlaying by remember { mutableStateOf(false) }
                            val scale by animateFloatAsState(
                                if (isPlaying) 1.15f else 1f,
                                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                                label = "audio_scale"
                            )

                            Button(
                                onClick = { isPlaying = true /* Trigger Audio logic should be in VM or here if injected */ },
                                modifier = Modifier
                                    .size(100.dp)
                                    .scale(scale),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(containerColor = AppPrimary),
                                contentPadding = PaddingValues(0.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                            ) {
                                Icon(
                                    Icons.Default.VolumeUp,
                                    contentDescription = "Play Audio",
                                    modifier = Modifier.size(42.dp),
                                    tint = Color.White
                                )
                            }

                            LaunchedEffect(isPlaying) {
                                if (isPlaying) {
                                    kotlinx.coroutines.delay(800)
                                    isPlaying = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionCard(text: String, isScript: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(2.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
            if (isScript) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = text,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            } else {
                Text(
                    text = text,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }
        }
    }
}

@Composable
fun QuizResultScreen(score: Int, total: Int, onFinish: () -> Unit) {
    val percentage = if (total > 0) (score.toFloat() / total.toFloat()) else 0f
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = percentage,
                modifier = Modifier.size(200.dp),
                strokeWidth = 12.dp,
                color = if (percentage > 0.7f) Color(0xFF10B981) else AppPrimary,
                trackColor = Color(0xFFF1F5F9)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B)
                )
                Text(text = "SCORE", fontWeight = FontWeight.Bold, color = Color(0xFF64748B), letterSpacing = 1.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = if (percentage > 0.8f) "Fantastic Achievement!" else "Well Done!",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )
        
        Text(
            text = "You correctly answered $score out of $total questions.",
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text("Complete", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
