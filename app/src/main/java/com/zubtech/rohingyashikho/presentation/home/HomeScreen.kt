package com.zubtech.rohingyashikho.presentation.home

import android.content.Intent
import android.net.Uri
import android.os.Build
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
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.domain.model.AppUpdateInfo
import com.zubtech.rohingyashikho.presentation.settings.LanguageStrings
import com.zubtech.rohingyashikho.presentation.admin.RichTextUtil

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
    val context = LocalContext.current
    
    var showNotificationBubble by remember { mutableStateOf(false) }

    val currentVersion = remember(context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, 0).longVersionCode
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0).versionCode.toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }

    val isUpdateAvailable = uiState.appUpdateInfo.version > currentVersion
    val hasInteracted = uiState.appUpdateInfo.version <= uiState.lastInteractedNotificationVersion
    val hasSeen = uiState.appUpdateInfo.version <= uiState.lastSeenNotificationVersion

    // Auto-show bubble ONCE per version if not seen yet
    LaunchedEffect(isUpdateAvailable, uiState.appUpdateInfo.version, uiState.lastSeenNotificationVersion) {
        if (isUpdateAvailable && uiState.appUpdateInfo.showNotification && !hasSeen) {
            showNotificationBubble = true
            viewModel.markNotificationAsSeen(uiState.appUpdateInfo.version)
        }
    }

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

                // FLOATING Dynamic Notification Bubble
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = padding.calculateTopPadding() + 92.dp)
                        .zIndex(20f)
                ) {
                    AnimatedVisibility(
                        visible = (showNotificationBubble || uiState.isUpdating) && isUpdateAvailable && !hasInteracted,
                        enter = fadeIn(tween(400)) + scaleIn(
                            initialScale = 0.1f,
                            transformOrigin = TransformOrigin(0.92f, 0f),
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        ),
                        exit = fadeOut(tween(300)) + scaleOut(
                            targetScale = 0.5f,
                            transformOrigin = TransformOrigin(0.92f, 0f)
                        )
                    ) {
                        NotificationUpdateBubble(
                            info = uiState.appUpdateInfo,
                            isUpdating = uiState.isUpdating,
                            progress = uiState.updateProgress,
                            onUpdateClick = {
                                viewModel.startInAppUpdate()
                            },
                            onDismissClick = { showNotificationBubble = false }
                        )
                    }
                }

                // Header with Notification Icon
                HomeTopBar(
                    userName = uiState.userName.ifBlank { "User" },
                    lang = lang,
                    showBadge = isUpdateAvailable && !hasInteracted,
                    onNotificationClick = {
                        if (isUpdateAvailable) {
                            showNotificationBubble = !showNotificationBubble
                        }
                    },
                    modifier = Modifier.align(Alignment.TopCenter).zIndex(21f)
                )
            }
        }
    }
}

class SpeechBubbleShape(
    private val cornerRadius: Dp = 24.dp,
    private val tipSize: Dp = 16.dp,
    private val tipOffset: Dp = 46.dp 
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val tipSizePx = with(density) { tipSize.toPx() }
        val tipOffsetPx = with(density) { tipOffset.toPx() }
        val radiusPx = with(density) { cornerRadius.toPx() }

        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(0f, tipSizePx, size.width, size.height),
                    topLeft = CornerRadius(radiusPx),
                    topRight = CornerRadius(radiusPx),
                    bottomRight = CornerRadius(radiusPx),
                    bottomLeft = CornerRadius(radiusPx)
                )
            )
            moveTo(size.width - tipOffsetPx - (tipSizePx / 1.1f), tipSizePx)
            lineTo(size.width - tipOffsetPx, 0f)
            lineTo(size.width - tipOffsetPx + (tipSizePx / 1.1f), tipSizePx)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun NotificationUpdateBubble(
    info: AppUpdateInfo,
    isUpdating: Boolean,
    progress: Float,
    onUpdateClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    val themeColor = if (info.highlightColor == 0L) 0xFF4F46E5 else info.highlightColor
    val textColor = if (info.textColor == 0L) 0xFF1E293B else info.textColor

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 32.dp, 
                    shape = SpeechBubbleShape(), 
                    spotColor = Color(themeColor).copy(alpha = 0.5f)
                ),
            color = Color.White,
            shape = SpeechBubbleShape(),
            border = BorderStroke(2.dp, Color(themeColor).copy(alpha = 0.8f))
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    Icons.Rounded.RocketLaunch,
                    contentDescription = null,
                    tint = Color(themeColor).copy(alpha = 0.05f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(110.dp)
                )

                Column(
                    modifier = Modifier
                        .padding(top = 36.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
                ) {
                    Text(
                        text = if (isUpdating) "Updating..." else "New Message",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color(textColor)
                    )
                    
                    Spacer(Modifier.height(14.dp))
                    
                    if (isUpdating) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Downloading updates, please wait...",
                                color = Color(textColor).copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(12.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape),
                                color = Color(themeColor),
                                trackColor = Color(themeColor).copy(alpha = 0.1f),
                                strokeCap = StrokeCap.Round
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                modifier = Modifier.align(Alignment.End),
                                color = Color(themeColor),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Text(
                            text = RichTextUtil.fromHtml(info.message),
                            color = Color(textColor).copy(alpha = 0.85f),
                            fontSize = info.fontSize.sp,
                            fontWeight = if (info.isBold) FontWeight.Bold else FontWeight.Medium,
                            fontStyle = if (info.isItalic) FontStyle.Italic else FontStyle.Normal,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(Modifier.height(30.dp))

                    if (!isUpdating) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = onDismissClick,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(2.dp, Color(themeColor).copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(textColor).copy(alpha = 0.7f))
                            ) {
                                Text("Later", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            
                            Button(
                                onClick = onUpdateClick,
                                modifier = Modifier.weight(1f).height(50.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(themeColor),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 10.dp)
                            ) {
                                Text("Action", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(
    userName: String,
    lang: String,
    showBadge: Boolean,
    onNotificationClick: () -> Unit,
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
                modifier = Modifier.size(44.dp).clickable { onNotificationClick() },
                shape = CircleShape,
                color = if (showBadge) Color(0xFFEEF2FF) else Color(0xFFF1F5F9)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (showBadge) Icons.Rounded.NotificationsActive else Icons.Rounded.NotificationsNone,
                        contentDescription = null,
                        tint = if (showBadge) Color(0xFF4F46E5) else Color(0xFF1E293B),
                        modifier = Modifier.size(24.dp)
                    )
                    if (showBadge) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 10.dp, end = 10.dp)
                                .size(10.dp)
                                .background(Color.Red, CircleShape)
                                .border(1.5.dp, Color.White, CircleShape)
                        )
                    }
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
