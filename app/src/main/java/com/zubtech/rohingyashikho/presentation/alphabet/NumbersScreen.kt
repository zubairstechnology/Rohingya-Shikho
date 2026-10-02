package com.zubtech.rohingyashikho.presentation.alphabet

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.presentation.ui.components.QuizVictoryDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun NumbersScreen(
    onNavigateBack: () -> Unit,
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val numbers by viewModel.numbers.collectAsState()
    val allProgress by viewModel.allProgress.collectAsState()
    val userStats by viewModel.userStats.collectAsState()

    var selectedMode by remember { mutableStateOf("Number") }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var quizScore by remember { mutableIntStateOf(0) }

    // Accurate separate count association based on mode
    val clickCounts = remember(allProgress) {
        allProgress.associate { it.lessonItemId to it.clickCount }
    }

    val displayNumbers = remember(numbers, selectedMode) {
        if (selectedMode == "Recognition") numbers.shuffled() else numbers
    }

    if (showVictoryDialog) {
        QuizVictoryDialog(
            score = quizScore,
            total = numbers.size,
            onDismiss = {
                showVictoryDialog = false
                selectedMode = "Number"
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB45309), // Amber/Orange
                        Color(0xFF92400E),
                        Color(0xFF451A03)
                    )
                )
            )
    ) {
        DynamicNumberBackgroundOrbs()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                NumbersTopBar(onNavigateBack)
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                NumberProgressCard(userStats)

                NumberModeSwitcher(
                    selectedMode = selectedMode,
                    onModeSelected = { selectedMode = it }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp)),
                    color = Color(0xFFF8FAFC)
                ) {
                    if (selectedMode == "Quiz") {
                        EmbeddedNumberQuiz(
                            items = numbers,
                            onFinish = { score ->
                                viewModel.completeNumberLevel(score)
                                quizScore = score
                                showVictoryDialog = true
                            },
                            playAudio = { viewModel.playAudio(it) }
                        )
                    } else {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 110.dp, start = 20.dp, end = 20.dp, top = 26.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                itemsIndexed(displayNumbers) { index, item ->
                                    val countKey = if (selectedMode == "Recognition") "rev_number_${item.id}" else item.id
                                    AnimatedNumberItem(
                                        item = item,
                                        index = index,
                                        clickCount = clickCounts[countKey] ?: 0,
                                        onClick = {
                                            viewModel.playAudio(item)
                                            if (selectedMode == "Number") {
                                                viewModel.recordNumberClick(item.id)
                                            } else {
                                                viewModel.recordReviewClick(item.id, "number")
                                            }
                                        }
                                    )
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
fun EmbeddedNumberQuiz(
    items: List<LessonItem>,
    onFinish: (Int) -> Unit,
    playAudio: (LessonItem) -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentIdx by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    val results = remember { mutableStateListOf<Boolean?>() }.apply { 
        if (isEmpty()) repeat(items.size) { add(null) } 
    }

    val currentQuestion = remember(currentIdx, items) {
        if (items.isNotEmpty()) {
            val correct = items[currentIdx]
            val options = (items.filter { it.id != correct.id }.shuffled().take(3) + correct).shuffled()
            QuizQuestionData(correct, options)
        } else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress Dots Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                results.forEach { res ->
                    val dotColor = when(res) {
                        true -> Color(0xFF10B981)
                        false -> Color(0xFFEF4444)
                        else -> Color(0xFFE2E8F0)
                    }
                    Box(modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape).background(dotColor))
                }
            }

            currentQuestion?.let { question ->
                // Beautiful smaller sound icon button
                Surface(
                    onClick = { playAudio(question.correctItem) },
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(6.dp, CircleShape),
                    shape = CircleShape,
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFF59E0B), Color(0xFFD97706))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.VolumeUp,
                            null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                
                Text(
                    text = "Select the correct Hanifi digit for:", 
                    fontSize = 13.sp, 
                    color = Color(0xFF64748B), 
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(4.dp))
                
                // Question text (English/Transliteration) in smaller beautiful size
                Text(
                    text = question.correctItem.transliteration, 
                    fontSize = 32.sp, 
                    fontWeight = FontWeight.Black, 
                    color = Color(0xFFB45309),
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(24.dp))
                
                val optionColors = listOf(
                    Color(0xFF8B5CF6), // Purple
                    Color(0xFF00BFA5), // Teal/Green
                    Color(0xFF007AFF), // Blue
                    Color(0xFFF50057)  // Pink
                )

                // Options: 4 rounded border cards arranged layout
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        question.options.chunked(2).forEachIndexed { rowIndex, rowOptions ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowOptions.forEachIndexed { colIndex, option ->
                                    val globalIndex = rowIndex * 2 + colIndex
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(72.dp)
                                            .clickable {
                                                val isCorrect = option.id == question.correctItem.id
                                                results[currentIdx] = isCorrect
                                                if (isCorrect) score++
                                                scope.launch {
                                                    delay(300)
                                                    if (currentIdx < items.size - 1) currentIdx++ else onFinish(score)
                                                }
                                            },
                                        shape = RoundedCornerShape(18.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(horizontal = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Start
                                        ) {
                                            Surface(
                                                color = optionColors[globalIndex % optionColors.size],
                                                shape = CircleShape,
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = ('A' + globalIndex).toString(),
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Black
                                                    )
                                                }
                                            }
                                            
                                            Spacer(Modifier.width(12.dp))
                                            
                                            Text(
                                                text = option.scriptText,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF0F172A)
                                            )
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
fun DynamicNumberBackgroundOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val width = size.width
                val height = size.height
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFBBF24).copy(alpha = 0.26f), Color.Transparent),
                    ),
                    radius = width * 0.65f,
                    center = Offset(
                        x = width * 0.25f + sin(waveOffset) * 80f,
                        y = height * 0.15f + sin(waveOffset + 1.2f) * 60f
                    )
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFF97316).copy(alpha = 0.18f), Color.Transparent),
                    ),
                    radius = width * 0.55f,
                    center = Offset(
                        x = width * 0.75f + sin(waveOffset + 2.4f) * 70f,
                        y = height * 0.35f + sin(waveOffset) * 90f
                    )
                )
            }
    )
}

