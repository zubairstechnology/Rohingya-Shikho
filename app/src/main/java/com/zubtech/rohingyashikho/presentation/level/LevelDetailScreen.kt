package com.zubtech.rohingyashikho.presentation.level

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.zubtech.rohingyashikho.R
import com.airbnb.lottie.compose.*

@Composable
fun LevelDetailScreen(
    levelId: String,
    onNavigateBack: () -> Unit,
    onLessonSelected: (String) -> Unit,
    onConsonantsClick: () -> Unit,
    onVowelsClick: () -> Unit,
    onCombinationClick: () -> Unit,
    onQuizClick: (String) -> Unit,
    onHanifiIntroClick: () -> Unit,
    onNumbersClick: () -> Unit,
    onGrammarClick: () -> Unit,
    onWordBuildingClick: () -> Unit = {},
    onVocabularyClick: () -> Unit = {},
    onConversationClick: () -> Unit = {}
) {
    val themeColors = when (levelId) {
        "2" -> listOf(Color(0xFF0F766E), Color(0xFF14B8A6)) 
        "3" -> listOf(Color(0xFF059669), Color(0xFF34D399)) 
        else -> listOf(Color(0xFF4338CA), Color(0xFF818CF8)) 
    }

    val lessonsList = when (levelId) {
        "2" -> mockIntermediateLessons
        "3" -> mockAdvancedLessons
        else -> mockBeginnerLessons
    }

    val levelTitle = when (levelId) {
        "2" -> "Intermediate"
        "3" -> "Advanced"
        else -> "Beginner"
    }

    val levelStep = when (levelId) {
        "2" -> "Level 02"
        "3" -> "Level 03"
        else -> "Level 01"
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = themeColors[0].toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Brush.verticalGradient(themeColors))) {
            DynamicMeshHero()

            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                LevelDetailHero(levelId = levelId, levelTag = levelStep, levelTitle = levelTitle, onBack = onNavigateBack)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 45.dp, topEnd = 45.dp),
                    color = Color(0xFFF8FAFC)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Education Background Watermark
                        Column(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp, vertical = 60.dp).alpha(0.035f),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.MenuBook, null, modifier = Modifier.size(160.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(40.dp)) {
                                Icon(Icons.Rounded.School, null, modifier = Modifier.size(100.dp))
                                Icon(Icons.Rounded.Class, null, modifier = Modifier.size(100.dp))
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp, top = 32.dp, start = 20.dp, end = 20.dp)
                        ) {
                            item {
                                // Explicitly using androidx.compose.animation.AnimatedVisibility to avoid ambiguity with ColumnScope.AnimatedVisibility
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = visible,
                                    enter = slideInVertically { 30 } + fadeIn(tween(800, 200))
                                ) {
                                    QuickStatsRow(
                                        levelId = levelId,
                                        onConsonantsClick = onConsonantsClick,
                                        onVowelsClick = onVowelsClick,
                                        onCombinationClick = onCombinationClick,
                                        onQuizClick = onQuizClick,
                                        onHanifiIntroClick = onHanifiIntroClick,
                                        onNumbersClick = onNumbersClick,
                                        onGrammarClick = onGrammarClick,
                                        onWordBuildingClick = onWordBuildingClick,
                                        onVocabularyClick = onVocabularyClick,
                                        onConversationClick = onConversationClick,
                                        accentColor = themeColors[0]
                                    )
                                }
                            }
                            
                            item {
                                Spacer(Modifier.height(36.dp))
                                CurriculumHeader(lessonCount = lessonsList.size, accentColor = themeColors[0])
                            }

                            itemsIndexed(lessonsList) { index, lesson ->
                                DetailedLessonCard(
                                    lesson = lesson, 
                                    index = index, 
                                    total = lessonsList.size, 
                                    onSelected = { id ->
                                        if (levelId == "3") {
                                            when (lesson.title) {
                                                "Vocabulary" -> onVocabularyClick()
                                                "Conversation" -> onConversationClick()
                                                else -> onLessonSelected(id)
                                            }
                                        } else {
                                            onLessonSelected(id)
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

@Composable
fun DetailedLessonCard(lesson: MockLesson, index: Int, total: Int, onSelected: (String) -> Unit) {
    val lessonGradient = getLessonGradient(index)
    
    // Dashed line connector logic
    Box(modifier = Modifier.fillMaxWidth().drawBehind {
        if (index < total - 1) {
            drawLine(
                brush = lessonGradient,
                start = Offset(43.dp.toPx(), 94.dp.toPx()), 
                end = Offset(43.dp.toPx(), 134.dp.toPx()), 
                strokeWidth = 4.dp.toPx(), 
                cap = StrokeCap.Round,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
            )
        }
    }) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).shadow(16.dp, RoundedCornerShape(32.dp), spotColor = lesson.accentColor.copy(alpha = 0.15f)).clickable { onSelected(lesson.id) },
            shape = RoundedCornerShape(32.dp), 
            colors = CardDefaults.cardColors(containerColor = Color.White), 
            border = BorderStroke(2.5.dp, lessonGradient)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(76.dp).clip(RoundedCornerShape(24.dp)).background(lessonGradient).padding(3.dp).clip(RoundedCornerShape(21.dp)).background(Color.White).padding(4.dp).clip(RoundedCornerShape(17.dp)).background(lessonGradient), 
                    contentAlignment = Alignment.Center
                ) { 
                    Icon(imageVector = lesson.icon, contentDescription = null, modifier = Modifier.size(36.dp), tint = Color.White) 
                }
                Spacer(Modifier.width(18.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Surface(color = lesson.accentColor.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)) {
                        Text("PATH STEP ${index + 1}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = lesson.accentColor, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp), letterSpacing = 1.sp)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(lesson.title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
                    Text(lesson.description, fontSize = 14.sp, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                
                IconButton(
                    onClick = { onSelected(lesson.id) },
                    modifier = Modifier.size(48.dp).background(lessonGradient, CircleShape).shadow(8.dp, CircleShape)
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}

private fun getLessonGradient(index: Int): Brush {
    val colors = when (index % 5) {
        0 -> listOf(Color(0xFF6366F1), Color(0xFFA855F7))
        1 -> listOf(Color(0xFFF43F5E), Color(0xFFFB923C))
        2 -> listOf(Color(0xFF10B981), Color(0xFF34D399))
        3 -> listOf(Color(0xFF3B82F6), Color(0xFF2DD4BF))
        else -> listOf(Color(0xFFF59E0B), Color(0xFFFFD700))
    }
    return Brush.linearGradient(colors)
}

@Composable
fun DynamicMeshHero() {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val animValue by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse),
        label = "anim"
    )
    Box(modifier = Modifier.fillMaxWidth().height(310.dp)) {
        Box(modifier = Modifier.size(500.dp).align(Alignment.TopEnd).offset(x = 200.dp, y = (-150).dp + (animValue * 50).dp).background(Color.White.copy(alpha = 0.1f), CircleShape))
    }
}

@Composable
fun LevelDetailHero(levelId: String, levelTag: String, levelTitle: String, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 24.dp, vertical = 16.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack, modifier = Modifier.size(48.dp).background(Color.White.copy(alpha = 0.18f), CircleShape).border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White, modifier = Modifier.size(26.dp))
                }
                
                // Polished Circular Progress Indicator
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(92.dp).background(brush = Brush.radialGradient(colors = listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.05f))), shape = CircleShape).border(1.5.dp, Color.White.copy(alpha = 0.3f), CircleShape).shadow(8.dp, CircleShape, clip = false, spotColor = Color.White.copy(alpha = 0.25f))
                ) {
                    CircularProgressIndicator(progress = { 0.42f }, modifier = Modifier.size(80.dp), color = Color(0xFF4ADE80), strokeWidth = 6.dp, trackColor = Color.White.copy(alpha = 0.2f), strokeCap = StrokeCap.Round)
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        Text(text = "42%", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = (-0.5).sp)
                        Text(text = "DONE", color = Color.White.copy(alpha = 0.7f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(color = Color(0xFFFFD700).copy(alpha = 0.25f), shape = RoundedCornerShape(8.dp)) {
                        Text(text = levelTag.uppercase(), color = Color(0xFFFFD700), fontSize = 10.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), letterSpacing = 1.2.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(text = levelTitle, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black, letterSpacing = (-1.5).sp)
                    Text(text = "Master foundations with ease", color = Color.White.copy(alpha = 0.8f), fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
                
                Box(modifier = Modifier.size(110.dp).background(Color.White.copy(alpha = 0.15f), CircleShape).padding(6.dp).clip(CircleShape)) {
                    Image(painter = painterResource(id = illustrationRes(levelId)), null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
            }
        }
    }
}

@Composable
fun QuickStatsRow(
    levelId: String, onConsonantsClick: () -> Unit, onVowelsClick: () -> Unit, onCombinationClick: () -> Unit,
    onQuizClick: (String) -> Unit, onHanifiIntroClick: () -> Unit, onNumbersClick: () -> Unit,
    onGrammarClick: () -> Unit, onWordBuildingClick: () -> Unit, onVocabularyClick: () -> Unit,
    onConversationClick: () -> Unit, accentColor: Color
) {
    Column {
        Text("Learning Pillars", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            when (levelId) {
                "1" -> {
                    FeatureEliteCard("Consonants", "28 Letters", Icons.Rounded.GridView, Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFF4F46E5))), onConsonantsClick)
                    FeatureEliteCard("Vowels", "10 Signs", Icons.Rounded.Category, Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFD946EF))), onVowelsClick)
                    FeatureEliteCard("Combining", "Flow Rules", Icons.Rounded.AutoAwesome, Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED))), onCombinationClick)
                    FeatureEliteCard("Numbers", "0-9 Digits", Icons.Rounded.Pin, Brush.linearGradient(listOf(Color(0xFF6D28D9), Color(0xFF5B21B6))), onNumbersClick)
                    FeatureEliteCard("Basics", "Hanifi Intro", Icons.Rounded.AutoStories, Brush.linearGradient(listOf(Color(0xFF0EA5E9), Color(0xFF2563EB))), onHanifiIntroClick)
                }
                "2" -> {
                    FeatureEliteCard("Writing", "Practice", Icons.Rounded.Create, Brush.linearGradient(listOf(Color(0xFF0F766E), Color(0xFF0D9488))), onConsonantsClick)
                    FeatureEliteCard("Grammar", "Basic Rules", Icons.Rounded.Class, Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFD946EF))), onGrammarClick)
                    FeatureEliteCard("Words", "Building", Icons.Rounded.Category, Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669))), onWordBuildingClick)
                    FeatureEliteCard("Practice", "Interactive Quiz", Icons.Rounded.Build, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))) { onQuizClick(levelId) }
                }
                "3" -> {
                    FeatureEliteCard("Grammar", "Advanced", Icons.Rounded.School, Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF047857))), onGrammarClick)
                    FeatureEliteCard("Vocabulary", "Word Bank", Icons.AutoMirrored.Rounded.MenuBook, Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))), onVocabularyClick)
                    FeatureEliteCard("Conversation", "Speaking", Icons.Rounded.Forum, Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706))), onConversationClick)
                    FeatureEliteCard("Practice", "Interactive Quiz", Icons.Rounded.Build, Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFFD946EF)))) { onQuizClick(levelId) }
                }
            }
        }
    }
}

