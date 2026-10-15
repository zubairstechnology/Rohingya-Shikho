package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.presentation.ui.components.QuizVictoryDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun ConsonantsScreen(
    onNavigateBack: () -> Unit,
    onNextClick: () -> Unit = {},
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val consonants by viewModel.consonants.collectAsState()
    val allProgress by viewModel.allProgress.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val playingItemId by viewModel.playingItemId.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()

    var selectedMode by remember { mutableStateOf("Consonant") }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var quizScore by remember { mutableIntStateOf(0) }

    val clickCounts = remember(allProgress) {
        allProgress.associate { it.lessonItemId to it.clickCount }
    }

    val displayConsonants = remember(consonants, selectedMode) {
        if (selectedMode == "Recognition") consonants.shuffled() else consonants
    }

    // Shared shine animation clock for performance
    val infiniteTransition = rememberInfiniteTransition(label = "global_shining")
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = 450f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shineOffset"
    )

    if (showVictoryDialog) {
        QuizVictoryDialog(
            score = quizScore,
            total = consonants.size,
            medal = userStats.consonantMedal,
            onDismiss = {
                showVictoryDialog = false
                selectedMode = "Consonant"
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF4C1D95), Color(0xFF2E1065), Color(0xFF1E1B4B))
                )
            )
    ) {
        DynamicConsonantBackgroundOrbs()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = { ConsonantsTopBar(onNavigateBack) }
        ) { padding ->
            Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                ConsonantProgressCard(userStats)
                ConsonantModeSwitcher(selectedMode, onModeSelected = { selectedMode = it })
                Spacer(modifier = Modifier.height(14.dp))
                Surface(
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp)),
                    color = Color(0xFFF8FAFC)
                ) {
                    if (selectedMode == "Quiz") {
                        EmbeddedConsonantQuiz(
                            items = consonants,
                            onFinish = { score ->
                                viewModel.completeConsonantLevel(score)
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
                                itemsIndexed(displayConsonants) { index, item ->
                                    val countKey = if (selectedMode == "Recognition") "rev_consonant_${item.id}" else item.id
                                    val isItemPlaying = isAudioPlaying && playingItemId == item.id
                                    AnimatedLuxuryLetterItem(
                                        item = item,
                                        index = index,
                                        clickCount = clickCounts[countKey] ?: 0,
                                        isPlaying = isItemPlaying,
                                        shineOffset = shineOffset,
                                        onClick = {
                                            viewModel.playAudio(item)
                                            if (selectedMode == "Consonant") {
                                                viewModel.recordClick(item.id)
                                            } else {
                                                viewModel.recordReviewClick(item.id, "consonant")
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
fun AnimatedLuxuryLetterItem(
    item: LessonItem,
    index: Int,
    clickCount: Int,
    isPlaying: Boolean,
    shineOffset: Float,
    onClick: () -> Unit
) {
    val state = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(tween(350, (index % 12) * 40)) + scaleIn(tween(350, (index % 12) * 40), initialScale = 0.82f)
    ) {
        LuxuryLetterCard(item, index, clickCount, isPlaying, shineOffset, onClick)
    }
}

@Composable
fun LuxuryLetterCard(
    item: LessonItem,
    index: Int,
    clickCount: Int,
    isPlaying: Boolean,
    shineOffset: Float,
    onClick: () -> Unit
) {
    val gradients = listOf(
        listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)),
        listOf(Color(0xFF10B981), Color(0xFF059669)),
        listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
        listOf(Color(0xFFEC4899), Color(0xFFF43F5E)),
        listOf(Color(0xFF3B82F6), Color(0xFF2563EB)),
        listOf(Color(0xFF06B6D4), Color(0xFF0891B2))
    )
    val currentGradient = gradients[index % gradients.size]
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val borderBrush = if (isPressed) {
        Brush.linearGradient(
            colors = listOf(currentGradient[0], currentGradient[1], Color.White, currentGradient[0]),
            start = Offset(shineOffset, shineOffset),
            end = Offset(shineOffset + 100f, shineOffset + 100f)
        )
    } else {
        Brush.linearGradient(
            colors = if (isPlaying) currentGradient else currentGradient.map { it.copy(alpha = 0.6f) }
        )
    }

    val textStyle = if (isPressed) {
        TextStyle(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF1E293B), Color.White, Color(0xFF1E293B)),
                start = Offset(shineOffset, shineOffset),
                end = Offset(shineOffset + 50f, shineOffset + 50f)
            )
        )
    } else {
        TextStyle(color = if (isPlaying) currentGradient[0] else Color(0xFF1E293B))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(
                elevation = if (isPlaying) 12.dp else 4.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = currentGradient[0].copy(alpha = if (isPlaying) 0.6f else 0.3f)
            )
            .border(
                width = if (isPressed || isPlaying) 3.dp else 1.5.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Icon(
                Icons.AutoMirrored.Rounded.VolumeUp, null,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp).size(14.dp),
                tint = currentGradient[0].copy(alpha = 0.4f)
            )
            if (clickCount > 0) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).size(18.dp)
                        .background(currentGradient[0], CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = clickCount.toString(), fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
            }
            Column(
                modifier = Modifier.align(Alignment.Center).padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = item.scriptText,
                    style = textStyle.copy(fontSize = 36.sp, fontWeight = FontWeight.Black),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.transliteration,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = currentGradient[0].copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EmbeddedConsonantQuiz(items: List<LessonItem>, onFinish: (Int) -> Unit, playAudio: (LessonItem) -> Unit) {
    val scope = rememberCoroutineScope()
    var currentIdx by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    val results = remember { mutableStateListOf<Boolean?>() }.apply { if (isEmpty()) repeat(items.size) { add(null) } }
    val currentQuestion = remember(currentIdx, items) {
        if (items.isNotEmpty()) {
            val correct = items[currentIdx]
            val options = (items.filter { it.id != correct.id }.shuffled().take(3) + correct).shuffled()
            QuizQuestionData(correct, options)
        } else null
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                results.forEach { res ->
                    val dotColor = when(res) { true -> Color(0xFF10B981); false -> Color(0xFFEF4444); else -> Color(0xFFE2E8F0) }
                    Box(modifier = Modifier.weight(1f).height(6.dp).clip(CircleShape).background(dotColor))
                }
            }
            currentQuestion?.let { question ->
                Surface(onClick = { playAudio(question.correctItem) }, modifier = Modifier.size(64.dp).shadow(6.dp, CircleShape), shape = CircleShape, color = Color.Transparent) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF8B5CF6), Color(0xFF6366F1)))), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, modifier = Modifier.size(28.dp), tint = Color.White)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Text(text = "Select the correct Hanifi script for:", fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(4.dp))
                Text(text = question.correctItem.transliteration, fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF8B5CF6), textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                val optionColors = listOf(Color(0xFF8B5CF6), Color(0xFF00BFA5), Color(0xFF007AFF), Color(0xFFF50057))
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        question.options.chunked(2).forEachIndexed { rowIndex, rowOptions ->
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                rowOptions.forEachIndexed { colIndex, option ->
                                    val globalIndex = rowIndex * 2 + colIndex
                                    Card(modifier = Modifier.weight(1f).height(72.dp).clickable {
                                        val isCorrect = option.id == question.correctItem.id
                                        results[currentIdx] = isCorrect
                                        if (isCorrect) score++
                                        scope.launch { delay(300); if (currentIdx < items.size - 1) currentIdx++ else onFinish(score) }
                                    }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                                        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Surface(color = optionColors[globalIndex % optionColors.size], shape = CircleShape, modifier = Modifier.size(32.dp)) {
                                                Box(contentAlignment = Alignment.Center) { Text(text = ('A' + globalIndex).toString(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black) }
                                            }
                                            Spacer(Modifier.width(12.dp))
                                            Text(text = option.scriptText, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
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
fun DynamicConsonantBackgroundOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(animation = tween(8000, easing = LinearEasing), repeatMode = RepeatMode.Restart),
        label = "offset"
    )
    Box(modifier = Modifier.fillMaxSize().drawBehind {
        val width = size.width
        val height = size.height
        drawCircle(brush = Brush.radialGradient(colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.24f), Color.Transparent)), radius = width * 0.65f, center = Offset(x = width * 0.25f + sin(waveOffset) * 80f, y = height * 0.15f + sin(waveOffset + 1.2f) * 60f))
        drawCircle(brush = Brush.radialGradient(colors = listOf(Color(0xFFD946EF).copy(alpha = 0.18f), Color.Transparent)), radius = width * 0.55f, center = Offset(x = width * 0.75f + sin(waveOffset + 2.4f) * 70f, y = height * 0.35f + sin(waveOffset) * 90f))
    })
}

@Composable
fun ConsonantsTopBar(onNavigateBack: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 22.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.size(44.dp).shadow(4.dp, CircleShape).background(Color.White, CircleShape)) {
                Icon(imageVector = Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B), modifier = Modifier.size(24.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("BEGINNER STAGE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFFFFD700), letterSpacing = 2.5.sp)
                Text("Consonants", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 0.25.sp)
            }
            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
fun ConsonantModeSwitcher(selectedMode: String, onModeSelected: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp).height(52.dp).background(Color.White.copy(alpha = 0.14f), RoundedCornerShape(26.dp)).padding(4.dp)) {
        val bias by animateFloatAsState(targetValue = when(selectedMode) { "Consonant" -> -1f; "Recognition" -> 0f; else -> 1f }, animationSpec = spring(dampingRatio = 0.84f, stiffness = Spring.StiffnessLow), label = "bias")
        Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.33f).align(BiasAlignment(horizontalBias = bias, verticalBias = 0f)).shadow(6.dp, RoundedCornerShape(22.dp)).background(Brush.verticalGradient(listOf(Color.White, Color(0xFFF8FAFC))), RoundedCornerShape(22.dp)))
        Row(modifier = Modifier.fillMaxSize()) {
            ConsonantModeTab("Letters", selectedMode == "Consonant") { onModeSelected("Consonant") }
            ConsonantModeTab("Review", selectedMode == "Recognition") { onModeSelected("Recognition") }
            ConsonantModeTab("Quiz", selectedMode == "Quiz") { onModeSelected("Quiz") }
        }
    }
}

