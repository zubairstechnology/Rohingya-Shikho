package com.zubtech.rohingyashikho.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val appLanguage by viewModel.appLanguage.collectAsState()
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            MaterialTheme.colorScheme.background
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (appLanguage == "rhg") "⚙️ 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮" else "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Profile Card
                ProfileSettingsHeader()

                // Settings Section - General Settings
                SettingsSectionTitle(title = if (appLanguage == "rhg") "𓄿𓐮𓊎𓐮𓂝𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮" else "App Customization")

                SettingsCardItem(
                    icon = when (themeMode) {
                        "dark" -> Icons.Default.DarkMode
                        "light" -> Icons.Default.LightMode
                        else -> Icons.Default.SettingsSuggest
                    },
                    iconBgColor = Color(0xFF6366F1),
                    title = if (appLanguage == "rhg") "𓂝𓐮𓊎𓐮𓄿𓐮" else "Theme Mode",
                    summary = when (themeMode) {
                        "dark" -> "Dark Mode"
                        "light" -> "Light Mode"
                        else -> "System Default"
                    },
                    onClick = { showThemeDialog = true }
                )

                SettingsCardItem(
                    icon = Icons.Default.Language,
                    iconBgColor = Color(0xFF00ACC1),
                    title = if (appLanguage == "rhg") "𓄿𓐮𓆵𓏞𓐮" else "Language / 𓄿𓐮𓆵𓏞𓐮",
                    summary = if (appLanguage == "rhg") "𓅌𓐮𓊎𓐮𓆵𓏏𓐮𓆷𓐮 (Rohingya)" else "English",
                    onClick = { showLanguageDialog = true }
                )

                // Settings Section - Additional Settings
                SettingsSectionTitle(title = if (appLanguage == "rhg") "𓎡𓐮𓈷𓐮𓅆𓐮𓄿𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮" else "Preferences")

                SettingsCardItem(
                    icon = Icons.Default.Speed,
                    iconBgColor = Color(0xFFEC4899),
                    title = if (appLanguage == "rhg") "𓄿𓐮𓅱𓐮𓊎𓐮𓆵𓏏𓐮 𓂝𓐮𓊎𓐮𓄿𓐮" else "Audio Playback Speed",
                    summary = "Normal",
                    onClick = { /* Future Implementation */ }
                )

                SettingsCardItem(
                    icon = Icons.Default.Notifications,
                    iconBgColor = Color(0xFFA855F7),
                    title = if (appLanguage == "rhg") "𓄿𓐮𓆷𓐮𓅱𓐮𓂝𓐮" else "Daily Reminders",
                    summary = "On - 8:00 PM",
                    onClick = { /* Future Implementation */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // App version info
                Text(
                    text = "Rohingya Shikho v1.1.0\nBeautiful & Powerful Learning Settings",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Dynamic Theme Selector Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(if (appLanguage == "rhg") "𓂝𓐮𓊎𓐮𓄿𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮" else "Choose Theme Mode") },
            text = {
                Column {
                    listOf("light" to "Light Mode", "dark" to "Dark Mode", "system" to "System Default").forEach { (mode, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setThemeMode(mode)
                                    showThemeDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = themeMode == mode, onClick = {
                                viewModel.setThemeMode(mode)
                                showThemeDialog = false
                            })
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    // Dynamic Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(if (appLanguage == "rhg") "𓄿𓐮𓆵𓏞𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮" else "Select Language") },
            text = {
                Column {
                    listOf("en" to "English", "rhg" to "𓅌𓐮𓊎𓐮𓆵𓏏𓐮𓆷𓐮 (Rohingya Hanifi)").forEach { (lang, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = appLanguage == lang, onClick = {
                                viewModel.setLanguage(lang)
                                showLanguageDialog = false
                            })
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = label, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
fun ProfileSettingsHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF6366F1), Color(0xFFEC4899))
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Rohingya Learner",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Achieve your everyday learning goals",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.sp
        ),
        modifier = Modifier.padding(start = 4.dp, top = 8.dp)
    )
}

@Composable
fun SettingsCardItem(
    icon: ImageVector,
    iconBgColor: Color,
    title: String,
    summary: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color = iconBgColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBgColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
