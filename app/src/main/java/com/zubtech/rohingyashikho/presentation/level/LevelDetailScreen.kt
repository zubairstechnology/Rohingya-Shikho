package com.zubtech.rohingyashikho.presentation.level

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zubtech.rohingyashikho.R

@Composable
fun LevelDetailScreen(
    levelId: String,
    onNavigateBack: () -> Unit,
    onLessonSelected: (String) -> Unit,
    onConsonantsClick: () -> Unit,
    onVowelsClick: () -> Unit,
    onCombinationClick: () -> Unit,
    onQuizClick: (String) -> Unit
) {
    // Determine configuration based on levelId
    val containerBgColor = when (levelId) {
        "2" -> Color(0xFFF3E8FF) // Intermediate purple background
        "3" -> Color(0xFFDCFCE7) // Advanced green background
        else -> Color(0xFFE0F2FE) // Basic blue background
    }

    val gradientColors = when (levelId) {
        "2" -> listOf(Color(0xFF6B21A8), Color(0xFF9333EA), Color(0xFFC084FC))
        "3" -> listOf(Color(0xFF166534), Color(0xFF16A34A), Color(0xFF4ADE80))
        else -> listOf(Color(0xFF1E40AF), Color(0xFF3B82F6), Color(0xFF60A5FA))
    }

    val levelTitle = when (levelId) {
        "2" -> "Intermediate Level"
        "3" -> "Advanced Level"
        else -> "Basic Level"
    }

    val levelSubtitle = when (levelId) {
        "2" -> "Build words & speak"
        "3" -> "Speak confidently"
        else -> "Rohingya Hanifi Script"
    }

    val levelDescription = when (levelId) {
        "2" -> "Learn vowel signs, word building and conversation"
        "3" -> "Master complex grammar, vocabulary and real-life topics"
        else -> "Build your foundation step by step"
    }

    val levelTag = when (levelId) {
        "2" -> "Fluency"
        "3" -> "Mastery"
        else -> "Foundation"
    }

    val illustrationRes = when (levelId) {
        "2" -> R.drawable.graphic2
        "3" -> R.drawable.every_day
        else -> R.drawable.graphic1
    }

    val lessonsList = when (levelId) {
        "2" -> mockIntermediateLessons
        "3" -> mockAdvancedLessons
        else -> mockLessons
    }

    Scaffold(
        containerColor = containerBgColor
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Background Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
                    .background(Brush.verticalGradient(colors = gradientColors))
            )

            Column(modifier = Modifier.fillMaxSize()) {
                // Hero Content - Responsive Header
                LevelDetailHero(
                    levelTag = levelTag,
                    levelTitle = levelTitle,
                    levelSubtitle = levelSubtitle,
                    levelDescription = levelDescription,
                    illustrationRes = illustrationRes,
                    onBack = onNavigateBack
                )

                // Bottom Content Sheet
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                    color = Color.White
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item {
                            QuickStatsRow(
                                levelId = levelId,
                                onConsonantsClick = onConsonantsClick,
                                onVowelsClick = onVowelsClick,
                                onCombinationClick = onCombinationClick,
                                onQuizClick = onQuizClick
                            )
                        }

                        items(lessonsList) { lesson ->
                            LessonItemCard(lesson) { id ->
                                when (lesson.category) {
                                    "Consonant" -> onConsonantsClick()
                                    "Vowel" -> onVowelsClick()
                                    "Combination" -> onCombinationClick()
                                    else -> onLessonSelected(id)
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
fun LevelDetailHero(
    levelTag: String,
    levelTitle: String,
    levelSubtitle: String,
    levelDescription: String,
    illustrationRes: Int,
    onBack: () -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        val screenWidth = maxWidth
        val isSmallScreen = screenWidth < 360.dp
        val imageSize = if (isSmallScreen) 130.dp else 170.dp

        Column(modifier = Modifier.fillMaxWidth(if (isSmallScreen) 1f else 0.65f)) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
            
            Spacer(Modifier.height(16.dp))
            
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = CircleShape
            ) {
                Text(
                    levelTag,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                levelTitle,
                color = Color(0xFFFFD700),
                fontSize = if (isSmallScreen) 26.sp else 34.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                levelSubtitle,
                color = Color.White,
                fontSize = if (isSmallScreen) 18.sp else 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                levelDescription,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { 0.2f },
                    modifier = Modifier.width(if (isSmallScreen) 120.dp else 160.dp).height(10.dp).clip(CircleShape),
                    color = Color(0xFF4ADE80),
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    "Available Lessons",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        // Illustration
        Image(
            painter = painterResource(id = illustrationRes),
            contentDescription = null,
            modifier = Modifier
                .size(imageSize)
                .align(Alignment.BottomEnd)
                .offset(y = 10.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun QuickStatsRow(
    levelId: String,
    onConsonantsClick: () -> Unit,
    onVowelsClick: () -> Unit,
    onCombinationClick: () -> Unit,
    onQuizClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val cardModifier = Modifier.width(115.dp)
        
        if (levelId == "2") {
            StatCard(
                title = "Vowel Signs\n& Rules",
                count = "10 Marks",
                icon = Icons.Rounded.Book,
                bgColor = Color(0xFFF3E8FF),
                accentColor = Color(0xFF9333EA),
                modifier = cardModifier,
                onClick = { onQuizClick("vowel_signs") }
            )
            StatCard(
                title = "Word\nBuilding",
                count = "Daily Practice",
                icon = Icons.Rounded.Build,
                bgColor = Color(0xFFEFF6FF),
                accentColor = Color(0xFF2563EB),
                modifier = cardModifier,
                onClick = { onQuizClick("word_building") }
            )
            StatCard(
                title = "Common\nConversations",
                count = "Audio Included",
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                bgColor = Color(0xFFF0FDF4),
                accentColor = Color(0xFF16A34A),
                modifier = cardModifier,
                onClick = { onQuizClick("conversation") }
            )
        } else if (levelId == "3") {
            StatCard(
                title = "Complex\nGrammar",
                count = "Advanced Rules",
                icon = Icons.Rounded.Gavel,
                bgColor = Color(0xFFDCFCE7),
                accentColor = Color(0xFF16A34A),
                modifier = cardModifier,
                onClick = { onQuizClick("grammar") }
            )
            StatCard(
                title = "Rich\nVocabulary",
                count = "500+ Words",
                icon = Icons.Rounded.Translate,
                bgColor = Color(0xFFFEF3C7),
                accentColor = Color(0xFFD97706),
                modifier = cardModifier,
                onClick = { onQuizClick("vocabulary") }
            )
            StatCard(
                title = "Literature\n& History",
                count = "Advanced Reading",
                icon = Icons.Rounded.HistoryEdu,
                bgColor = Color(0xFFECFDF5),
                accentColor = Color(0xFF059669),
                modifier = cardModifier,
                onClick = { onQuizClick("literature") }
            )
        } else {
            StatCard(
                title = "Hanifi Script\nIntroduction",
                count = "Info Hanifi Script",
                icon = Icons.Rounded.TextFormat,
                bgColor = Color(0xFFEFF6FF),
                accentColor = Color(0xFF3B82F6),
                modifier = cardModifier,
                onClick = { /* Could navigate to intro if needed */ }
            )
            StatCard(
                title = "28\nConsonants",
                count = "Included Quiz",
                icon = Icons.Rounded.GridView,
                bgColor = Color(0xFFFAF5FF),
                accentColor = Color(0xFF8B5CF6),
                modifier = cardModifier,
                onClick = onConsonantsClick
            )
            StatCard(
                title = "10\nVowels",
                count = "Included Quiz",
                icon = Icons.Rounded.Category,
                bgColor = Color(0xFFF0FDF4),
                accentColor = Color(0xFF22C55E),
                modifier = cardModifier,
                onClick = onVowelsClick
            )
            StatCard(
                title = "Combined\nLetters",
                count = "combination",
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                bgColor = Color(0xFFFFF7ED),
                accentColor = Color(0xFFF97316),
                modifier = cardModifier,
                onClick = onCombinationClick
            )
            StatCard(
                title = "Number\nWith song",
                count = "Number",
                icon = Icons.AutoMirrored.Rounded.VolumeUp,
                bgColor = Color(0xFFFFF7ED),
                accentColor = Color(0xFFF97316),
                modifier = cardModifier,
                onClick = { onQuizClick("numbers") }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    count: String,
    icon: ImageVector,
    bgColor: Color,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(130.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = accentColor
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.padding(8.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(
                title,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                color = Color(0xFF1E293B)
            )
            Spacer(Modifier.height(4.dp))
            Text(count, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = accentColor)
        }
    }
}

@Composable
fun LessonItemCard(lesson: MockLesson, onSelected: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .shadow(2.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = lesson.cardBg)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(lesson.symbol, fontSize = 32.sp, fontWeight = FontWeight.Black, color = lesson.accentColor)
                    }
                }
                Surface(
                    modifier = Modifier.size(24.dp).align(Alignment.TopStart).offset(x = (-6).dp, y = (-6).dp),
                    shape = CircleShape,
                    color = Color(0xFF6366F1)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(lesson.id, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFF3B82F6).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Lesson ${lesson.id}",
                        color = Color(0xFF3B82F6),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    lesson.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B)
                )
                Text(
                    lesson.description,
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Spacer(Modifier.width(8.dp))
            
            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = { onSelected(lesson.id) },
                    colors = ButtonDefaults.buttonColors(containerColor = lesson.btnColor),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(lesson.btnText, fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
                
                Spacer(Modifier.height(10.dp))
                
                LinearProgressIndicator(
                    progress = { lesson.progress },
                    modifier = Modifier.width(50.dp).height(6.dp).clip(CircleShape),
                    color = Color(0xFF4ADE80),
                    trackColor = Color.Gray.copy(alpha = 0.2f)
                )
            }
        }
    }
}

data class MockLesson(
    val id: String,
    val title: String,
    val description: String,
    val symbol: String,
    val meta1: String,
    val meta2: String,
    val progress: Float,
    val completed: Int,
    val total: Int,
    val category: String, // "General", "Consonant", "Vowel"
    val btnText: String = "Start",
    val btnColor: Color = Color(0xFF3B82F6),
    val cardBg: Color = Color(0xFFEFF6FF),
    val accentColor: Color = Color(0xFF3B82F6)
)

val mockLessons = listOf(
    MockLesson(
        "1",
        "Introduction",
        "Overview of Rohingya Hanifi script.",
        "أ",
        "1 Video",
        "5 min",
        0.5f,
        1,
        2,
        category = "General",
        cardBg = Color(0xFFF0F9FF),
        accentColor = Color(0xFF0EA5E9)
    ),
    MockLesson(
        "2",
        "Consonants",
        "Learn basic shapes and sounds of 28 consonants.",
        "ب",
        "28 Letters",
        "20 min",
        0.2f,
        1,
        28,
        category = "Consonant",
        cardBg = Color(0xFFF5F3FF),
        accentColor = Color(0xFF8B5CF6)
    ),
    MockLesson(
        "3",
        "Vowels",
        "Learn the 10 vowels and how they modify sounds.",
        "د",
        "10 Letters",
        "15 min",
        0f,
        0,
        10,
        category = "Vowel",
        cardBg = Color(0xFFFDF2F8),
        accentColor = Color(0xFFEC4899)
    ),
    MockLesson(
        "4",
        "Combined Script",
        "Practice combining consonants and vowels.",
        "𐴌𐴗",
        "Exercises",
        "15 min",
        0f,
        0,
        15,
        category = "Combination",
        cardBg = Color(0xFFF0FDF4),
        accentColor = Color(0xFF10B981)
    ),
    MockLesson(
        "5",
        "Numbers & Counting",
        "Practice Rohingya numbers and songs.",
        "𐴰",
        "Exercises",
        "15 min",
        0f,
        0,
        15,
        category = "Number",
        cardBg = Color(0xFFFFF7ED),
        accentColor = Color(0xFFF97316)
    )
)

val mockIntermediateLessons = listOf(
    MockLesson(
        "1",
        "Vowel Signs (Sukun)",
        "Understand how Sukun alters consonant combinations.",
        "𐴿",
        "Practice",
        "10 min",
        0f,
        0,
        1,
        category = "Intermediate",
        cardBg = Color(0xFFF3E8FF),
        accentColor = Color(0xFF9333EA)
    ),
    MockLesson(
        "2",
        "Word Construction",
        "Learn to build simple words using Hanifi letters.",
        "𐴖𐴕",
        "Practice",
        "15 min",
        0f,
        0,
        1,
        category = "Intermediate",
        cardBg = Color(0xFFEFF6FF),
        accentColor = Color(0xFF2563EB)
    ),
    MockLesson(
        "3",
        "Daily Conversation",
        "Useful phrases and greetings in daily conversations.",
        "𐴈𐴢",
        "Practice",
        "20 min",
        0f,
        0,
        1,
        category = "Intermediate",
        cardBg = Color(0xFFF0FDF4),
        accentColor = Color(0xFF16A34A)
    )
)

val mockAdvancedLessons = listOf(
    MockLesson(
        "1",
        "Complex Grammar",
        "Deep dive into Rohingya sentence structure.",
        "𐴞",
        "Practice",
        "20 min",
        0f,
        0,
        1,
        category = "Advanced",
        cardBg = Color(0xFFDCFCE7),
        accentColor = Color(0xFF16A34A)
    ),
    MockLesson(
        "2",
        "Advanced Vocabulary",
        "Expand your word collection for formal use.",
        "𐴛",
        "Practice",
        "25 min",
        0f,
        0,
        1,
        category = "Advanced",
        cardBg = Color(0xFFFEF3C7),
        accentColor = Color(0xFFD97706)
    ),
    MockLesson(
        "3",
        "Literature & History",
        "Formal reading and historical texts in Hanifi script.",
        "𐴟",
        "Practice",
        "30 min",
        0f,
        0,
        1,
        category = "Advanced",
        cardBg = Color(0xFFECFDF5),
        accentColor = Color(0xFF059669)
    )
)
