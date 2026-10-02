package com.zubtech.rohingyashikho.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.presentation.settings.LanguageStrings

@Composable
fun HomeScreen(
    onLessonSelected: (String) -> Unit,
    onAlphabetClick: () -> Unit,
    onStartQuiz: (String) -> Unit,
    onStartReview: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lang = uiState.appLanguage
    
    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6366F1))
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = padding.calculateTopPadding() + 110.dp,
                        bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ContinueLearningCard(
                            progress = 0.69f,
                            lang = lang,
                            onContinue = { /* Continue Action */ }
                        )
                    }

                    item {
                        ScriptLearningCard(
                            progress = 0.34f,
                            streak = uiState.streak,
                            lang = lang
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                                    spotColor = Color(0xFF6366F1).copy(alpha = 0.08f)
                                )
                                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                                .background(Color.White)
                                .padding(top = 26.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            LearningPathHeader(lang = lang, onGoalClick = {})
                            
                            Spacer(modifier = Modifier.height(2.dp))

                            LevelCard(
                                level = 1,
                                title = LanguageStrings.getText("beginner", lang),
                                sections = 5,
                                items = listOf(
                                    LanguageStrings.getText("script_intro", lang),
                                    LanguageStrings.getText("consonants", lang),
                                    LanguageStrings.getText("vowels", lang),
                                    LanguageStrings.getText("combining", lang),
                                    LanguageStrings.getText("numbers", lang)
                                ),
                                imageRes = R.drawable.beginner,
                                accentColor = Color(0xFF0072FF),
                                lang = lang,
                                onClick = { onLessonSelected("1") }
                            )

                            LevelCard(
                                level = 2,
                                title = LanguageStrings.getText("intermediate", lang),
                                sections = 3,
                                items = listOf("Writing Practice", "Basic Grammer", "Word Building"),
                                imageRes = R.drawable.intermediate,
                                accentColor = Color(0xFFA855F7),
                                lang = lang,
                                onClick = { onLessonSelected("2") }
                            )

                            LevelCard(
                                level = 3,
                                title = LanguageStrings.getText("advanced", lang),
                                sections = 3,
                                items = listOf("Advance Grammer", "Vocabulary", "Conversation"),
                                imageRes = R.drawable.advanced,
                                accentColor = Color(0xFF10B981),
                                lang = lang,
                                onClick = { onLessonSelected("3") }
                            )
                        }
                    }
                }

                HomeTopBar(
                    userName = uiState.userName.ifBlank { "User" },
                    lang = lang,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
fun HomeTopBar(
    userName: String,
    lang: String,
    modifier: Modifier = Modifier
) {
    val shiningHeaderBorder = Brush.linearGradient(
        colors = listOf(
            Color(0xFF6366F1),
            Color(0xFFEC4899),
            Color(0xFFF59E0B),
            Color(0xFF10B981),
            Color(0xFF6366F1)
        )
    )

    val titleGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF6366F1),
            Color(0xFFA855F7),
            Color(0xFFEC4899)
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 15.dp,
                shape = RoundedCornerShape(bottomStart = 38.dp, bottomEnd = 38.dp),
                spotColor = Color(0xFF6366F1).copy(alpha = 0.25f)
            )
            .border(
                width = 2.5.dp,
                brush = shiningHeaderBorder,
                shape = RoundedCornerShape(bottomStart = 38.dp, bottomEnd = 38.dp)
            ),
        color = Color.White,
        shape = RoundedCornerShape(bottomStart = 38.dp, bottomEnd = 38.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Rohingya Shikho",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        brush = titleGradient,
                        letterSpacing = (-0.5).sp
                    )
                )
                Text(
                    text = "${LanguageStrings.getText("welcome_back", lang).replace("!", "").trim()}! $userName",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = Color(0xFFF1F5F9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Rounded.NotificationsNone,
                        contentDescription = null,
                        tint = Color(0xFF1E293B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ContinueLearningCard(progress: Float, lang: String, onContinue: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "graphic_animation")
    val floatTranslation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatTranslation"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .shadow(25.dp, RoundedCornerShape(32.dp), spotColor = Color(0xFF0072FF).copy(alpha = 0.3f))
                .clickable { onContinue() },
            shape = RoundedCornerShape(32.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF00C6FF), Color(0xFF0072FF)))))
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(top = 20.dp, end = 24.dp)) {
                    CircularProgressIndicator(
                        progress = { progress },
                        color = Color(0xFFFFD700),
                        trackColor = Color.White.copy(alpha = 0.2f),
                        strokeWidth = 7.dp,
                        modifier = Modifier.size(64.dp),
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.graphic1),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 80.dp, bottom = 12.dp)
                        .size(110.dp)
                        .graphicsLayer {
                            translationY = floatTranslation
                            scaleX = pulseScale
                            scaleY = pulseScale
                        },
                    contentScale = ContentScale.Fit
                )
                Column(modifier = Modifier.padding(24.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = Color.White.copy(alpha = 0.2f), shape = CircleShape) {
                            Icon(Icons.Rounded.Explore, null, tint = Color.White, modifier = Modifier.padding(6.dp).size(14.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(LanguageStrings.getText("continue_learning", lang), color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = LanguageStrings.getText("rohingya_language", lang).replace(" ", "\n"), 
                        color = Color.White, 
                        fontSize = 28.sp, 
                        fontWeight = FontWeight.Black, 
                        modifier = Modifier.width(220.dp),
                        lineHeight = 34.sp
                    )
                    Surface(color = Color.White.copy(alpha = 0.25f), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Rounded.MenuBook, null, tint = Color(0xFFFFFFFF), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(LanguageStrings.getText("lessons_count", lang), color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScriptLearningCard(progress: Float, streak: Int, lang: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth().shadow(15.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.03f)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF007AFF).copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp), modifier = Modifier.size(52.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.MenuBook, null, tint = Color(0xFF007AFF), modifier = Modifier.padding(14.dp))
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(LanguageStrings.getText("hanifi_script", lang), fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1E293B))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.LocalFireDepartment, null, tint = Color(0xFFFF5722), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("$streak ${LanguageStrings.getText("days", lang)}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF1E293B))
                        }
                    }
                    Text(LanguageStrings.getText("overall_progress", lang), color = Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
                            color = Color(0xFF6366F1),
                            trackColor = Color(0xFFF1F5F9),
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("${(progress * 100).toInt()}%", color = Color(0xFF1E293B), fontWeight = FontWeight.Black, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun LearningPathHeader(lang: String, onGoalClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(color = Color(0xFF6366F1).copy(alpha = 0.1f), shape = CircleShape, modifier = Modifier.size(40.dp)) {
            Icon(Icons.Rounded.Explore, null, tint = Color(0xFF6366F1), modifier = Modifier.padding(10.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(LanguageStrings.getText("learning_path", lang), fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF1E293B))
            Text(LanguageStrings.getText("foundation_mastery", lang), color = Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
        Surface(
            onClick = onGoalClick,
            color = Color.White,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 4.dp,
            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.EmojiEvents, null, tint = Color(0xFF00ACC1), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(LanguageStrings.getText("goal", lang), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
            }
        }
    }
}

@Composable
fun LevelCard(
    level: Int,
    title: String,
    sections: Int,
    items: List<String>,
    imageRes: Int,
    accentColor: Color,
    lang: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animateScale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "scale")
    val arrowShadowElevation by animateDpAsState(targetValue = if (isPressed) 4.dp else 12.dp, label = "arrowShadow")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(animateScale)
            .shadow(10.dp, RoundedCornerShape(24.dp), spotColor = accentColor.copy(alpha = 0.15f))
            .border(2.5.dp, accentColor.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Beauty: Increased Big size icon surface with soft background
            Surface(
                color = accentColor.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.size(120.dp),
                border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.12f))
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1.1f)) {
                Surface(color = accentColor, shape = RoundedCornerShape(6.dp)) {
                    Text(
                        text = "${LanguageStrings.getText("level", lang)} $level",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$sections ${LanguageStrings.getText("sections", lang)}",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                shadowElevation = arrowShadowElevation
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