@Composable
fun RowScope.ConsonantModeTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxHeight().weight(1f).clip(RoundedCornerShape(22.dp)).clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() }, contentAlignment = Alignment.Center) {
        Text(text = label, color = if (isSelected) Color(0xFF4C1D95) else Color.White.copy(alpha = 0.85f), fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun ConsonantProgressCard(userStats: UserStatsEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).shadow(10.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF4C1D95).copy(alpha = 0.3f)), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(22.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(88.dp)) {
                val total = (userStats.consonantProgress + userStats.recognitionProgress + userStats.quizProgress) / 3f
                CircularProgressIndicator(progress = { total }, modifier = Modifier.fillMaxSize(), strokeWidth = 8.dp, color = Color(0xFF7C3AED), trackColor = Color(0xFFF1F5F9), strokeCap = StrokeCap.Round)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${(total * 100).toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                    Text("MASTERED", fontSize = 8.sp, color = Color(0xFF7C3AED), fontWeight = FontWeight.Black)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(start = 18.dp)) {
                ConsonantProgressMiniItem("Learning", Color(0xFF7C3AED), "${(userStats.consonantProgress * 100).toInt()}%")
                ConsonantProgressMiniItem("Review", Color(0xFF2563EB), "${(userStats.recognitionProgress * 100).toInt()}%")
                ConsonantProgressMiniItem("Quiz", Color(0xFF10B981), "${(userStats.quizProgress * 100).toInt()}%")
            }
        }
    }
}

@Composable
fun ConsonantProgressMiniItem(label: String, color: Color, percentage: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(140.dp)) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
        Spacer(Modifier.weight(1f))
        Text(percentage, fontSize = 12.sp, fontWeight = FontWeight.Black, color = color)
    }
}
