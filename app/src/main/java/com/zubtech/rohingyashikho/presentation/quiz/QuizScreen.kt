package com.zubtech.rohingyashikho.presentation.quiz

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
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
                            .padding(horizontal = 16.dp)
                            .height(10.dp)
                            .clip(CircleShape),
                        color = AppPrimary,
                        trackColor = AppPrimary.copy(alpha = 0.1f)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
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
                            .padding(24.dp),
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
                        
                        Spacer(modifier = Modifier.height(32.dp))

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
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
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FF)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val instruction = when (question.type) {
                    QuizType.SCRIPT_TO_MEANING -> "What does this mean?"
                    QuizType.MEANING_TO_SCRIPT -> "Select the script for:"
                    QuizType.AUDIO_TO_SCRIPT -> "Listen and select the script"
                }
                
                Text(
                    text = instruction,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                when (question.type) {
                    QuizType.SCRIPT_TO_MEANING -> {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = question.item.scriptText,
                                fontSize = 84.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }
                    }
                    QuizType.MEANING_TO_SCRIPT -> {
                        Text(
                            text = question.item.englishMeaning,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = AppPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                    QuizType.AUDIO_TO_SCRIPT -> {
                        var isPlaying by remember { mutableStateOf(false) }
                        val scale by animateFloatAsState(if (isPlaying) 1.2f else 1f, label = "audio_scale")
                        
                        IconButton(
                            onClick = { isPlaying = true /* Trigger Audio */ },
                            modifier = Modifier.size(100.dp).scale(scale),
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = AppPrimary)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Play", modifier = Modifier.size(48.dp))
                        }
                        
                        LaunchedEffect(isPlaying) {
                            if (isPlaying) {
                                kotlinx.coroutines.delay(1000)
                                isPlaying = false
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
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.92f else 1f, label = "option_scale")

    Surface(
        onClick = { onClick() },
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        border = BorderStroke(2.dp, Color(0xFFEEEEEE)),
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
            if (isScript) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = text, 
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            } else {
                Text(
                    text = text, 
                    fontSize = 18.sp, 
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun QuizResultScreen(score: Int, total: Int, onFinish: () -> Unit) {
    val percentage = if (total > 0) (score.toFloat() / total.toFloat()) else 0f
    
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = percentage,
                modifier = Modifier.size(200.dp),
                strokeWidth = 12.dp,
                color = if (percentage > 0.7f) Color(0xFF4CAF50) else AppPrimary,
                trackColor = Color(0xFFF0F0F0)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black
                )
                Text(text = "Score", color = Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = if (percentage > 0.8f) "Outstanding!" else "Great Effort!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "You got $score out of $total correct answers",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text("Finish Quiz", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}
