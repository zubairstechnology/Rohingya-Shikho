package com.zubtech.rohingyashikho.presentation.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.presentation.progress.ProgressViewModel
import com.zubtech.rohingyashikho.presentation.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdmin: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: ProgressViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val appLanguage by settingsViewModel.appLanguage.collectAsState()
    val isAdmin by settingsViewModel.isAdmin.collectAsState()

    val primaryColor = Color(0xFF6366F1)
    val backgroundColor = Color(0xFFF8FAFC)

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = onNavigateToAdmin) {
                            Icon(Icons.Default.AdminPanelSettings, "Admin", tint = primaryColor)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Profile Info Card
            item {
                ProfileHeroSection(
                    userName = uiState.userName,
                    streak = uiState.streak,
                    points = uiState.totalPoints
                )
            }

            // Quick Stats
            item {
                StatsGrid(
                    mastered = uiState.masteredItems,
                    total = uiState.totalItems,
                    progress = (uiState.levelProgress.values.average() * 100).toInt().coerceAtLeast(0)
                )
            }

            // Learning Section
            item {
                SectionHeader("My Learning Journey")
            }
            
            item {
                LearningSummaryCard(uiState.masteredItems, uiState.totalItems)
            }

            // Settings Section
            item {
                SectionHeader("Preferences")
            }

            item {
                SettingsCard {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        title = "Theme",
                        subtitle = if (themeMode == "dark") "Dark Mode" else "Light Mode",
                        onClick = { 
                            settingsViewModel.setThemeMode(if (themeMode == "dark") "light" else "dark") 
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    SettingsItem(
                        icon = Icons.Default.Translate,
                        title = "Language",
                        subtitle = if (appLanguage == "en") "English" else "Rohingya",
                        onClick = {
                            settingsViewModel.setLanguage(if (appLanguage == "en") "rhg" else "en")
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Notifications",
                        subtitle = "Daily reminders enabled",
                        trailing = {
                            Switch(checked = true, onCheckedChange = {}, modifier = Modifier.size(32.dp))
                        }
                    )
                }
            }

            // Support Section
            item {
                SectionHeader("Support")
            }

            item {
                SettingsCard {
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Help & Support",
                        onClick = {}
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "About App",
                        subtitle = "Version 1.2.0",
                        onClick = {}
                    )
                }
            }

            // Logout
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.logout(onLogoutSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFEE2E2),
                        contentColor = Color(0xFFEF4444)
                    ),
                    elevation = null
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileHeroSection(userName: String, streak: Int, points: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .shadow(8.dp, CircleShape)
                .border(4.dp, Color.White, CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.name),
                contentDescription = "Avatar",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        
        Row(
            modifier = Modifier.padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BadgeChip(text = "🗲 $streak Day Streak", color = Color(0xFFEF4444))
            Spacer(modifier = Modifier.width(8.dp))
            BadgeChip(text = "★ $points Points", color = Color(0xFFF59E0B))
        }
    }
}

@Composable
fun BadgeChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun StatsGrid(mastered: Int, total: Int, progress: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatBox(
            modifier = Modifier.weight(1f),
            label = "Mastered",
            value = mastered.toString(),
            icon = Icons.Default.EmojiEvents,
            color = Color(0xFF10B981)
        )
        StatBox(
            modifier = Modifier.weight(1f),
            label = "Progress",
            value = "$progress%",
            icon = Icons.Default.Timeline,
            color = Color(0xFF6366F1)
        )
    }
}

@Composable
fun StatBox(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            Text(text = label, fontSize = 12.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF64748B)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFF6366F1), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFCBD5E1))
        }
    }
}

@Composable
fun LearningSummaryCard(mastered: Int, total: Int) {
    val progress = if (total > 0) mastered.toFloat() / total else 0f
    
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFEEF2FF)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall Mastery",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4338CA)
                )
                Text(
                    text = "$mastered / $total Units",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4338CA)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = Color(0xFF6366F1),
                trackColor = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Keep going! You're doing great.",
                fontSize = 12.sp,
                color = Color(0xFF6366F1).copy(alpha = 0.8f)
            )
        }
    }
}
