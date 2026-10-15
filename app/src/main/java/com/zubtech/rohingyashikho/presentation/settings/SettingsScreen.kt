package com.zubtech.rohingyashikho.presentation.settings

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdmin: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            TopAppBar(
                title = { Text("Profile & Settings", fontWeight = FontWeight.Black, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B))
                    }
                },
                actions = {
                    if (isAdmin) {
                        IconButton(onClick = onNavigateToAdmin) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin", tint = Color(0xFF6366F1))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.White, Color(0xFFF1F5F9))
                    )
                )
        ) {
            // Creative Profile Section
            ProfileHeader(
                name = userName ?: "Zubair Nsk",
                onEditClick = { /* Handle edit profile */ }
            )

            // Dynamic Stats Section
            StatsRow()

            Spacer(modifier = Modifier.height(24.dp))

            // Main Settings Content
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                
                // Weekly Learning Goal Card
                WeeklyProgressCard()

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Groups
                SettingsGroup(title = "App Customization") {
                    SettingsItem(
                        icon = Icons.Default.Palette,
                        iconBg = Color(0xFFEEF2FF),
                        iconTint = Color(0xFF6366F1),
                        title = "Appearance",
                        subtitle = if (themeMode == "dark") "Dark Mode" else "Light Mode",
                        onClick = { viewModel.setThemeMode(if (themeMode == "dark") "light" else "dark") }
                    )
                    SettingsItem(
                        icon = Icons.Default.Translate,
                        iconBg = Color(0xFFDBEAFE),
                        iconTint = Color(0xFF3B82F6),
                        title = "App Language",
                        subtitle = if (appLanguage == "en") "English (United Kingdom)" else "Rohingya (Hanifi)",
                        onClick = { /* Navigate to Language selection */ }
                    )
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.VolumeUp,
                        iconBg = Color(0xFFD1FAE5),
                        iconTint = Color(0xFF10B981),
                        title = "Voice & Audio",
                        subtitle = "Native Pronunciation • High Quality",
                        onClick = { }
                    )
                }

                SettingsGroup(title = "Learning & Content") {
                    SettingsItem(
                        icon = Icons.Default.CloudDownload,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = Color(0xFFD97706),
                        title = "Offline Lessons",
                        subtitle = "14 lessons downloaded (120MB)",
                        onClick = { }
                    )
                    SettingsItem(
                        icon = Icons.Default.NotificationsActive,
                        iconBg = Color(0xFFFFEDD5),
                        iconTint = Color(0xFFF59E0B),
                        title = "Daily Reminders",
                        hasSwitch = true,
                        switchChecked = true
                    )
                }

                SettingsGroup(title = "Support & Legal") {
                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.Help,
                        iconBg = Color(0xFFF1F5F9),
                        iconTint = Color(0xFF64748B),
                        title = "Help Center",
                        onClick = { }
                    )
                    SettingsItem(
                        icon = Icons.Default.Security,
                        iconBg = Color(0xFFF1F5F9),
                        iconTint = Color(0xFF64748B),
                        title = "Privacy Policy",
                        onClick = { }
                    )
                    SettingsItem(
                        icon = Icons.Default.Info,
                        iconBg = Color(0xFFF1F5F9),
                        iconTint = Color(0xFF64748B),
                        title = "About Rohingya Shikho",
                        subtitle = "Version 1.1.2 • Build 2024",
                        onClick = { }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Logout Button
                Button(
                    onClick = { /* Handle sign out */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFEE2E2),
                        contentColor = Color(0xFFEF4444)
                    ),
                    elevation = null
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign Out", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(110.dp),
                shape = CircleShape,
                border = BorderStroke(4.dp, Color(0xFF6366F1).copy(alpha = 0.1f)),
                shadowElevation = 8.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.name),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Surface(
                modifier = Modifier
                    .size(34.dp)
                    .clickable { onEditClick() },
                shape = CircleShape,
                color = Color(0xFF6366F1),
                tonalElevation = 6.dp
            ) {
                Icon(
                    Icons.Default.CameraAlt,
                    contentDescription = "Edit Profile",
                    modifier = Modifier.padding(8.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = name,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1E293B)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 6.dp)
        ) {
            Badge(text = "Level 24", containerColor = Color(0xFFEEF2FF), contentColor = Color(0xFF6366F1))
            Spacer(modifier = Modifier.width(8.dp))
            Badge(text = "Gold Member", containerColor = Color(0xFFFEF3C7), contentColor = Color(0xFFD97706))
        }
    }
}

@Composable
fun Badge(text: String, containerColor: Color, contentColor: Color) {
    Surface(
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
fun StatsRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Streak",
            value = "12",
            icon = Icons.Default.LocalFireDepartment,
            color = Color(0xFFF59E0B)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Score",
            value = "2.4k",
            icon = Icons.Default.Stars,
            color = Color(0xFF6366F1)
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Lessons",
            value = "84",
            icon = Icons.Default.MenuBook,
            color = Color(0xFF10B981)
        )
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1E293B))
            Text(label, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun WeeklyProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Weekly Goal", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                    Text("Keep it up!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Icon(
                    painter = painterResource(id = R.drawable.every_day),
                    contentDescription = null,
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            LinearProgressIndicator(
                progress = { 0.72f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = Color(0xFF6366F1),
                trackColor = Color.White.copy(alpha = 0.1f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("72% completed", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                Text("18 / 25 lessons", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            modifier = Modifier.padding(start = 12.dp, bottom = 12.dp)
        )
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            shadowElevation = 0.5.dp
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    hasSwitch: Boolean = false,
    switchChecked: Boolean = false,
    onSwitchChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(iconBg, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(22.dp))
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1E293B)
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        if (hasSwitch) {
            Switch(
                checked = switchChecked,
                onCheckedChange = onSwitchChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF10B981),
                    uncheckedTrackColor = Color(0xFFE2E8F0)
                ),
                modifier = Modifier.scale(0.85f)
            )
        } else if (onClick != null) {
            Icon(
                Icons.Default.ChevronRight,
                null,
                tint = Color(0xFFCBD5E1),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
