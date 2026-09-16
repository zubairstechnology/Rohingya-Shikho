package com.zubtech.rohingyashikho.presentation.progress

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile & Progress", fontWeight = FontWeight.Black, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B))
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFF6366F1)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF3B82F6))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    AttractiveProfileHeader(
                        userName = uiState.userName,
                        masteredItems = uiState.masteredItems,
                        totalItems = uiState.totalItems
                    )
                }

                item {
                    QuickStatsSection(
                        streak = uiState.streak,
                        points = uiState.totalPoints
                    )
                }

                item {
                    Text(
                        text = "Learning Path Progress",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                items(uiState.levelProgress.toList()) { (levelId, progress) ->
                    ModernLevelProgressCard(levelId = levelId, progress = progress)
                }

                item {
                    BeautifulAchievementsSection()
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.logout(onLogout) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFEE2E2),
                            contentColor = Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun AttractiveProfileHeader(userName: String, masteredItems: Int, totalItems: Int) {
    val percentage = if (totalItems > 0) (masteredItems.toFloat() / totalItems.toFloat()) else 0f
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PRO LEARNER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userName,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Keep up the excellent work!",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(90.dp)
            ) {
                Canvas(modifier = Modifier.size(90.dp)) {
                    drawArc(
                        color = Color.White.copy(alpha = 0.2f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = Color.White,
                        startAngle = -90f,
                        sweepAngle = percentage * 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(percentage * 100).toInt()}%",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Mastery",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickStatsSection(streak: Int, points: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Day Streak",
            value = "$streak Days",
            icon = Icons.Default.LocalFireDepartment,
            iconColor = Color(0xFFF59E0B),
            bgColor = Color(0xFFFEF3C7)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Total Points",
            value = "$points XP",
            icon = Icons.Default.Star,
            iconColor = Color(0xFF10B981),
            bgColor = Color(0xFFD1FAE5)
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    bgColor: Color
) {
    Surface(
        modifier = modifier.shadow(4.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(bgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 12.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            }
        }
    }
}

@Composable
fun ModernLevelProgressCard(levelId: String, progress: Float) {
    val cleanName = levelId.replace("lvl_", "").replaceFirstChar { it.uppercase() }
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Level $cleanName",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4F46E5)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = Color(0xFF6366F1),
                trackColor = Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
fun BeautifulAchievementsSection() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Earned Badges",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BeautifulBadge(icon = Icons.Default.EmojiEvents, label = "First Lesson", unlocked = true, activeColor = Color(0xFF3B82F6))
                BeautifulBadge(icon = Icons.Default.EmojiEvents, label = "7 Day Streak", unlocked = false, activeColor = Color(0xFFF59E0B))
                BeautifulBadge(icon = Icons.Default.EmojiEvents, label = "Alphabet Master", unlocked = false, activeColor = Color(0xFF10B981))
            }
        }
    }
}

@Composable
fun BeautifulBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    unlocked: Boolean,
    activeColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .shadow(if (unlocked) 6.dp else 0.dp, CircleShape)
                .background(if (unlocked) activeColor else Color(0xFFF1F5F9), CircleShape)
                .border(2.dp, if (unlocked) Color.White else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = if (unlocked) Color.White else Color(0xFF94A3B8)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (unlocked) Color(0xFF334155) else Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}
