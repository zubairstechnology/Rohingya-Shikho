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
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
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
import com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.presentation.ui.components.AnimatedBeautyBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

data class QuizQuestionData(
    val correctItem: LessonItem,
    val options: List<LessonItem>
)

@Composable
fun QuizOptionButton(
    label: String,
    text: String,
    bgColor: Color,
    borderColor: Color,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (label.isNotEmpty()) {
                Surface(
                    color = accentColor,
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(label, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
                Spacer(Modifier.width(12.dp))
            }
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.weight(1f),
                textAlign = if (label.isEmpty()) TextAlign.Center else TextAlign.Start
            )
        }
    }
}

@Composable
fun QuizResultView(score: Int, onFinish: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Rounded.EmojiEvents, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(100.dp))
            Spacer(Modifier.height(24.dp))
            Text("Quiz Finished!", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            Text("Your Score: $score / 28", fontSize = 20.sp, color = Color.Gray)
            Spacer(Modifier.height(48.dp))
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth().height(64.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0369A1))
            ) {
                Text("Save & Continue", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
fun ConsonantsScreen(
    onNavigateBack: () -> Unit,
    onItemClick: (String) -> Unit = {},
    onNextClick: () -> Unit = {},
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val consonants by viewModel.consonants.collectAsState()
    val allProgress by viewModel.allProgress.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val playingItemId by viewModel.playingItemId.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()

    var selectedMode by remember { mutableStateOf("Consonant") }
    var showUnlockDialog by remember { mutableStateOf(false) }

    val clickCounts = remember(allProgress) {
        allProgress.associate { it.lessonItemId to it.clickCount }
    }

    val recognitionOrder = remember {
        listOf(
            "𐴁", "𐴏", "𐴃", "𐴘", "𐴈", "𐴎", "𐴉", "𐴌", "𐴕", "𐴆", "𐴛", "𐴐", "𐴊",
            "𐴔", "𐴄", "𐴙", "𐴇", "𐴍", "𐴀", "𐴒", "𐴖", "𐴂", "𐴋", "𐴓", "𐴑", "𐴗", "𐴚", "𐴅"
        )
    }

    val displayConsonants = remember(consonants, selectedMode) {
        if (selectedMode == "Recognition") {
            recognitionOrder.mapNotNull { script ->
                consonants.find { it.scriptText.replace("𐴢", "") == script }
            }.ifEmpty { consonants }
        } else {
            consonants
        }
    }

    LaunchedEffect(clickCounts) {
        val totalClicks = clickCounts.values.sum()
        if (!userStats.isAlphabetQuizUnlocked && totalClicks >= 140) {
            viewModel.unlockAlphabetQuiz()
            showUnlockDialog = true
        }
    }

    if (showUnlockDialog) {
        AlertDialog(
            onDismissRequest = { 
                showUnlockDialog = false
                selectedMode = "Recognition"
            },
            title = { Text("Congratulations!", fontWeight = FontWeight.Black, color = Color(0xFF6366F1)) },
            text = { Text("You have completed the learning phase! Recognition and Quiz modes are now unlocked.") },
            confirmButton = {
                Button(
                    onClick = { 
                        showUnlockDialog = false
                        selectedMode = "Recognition"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
                ) {
                    Text("Go to Recognition")
                }
            },
            shape = RoundedCornerShape(24.dp),
            containerColor = Color.White
        )
    }

    if (selectedMode == "Quiz") {
        ConsonantQuizView(
            items = consonants,
            onNavigateBack = { selectedMode = "Consonant" },
            onFinish = { score ->
                viewModel.completeConsonantLevel(score)
                selectedMode = "Consonant"
            },
            playAudio = { viewModel.playAudio(it) }
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedBeautyBackground(Color(0xFF6366F1))

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
                            Text("Lesson 2", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.9f))
                            Text("Consonants", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }
                        Spacer(Modifier.weight(1f))

                        IconButton(
                            onClick = onNextClick,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowForward, "Next", tint = Color.White)
                        }
                    }
                }
            ) { padding ->
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    
                    ProgressSection(userStats)

                    // Switch Section
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 14.dp)
                            .height(56.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .padding(6.dp)
                    ) {
                        val bias by animateFloatAsState(
                            targetValue = when(selectedMode) {
                                "Consonant" -> -1f
                                "Recognition" -> 0f
                                else -> 1f
                            },
                            label = "bias"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.33f)
                                .align(BiasAlignment(horizontalBias = bias, verticalBias = 0f))
                                .shadow(6.dp, RoundedCornerShape(16.dp))
                                .background(Color.White, RoundedCornerShape(16.dp))
                        )

                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxSize().clip(RoundedCornerShape(16.dp)).clickable { selectedMode = "Consonant" },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Learn", color = if (selectedMode == "Consonant") Color(0xFF1E293B) else Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                            Box(
                                modifier = Modifier.weight(1f).fillMaxSize().clip(RoundedCornerShape(16.dp)).clickable { 
                                    if (userStats.isAlphabetQuizUnlocked) selectedMode = "Recognition"
                                },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Test", color = if (selectedMode == "Recognition") Color(0xFF1E293B) else Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    if (!userStats.isAlphabetQuizUnlocked) Icon(Icons.Rounded.Lock, null, tint = Color.White, modifier = Modifier.size(10.dp))
                                }
                            }
                            Box(
                                modifier = Modifier.weight(1f).fillMaxSize().clip(RoundedCornerShape(16.dp)).clickable { 
                                    if (userStats.isAlphabetQuizUnlocked) selectedMode = "Quiz"
                                },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Quiz", color = if (selectedMode == "Quiz") Color(0xFF1E293B) else Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    if (!userStats.isAlphabetQuizUnlocked) Icon(Icons.Rounded.Lock, null, tint = Color.White, modifier = Modifier.size(10.dp))
                                }
                            }
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
                                itemsIndexed(displayConsonants) { index, item ->
                                    AnimatedAlphabetItem(
                                        item = item,
                                        index = index,
                                        clickCount = if (selectedMode == "Consonant") (clickCounts[item.id] ?: 0) else null,
                                    ) {
                                        when (selectedMode) {
                                            "Consonant" -> {
                                                viewModel.playAudio(item)
                                                viewModel.recordClick(item.id)
                                            }
                                            "Recognition" -> {
                                                viewModel.playAudio(item)
                                                scope.launch {
                                                    delay(10000)
                                                    Toast.makeText(context, "Remember this letter: ${item.transliteration}", Toast.LENGTH_SHORT).show()
                                                }
                                                val progress = (clickCounts.size.toFloat() / consonants.size.toFloat()).coerceAtMost(1f)
                                                viewModel.updateRecognitionProgress(progress)
                                            }
                                            else -> onItemClick(item.id)
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
fun ProgressSection(userStats: UserStatsEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp)
            .shadow(8.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val combinedProgress = (userStats.consonantProgress + userStats.recognitionProgress + userStats.quizProgress) / 3f
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(110.dp)) {
                    CircularProgressIndicator(
                        progress = combinedProgress,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 10.dp,
                        color = Color(0xFF6366F1),
                        trackColor = Color(0xFFF1F5F9),
                        strokeCap = StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${(combinedProgress * 100).toInt()}%", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                        Text("TOTAL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SmallProgressItem("Learning", userStats.consonantProgress, Color(0xFF8B5CF6))
                    SmallProgressItem("Testing", userStats.recognitionProgress, Color(0xFF3B82F6))
                    SmallProgressItem("Quiz", userStats.quizProgress, Color(0xFFF59E0B))
                }
            }
        }
    }
}

