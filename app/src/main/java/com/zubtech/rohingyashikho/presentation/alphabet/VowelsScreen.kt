package com.zubtech.rohingyashikho.presentation.alphabet

import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.presentation.ui.components.AnimatedBeautyBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun VowelsScreen(
    onNavigateBack: () -> Unit,
    onItemClick: (String) -> Unit = {},
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val vowels by viewModel.vowels.collectAsState()
    val allProgress by viewModel.allProgress.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val playingItemId by viewModel.playingItemId.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()

    var selectedMode by remember { mutableStateOf("Vowel") }
    var showUnlockDialog by remember { mutableStateOf(false) }

    val clickCounts = remember(allProgress) {
        allProgress.associate { it.lessonItemId to it.clickCount }
    }

    val displayVowels = remember(vowels, selectedMode) {
        if (selectedMode == "Recognition") vowels.shuffled() else vowels
    }

    LaunchedEffect(clickCounts) {
        val vowelIds = vowels.map { it.id }
        val vowelClicks = allProgress.filter { it.lessonItemId in vowelIds }.sumOf { it.clickCount }
        if (!userStats.isAlphabetQuizUnlocked && vowelClicks >= 50) {
            viewModel.unlockAlphabetQuiz()
            showUnlockDialog = true
        }
    }

    if (showUnlockDialog) {
        AlertDialog(
            onDismissRequest = { showUnlockDialog = false },
            title = { Text("Level Up!", fontWeight = FontWeight.Black, color = Color(0xFF10B981)) },
            text = { Text("You've explored the vowels! Recognition and Quiz modes are now available.") },
            confirmButton = {
                Button(onClick = { showUnlockDialog = false; selectedMode = "Recognition" }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                    Text("Unlock Now")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    if (selectedMode == "Quiz") {
        VowelQuizView(
            items = vowels,
            onNavigateBack = { selectedMode = "Vowel" },
            onFinish = { score ->
                viewModel.completeVowelLevel(score)
                selectedMode = "Vowel"
            },
            playAudio = { viewModel.playAudio(it) }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedBeautyBackground(Color(0xFF10B981))

            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White, CircleShape)
                                .shadow(4.dp, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Color(0xFF1E293B))
                        }
                        Spacer(Modifier.width(20.dp))
                        Column {
                            Text("Learning Path", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.9f))
                            Text("Hanifi Vowels", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                    }
                }
            ) { padding ->
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    
                    VowelProgressCard(userStats)

                    // Mode Switcher
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp).height(56.dp).background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp)).padding(6.dp)) {
                        val bias by animateFloatAsState(targetValue = when(selectedMode) { "Vowel" -> -1f; "Recognition" -> 0f; else -> 1f }, label = "bias")
                        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.33f).align(BiasAlignment(bias, 0f)).shadow(6.dp, RoundedCornerShape(16.dp)).background(Color.White, RoundedCornerShape(16.dp)))
                        Row(modifier = Modifier.fillMaxSize()) {
                            ModeButton("Learn", selectedMode == "Vowel") { selectedMode = "Vowel" }
                            ModeButton("Test", selectedMode == "Recognition", !userStats.isAlphabetQuizUnlocked) { selectedMode = "Recognition" }
                            ModeButton("Quiz", selectedMode == "Quiz", !userStats.isAlphabetQuizUnlocked) { selectedMode = "Quiz" }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                        color = Color.White.copy(alpha = 0.95f),
                        shadowElevation = 12.dp
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp, start = 20.dp, end = 20.dp, top = 28.dp),
                                verticalArrangement = Arrangement.spacedBy(18.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                itemsIndexed(displayVowels) { index, item ->
                                    val isCurrentlyPlaying = isAudioPlaying && playingItemId == item.id
                                    AnimatedAlphabetItem(
                                        item = item,
                                        index = index,
                                        isCurrentlyPlaying = isCurrentlyPlaying,
                                        clickCount = if (selectedMode == "Vowel") (clickCounts[item.id] ?: 0) else null,
                                        accentColor = Color(0xFF10B981)
                                    ) {
                                        viewModel.playAudio(item)
                                        if (selectedMode == "Vowel") {
                                            viewModel.recordVowelClick(item.id)
                                            onItemClick(item.id)
                                        }
                                        if (selectedMode == "Recognition") {
                                            scope.launch {
                                                delay(800)
                                                Toast.makeText(context, "Sign for: ${item.transliteration}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
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
fun RowScope.ModeButton(label: String, isSelected: Boolean, isLocked: Boolean = false, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxHeight().width(0.dp).weight(1f).clip(RoundedCornerShape(16.dp)).clickable(enabled = !isLocked) { onClick() }, contentAlignment = Alignment.Center) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = if (isSelected) Color(0xFF1E293B) else Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
            if (isLocked) {
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Rounded.Lock, null, tint = Color.White, modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
fun VowelProgressCard(userStats: com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .shadow(8.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                CircularProgressIndicator(progress = userStats.vowelProgress, modifier = Modifier.fillMaxSize(), strokeWidth = 10.dp, color = Color(0xFF10B981), trackColor = Color(0xFFF1F5F9), strokeCap = StrokeCap.Round)
                Text("${(userStats.vowelProgress * 100).toInt()}%", fontSize = 20.sp, fontWeight = FontWeight.Black)
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                VowelStatItem("Learning", userStats.vowelProgress, Color(0xFF10B981))
                VowelStatItem("Mastery", userStats.vowelQuizProgress, Color(0xFFF59E0B))
            }
        }
    }
}

@Composable
fun VowelStatItem(label: String, progress: Float, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(36.dp)) {
            CircularProgressIndicator(progress = progress, modifier = Modifier.fillMaxSize(), strokeWidth = 4.dp, color = color, trackColor = Color(0xFFF1F5F9), strokeCap = StrokeCap.Round)
            Text("${(progress * 100).toInt()}%", fontSize = 8.sp, fontWeight = FontWeight.Black)
        }
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF475569))
    }
}

@Composable
fun VowelQuizView(items: List<LessonItem>, onNavigateBack: () -> Unit, onFinish: (Int) -> Unit, playAudio: (LessonItem) -> Unit) {
    val context = LocalContext.current
    var currentIdx by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    val results = remember { mutableStateListOf<Boolean?>() }.apply { if (isEmpty()) repeat(items.size) { add(null) } }

    val currentQuestion = remember(currentIdx) {
        val correct = items[currentIdx]
        val options = (items.filter { it.id != correct.id }.shuffled().take(3) + correct).shuffled()
        QuizQuestionData(correct, options)
    }

    if (isFinished) {
        VowelQuizResultView(score, items.size, onFinish = { onFinish(score) })
    } else {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0FDF4))) {
            Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color(0xFF065F46)) }
                Spacer(Modifier.weight(1f))
                Text("Vowel Quiz", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF065F46))
                Spacer(Modifier.weight(1f))
            }

            Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Question ${currentIdx + 1}/${items.size}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        results.forEach { res ->
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(when(res) { true -> Color.Green; false -> Color.Red; else -> Color.LightGray }))
                        }
                    }
                }
            }

            Card(modifier = Modifier.weight(1f).padding(16.dp), shape = RoundedCornerShape(32.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    IconButton(onClick = { playAudio(currentQuestion.correctItem) }, modifier = Modifier.size(80.dp).background(Color(0xFF10B981), CircleShape)) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("Select the vowel for: ${currentQuestion.correctItem.transliteration}", fontSize = 22.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(32.dp))
                    
                    currentQuestion.options.chunked(2).forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { opt ->
                                QuizOptionButton(label = "", text = opt.transliteration, bgColor = Color(0xFFF0FDF4), borderColor = Color(0xFFBBF7D0), accentColor = Color(0xFF10B981), modifier = Modifier.weight(1f)) {
                                    val correct = opt.id == currentQuestion.correctItem.id
                                    results[currentIdx] = correct
                                    if (correct) score++
                                    if (currentIdx < items.size - 1) currentIdx++ else isFinished = true
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun VowelQuizResultView(score: Int, total: Int, onFinish: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Rounded.EmojiEvents, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(100.dp))
            Spacer(Modifier.height(24.dp))
            Text("Quiz Finished!", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            Text("Your Score: $score / $total", fontSize = 20.sp, color = Color.Gray)
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
            ) {
                Text("Save & Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