@Composable
fun NumbersTopBar(onNavigateBack: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(44.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1E293B),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "BEGINNER STAGE", 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Black, 
                    color = Color(0xFFFFD700), 
                    letterSpacing = 2.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Numbers", 
                    fontSize = 28.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = Color.White,
                    letterSpacing = 0.25.sp
                )
            }

            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
fun NumberProgressCard(userStats: com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFFB45309).copy(alpha = 0.3f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(88.dp)) {
                val total = (userStats.numberProgress + userStats.numberRecognitionProgress + userStats.numberQuizProgress) / 3f
                CircularProgressIndicator(
                    progress = { total },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 8.dp,
                    color = Color(0xFFF59E0B),
                    trackColor = Color(0xFFF1F5F9),
                    strokeCap = StrokeCap.Round
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${(total * 100).toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                    Text("MASTERED", fontSize = 8.sp, color = Color(0xFFF59E0B), fontWeight = FontWeight.Black)
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(start = 18.dp)
            ) {
                NumberProgressMiniItem("Learning", Color(0xFFF59E0B), "${(userStats.numberProgress * 100).toInt()}%")
                NumberProgressMiniItem("Review", Color(0xFF2563EB), "${(userStats.numberRecognitionProgress * 100).toInt()}%")
                NumberProgressMiniItem("Quiz", Color(0xFF10B981), "${(userStats.numberQuizProgress * 100).toInt()}%")
            }
        }
    }
}

@Composable
fun NumberProgressMiniItem(label: String, color: Color, percentage: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(140.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
        Spacer(Modifier.weight(1f))
        Text(percentage, fontSize = 12.sp, fontWeight = FontWeight.Black, color = color)
    }
}

@Composable
fun NumberModeSwitcher(
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp)
            .height(52.dp)
            .background(Color.White.copy(alpha = 0.14f), RoundedCornerShape(26.dp))
            .padding(4.dp)
    ) {
        val bias by animateFloatAsState(
            targetValue = when(selectedMode) {
                "Number" -> -1f
                "Recognition" -> 0f
                else -> 1f
            },
            animationSpec = spring(dampingRatio = 0.84f, stiffness = Spring.StiffnessLow),
            label = "bias"
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.33f)
                .align(BiasAlignment(horizontalBias = bias, verticalBias = 0f))
                .shadow(6.dp, RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFF8FAFC))
                    ), 
                    RoundedCornerShape(22.dp)
                )
        )

        Row(modifier = Modifier.fillMaxSize()) {
            NumberModeTab("Digits", selectedMode == "Number") { onModeSelected("Number") }
            NumberModeTab("Review", selectedMode == "Recognition") { onModeSelected("Recognition") }
            NumberModeTab("Quiz", selectedMode == "Quiz") { onModeSelected("Quiz") }
        }
    }
}

@Composable
fun RowScope.NumberModeTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clip(RoundedCornerShape(22.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label, 
            color = if (isSelected) Color(0xFFB45309) else Color.White.copy(alpha = 0.85f), 
            fontWeight = FontWeight.Bold, 
            fontSize = 14.sp
        )
    }
}

@Composable
fun AnimatedNumberItem(
    item: LessonItem,
    index: Int,
    clickCount: Int,
    onClick: () -> Unit
) {
    val state = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(tween(350, (index % 12) * 40)) + 
                scaleIn(tween(350, (index % 12) * 40), initialScale = 0.82f)
    ) {
        NumberDisplayItem(item, index, clickCount, onClick)
    }
}

@Composable
fun NumberDisplayItem(
    item: LessonItem,
    index: Int,
    clickCount: Int,
    onClick: () -> Unit
) {
    val accentColors = BirdColors
    val accentColor = accentColors[index % accentColors.size]
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f, 
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
        label = "scale"
    )

    val volumeScale by animateFloatAsState(
        targetValue = if (isPressed) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "volumeScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.88f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource, 
                indication = null
            ) { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(accentColor.copy(alpha = 0.08f), Color.Transparent),
                            center = Offset.Zero
                        )
                    )
            )

            // Volume Icon moved to Top Right corner with animation (TopStart in RTL layout)
            Icon(
                Icons.AutoMirrored.Rounded.VolumeUp,
                null,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(16.dp)
                    .graphicsLayer {
                        scaleX = volumeScale
                        scaleY = volumeScale
                    },
                tint = accentColor.copy(alpha = 0.6f)
            )

            Text(
                text = item.englishMeaning,
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp), // Top Left in RTL
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFCBD5E1)
            )

            if (clickCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd) // Bottom Right in RTL
                        .padding(8.dp)
                        .size(20.dp)
                        .background(accentColor, CircleShape)
                        .shadow(2.dp, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = clickCount.toString(), 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Black, 
                        color = Color.White
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.scriptText,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.transliteration,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private val BirdColors = listOf(
    Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF3B82F6),
    Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFF97316)
)