@Composable
fun SmallProgressItem(label: String, progress: Float, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(38.dp)) {
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 4.dp,
                color = color,
                trackColor = Color(0xFFF1F5F9),
                strokeCap = StrokeCap.Round
            )
            Text("${(progress * 100).toInt()}%", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
        }
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF475569))
    }
}

@Composable
fun ConsonantQuizView(
    items: List<LessonItem>,
    onNavigateBack: () -> Unit,
    onFinish: (Int) -> Unit,
    playAudio: (LessonItem) -> Unit
) {
    val context = LocalContext.current
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }
    
    val answerResults = remember { mutableStateListOf<Boolean?>() }
    if (answerResults.isEmpty()) {
        repeat(28) { answerResults.add(null) }
    }

    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    
    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status -> }
        ttsInstance.language = Locale.ENGLISH
        tts = ttsInstance
        onDispose { ttsInstance.stop(); ttsInstance.shutdown() }
    }

    fun speak(text: String) { tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null) }
    
    val currentQuestion = remember(currentQuestionIndex, items) {
        if (items.isNotEmpty()) {
            val correctItem = items[currentQuestionIndex]
            val options = (items.filter { it.id != correctItem.id }.shuffled().take(3) + correctItem).shuffled()
            QuizQuestionData(correctItem, options)
        } else null
    }

    LaunchedEffect(currentQuestionIndex, tts) {
        if (tts != null && currentQuestion != null) {
            speak("Which letter is ${currentQuestion.correctItem.transliteration}?")
        }
    }

    if (isFinished) {
        QuizResultView(score = score, onFinish = { onFinish(score) })
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F9FF))) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 24.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color(0xFF0369A1)) }
                    Spacer(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Rohingya Shikho", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF0369A1))
                        Text("Learn • Practice • Grow", fontSize = 12.sp, color = Color(0xFF0369A1).copy(alpha = 0.7f))
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {}) { Icon(Icons.Default.Settings, null, tint = Color(0xFF0369A1)) }
                }

                // Header
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Hanifi Rohingya Alphabet", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E3A8A))
                    Surface(color = Color(0xFFDBEAFE), shape = RoundedCornerShape(16.dp), modifier = Modifier.padding(top = 4.dp)) {
                        Text("Letter Recognition Quiz", modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                    }
                }

                // Status Card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color(0xFFE0F2FE), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Rounded.List, null, tint = Color(0xFF0369A1), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Question", fontSize = 10.sp, color = Color.Gray)
                                    Text("${currentQuestionIndex + 1} / 28", fontSize = 14.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            val startWindow = (currentQuestionIndex / 10) * 10
                            for (i in startWindow until minOf(startWindow + 10, 28)) {
                                val dotColor = when(answerResults[i]) {
                                    true -> Color(0xFF22C55E)
                                    false -> Color(0xFFEF4444)
                                    else -> Color(0xFFE2E8F0)
                                }
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotColor))
                            }
                        }
                        
                        Spacer(Modifier.weight(1f))
                        Surface(color = Color(0xFFFFF7ED), shape = RoundedCornerShape(12.dp)) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Star, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text("Score", fontSize = 10.sp, color = Color.Gray)
                                    Text("$score", fontSize = 14.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                // Quiz Card
                currentQuestion?.let { question ->
                    Card(
                        modifier = Modifier.fillMaxWidth().weight(1f).padding(20.dp),
                        shape = RoundedCornerShape(32.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { playAudio(question.correctItem) }, modifier = Modifier.size(64.dp).background(Color(0xFF3B82F6), CircleShape)) {
                                    Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, tint = Color.White, modifier = Modifier.size(32.dp))
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text("Listen", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E40AF))
                                    Text("Tap to hear pronunciation", fontSize = 12.sp, color = Color.Gray)
                                }
                            }
                            Spacer(Modifier.height(32.dp))
                            Text(text = buildAnnotatedString { append("Which letter is "); withStyle(SpanStyle(color = Color(0xFF3B82F6))) { append(question.correctItem.transliteration) }; append("?") }, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                            Spacer(Modifier.height(32.dp))
                            
                            val colors = listOf(Color(0xFFEFF6FF), Color(0xFFF0FDF4), Color(0xFFFAF5FF), Color(0xFFFEF2F2))
                            val borders = listOf(Color(0xFFBFDBFE), Color(0xFFBBF7D0), Color(0xFFE9D5FF), Color(0xFFFECACA))
                            val accents = listOf(Color(0xFF3B82F6), Color(0xFF22C55E), Color(0xFF8B5CF6), Color(0xFFEF4444))

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                repeat(2) { r ->
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        repeat(2) { c ->
                                            val i = r * 2 + c
                                            QuizOptionButton(
                                                label = ('A' + i).toString(),
                                                text = question.options[i].transliteration,
                                                bgColor = colors[i], borderColor = borders[i], accentColor = accents[i],
                                                modifier = Modifier.weight(1f),
                                                onClick = {
                                                    val isCorrect = question.options[i].id == question.correctItem.id
                                                    answerResults[currentQuestionIndex] = isCorrect
                                                    if (isCorrect) score++
                                                    if (currentQuestionIndex < 27) currentQuestionIndex++ else isFinished = true
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Bottom Nav
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- }, modifier = Modifier.height(56.dp).weight(1f).padding(end = 12.dp), shape = RoundedCornerShape(28.dp), border = BorderStroke(2.dp, Color(0xFFE2E8F0))) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null); Spacer(Modifier.width(8.dp)); Text("Previous", fontWeight = FontWeight.Bold)
                    }
                    Button(onClick = { if (currentQuestionIndex < 27) currentQuestionIndex++ }, modifier = Modifier.height(56.dp).weight(1f).padding(start = 12.dp), shape = RoundedCornerShape(28.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0369A1))) {
                        Text("Next", fontWeight = FontWeight.Bold); Spacer(Modifier.width(8.dp)); Icon(Icons.AutoMirrored.Rounded.ArrowForward, null)
                    }
                }
            }
        }
    }
}