@Composable
fun FeatureEliteCard(title: String, subtitle: String, icon: ImageVector, gradient: Brush, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).height(195.dp).shadow(24.dp, RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.12f)).clickable(onClick = onClick),
        shape = RoundedCornerShape(32.dp), 
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(3.dp, gradient)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(modifier = Modifier.size(56.dp).background(gradient, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.weight(1f))
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
            Text(subtitle, fontSize = 13.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CurriculumHeader(lessonCount: Int, accentColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text("Roadmap Path", fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F172A))
            Text("Master foundations step-by-step", fontSize = 13.sp, color = Color(0xFF64748B))
        }
        Surface(color = accentColor.copy(alpha = 0.12f), shape = RoundedCornerShape(12.dp)) {
            Text("$lessonCount Lessons", color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
        }
    }
}

private fun illustrationRes(levelId: String): Int = when (levelId) {
    "1" -> R.drawable.beginner
    "2" -> R.drawable.intermediate
    "3" -> R.drawable.advanced
    else -> R.drawable.beginner
}

data class MockLesson(val id: String, val title: String, val description: String, val icon: ImageVector, val accentColor: Color)

val mockBeginnerLessons = listOf(
    MockLesson("1", "Script Intro", "Visual overview of system.", Icons.Rounded.AutoStories, Color(0xFF6366F1)),
    MockLesson("2", "Consonants", "Learn 28 characters.", Icons.Rounded.GridView, Color(0xFFF43F5E)),
    MockLesson("3", "Vowels", "Sign usage & sounds.", Icons.Rounded.Category, Color(0xFF10B981)),
    MockLesson("4", "Combining", "Flow and Script Rules.", Icons.Rounded.AutoAwesome, Color(0xFF3B82F6)),
    MockLesson("5", "Numbers", "Hanifi numeral system.", Icons.Rounded.Pin, Color(0xFFF59E0B))
)

val mockIntermediateLessons = listOf(
    MockLesson("1", "Writing Practice", "Master script writing.", Icons.Rounded.Create, Color(0xFF0F766E)),
    MockLesson("2", "Grammar Rules", "Foundation of sentences.", Icons.Rounded.Class, Color(0xFFEC4899)),
    MockLesson("3", "Word Building", "Construct complex words.", Icons.Rounded.Extension, Color(0xFF8B5CF6))
)

val mockAdvancedLessons = listOf(
    MockLesson("1", "Advanced Grammar", "Complex syntax and nuances.", Icons.Rounded.School, Color(0xFF059669)),
    MockLesson("2", "Vocabulary", "Expand your word bank.", Icons.AutoMirrored.Rounded.MenuBook, Color(0xFF3B82F6)),
    MockLesson("3", "Conversation", "Real-life speaking scenarios.", Icons.Rounded.Forum, Color(0xFFF59E0B))
)
