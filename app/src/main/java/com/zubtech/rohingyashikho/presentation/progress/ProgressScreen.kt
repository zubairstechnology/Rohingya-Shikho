package com.zubtech.rohingyashikho.presentation.progress

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.*
import com.zubtech.rohingyashikho.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit,
    onStartReview: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Learning Journey",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.padding(8.dp).background(Color.White, CircleShape).shadow(2.dp, CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B))
                    }
                },
                actions = {
                    IconButton(
                        onClick = onSettingsClick,
                        modifier = Modifier.padding(8.dp).background(Color.White, CircleShape).shadow(2.dp, CircleShape)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color(0xFF6366F1))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Creative Background Blobs
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6366F1).copy(alpha = 0.05f), Color.Transparent),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(size.width * 0.9f, size.height * 0.1f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFEC4899).copy(alpha = 0.04f), Color.Transparent),
                        radius = size.width * 0.7f
                    ),
                    center = Offset(size.width * 0.1f, size.height * 0.5f)
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF6366F1))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    item {
                        ProfileHeaderSection(
                            userName = uiState.userName,
                            totalPoints = uiState.totalPoints,
                            mastered = uiState.masteredItems,
                            total = uiState.totalItems
                        )
                    }

                    item {
                        QuickStatsGrid(
                            streak = uiState.streak,
                            xp = uiState.totalPoints
                        )
                    }

                    item {
                        DailyActionCard(onAction = onStartReview)
                    }

                    item {
                        SectionHeader(title = "Knowledge Map")
                    }

                    items(uiState.levelProgress.toList()) { (levelId, progress) ->
                        ModernLevelCard(levelId = levelId, progress = progress)
                    }

                    item {
                        SectionHeader(title = "Recent Achievements")
                        AchievementScroll()
                    }

                    item {
                        LogoutButton(onLogout = { viewModel.logout(onLogout) })
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeaderSection(userName: String, totalPoints: Int, mastered: Int, total: Int) {
    val progress = if (total > 0) mastered.toFloat() / total else 0f
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.student_animation))

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = Color(0xFF6366F1).copy(alpha = 0.2f)),
        shape = RoundedCornerShape(32.dp),
        color = Color.White
    ) {
        Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            // Accent background gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.35f)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF6366F1).copy(alpha = 0.08f), Color.Transparent)
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with interactive-looking ring
                Box(contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.size(90.dp)) {
                        drawCircle(
                            color = Color(0xFFE2E8F0),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                        )
                        drawArc(
                            color = Color(0xFF6366F1),
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 4.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.name),
                        contentDescription = null,
                        modifier = Modifier
                            .size(74.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userName,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Expert Explorer",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFEEF2FF),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$totalPoints XP",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF6366F1)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${(progress * 100).toInt()}% Course",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }
                
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.size(60.dp).graphicsLayer(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun QuickStatsGrid(streak: Int, xp: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Streak",
            value = "$streak Days",
            icon = Icons.Rounded.Bolt,
            color = Color(0xFFF59E0B)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Mastery",
            value = "${xp / 10} Pts",
            icon = Icons.Rounded.Verified,
            color = Color(0xFF10B981)
        )
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = modifier.shadow(12.dp, RoundedCornerShape(24.dp), spotColor = color.copy(alpha = 0.2f)),
        color = Color.White,
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            Text(text = label, fontSize = 12.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DailyActionCard(onAction: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "scale")

    Surface(
        onClick = onAction,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(16.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF6366F1).copy(alpha = 0.3f)),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF6366F1)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ready to practice?",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Smart Review",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Icon(
                Icons.Rounded.PlayCircle,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        color = Color(0xFF1E293B)
    )
}

@Composable
fun ModernLevelCard(levelId: String, progress: Float) {
    val cleanName = levelId.replace("lvl_", "").replaceFirstChar { it.uppercase() }
    
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(36.dp).background(Color(0xFFF1F5F9), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.MenuBook, null, tint = Color(0xFF6366F1), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Level $cleanName",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF6366F1)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = Color(0xFF6366F1),
                trackColor = Color(0xFFF1F5F9)
            )
        }
    }
}

@Composable
fun AchievementScroll() {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(5) { i ->
            BadgeItem(i)
        }
    }
}

@Composable
fun BadgeItem(i: Int) {
    val colors = listOf(Color(0xFF6366F1), Color(0xFFEC4899), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF8B5CF6))
    val icons = listOf(Icons.Rounded.Star, Icons.Rounded.Favorite, Icons.Rounded.School, Icons.Rounded.WorkspacePremium, Icons.Rounded.EmojiEvents)
    
    Surface(
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        color = colors[i % colors.size].copy(alpha = 0.1f),
        border = BorderStroke(2.dp, colors[i % colors.size])
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icons[i % icons.size],
                contentDescription = null,
                tint = colors[i % colors.size],
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun LogoutButton(onLogout: () -> Unit) {
    Button(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth().height(56.dp).shadow(8.dp, RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(0xFFEF4444)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFFEE2E2))
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, null)
        Spacer(modifier = Modifier.width(12.dp))
        Text("Logout Account", fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
