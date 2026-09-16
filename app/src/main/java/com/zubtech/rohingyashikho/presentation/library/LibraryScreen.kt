package com.zubtech.rohingyashikho.presentation.library

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    onNavigateToConsonants: () -> Unit,
    onNavigateToVowels: () -> Unit,
    onNavigateToPdf: () -> Unit,
    onNavigateToDrawing: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Learning Library", fontWeight = FontWeight.Black, color = Color(0xFF1E293B)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8FAFC)
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600)) + slideInVertically(initialOffsetY = { -40 })
                ) {
                    LibraryHeroCard()
                }
            }

            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600, 100)) + slideInHorizontally(initialOffsetX = { -40 })
                ) {
                    Text(
                        text = "Core Learning",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CategoryItemWrapper(
                        modifier = Modifier.weight(1f),
                        visible = visible,
                        delay = 200,
                        enterFromLeft = true
                    ) {
                        CategoryCard(
                            title = "Consonants",
                            subtitle = "28 Letters",
                            icon = Icons.Default.Translate,
                            color = Color(0xFF6366F1),
                            onClick = onNavigateToConsonants
                        )
                    }
                    CategoryItemWrapper(
                        modifier = Modifier.weight(1f),
                        visible = visible,
                        delay = 300,
                        enterFromLeft = false
                    ) {
                        CategoryCard(
                            title = "Vowels",
                            subtitle = "Haraka & Rules",
                            icon = Icons.Default.MenuBook,
                            color = Color(0xFFEC4899),
                            onClick = onNavigateToVowels
                        )
                    }
                }
            }

            item {
                ResourceItemWrapper(visible = visible, delay = 400) {
                    ResourceCard(
                        title = "Rohingya Qaida PDF",
                        description = "Traditional Rohingya Qaida Book (Digital).",
                        icon = Icons.Default.Book,
                        color = Color(0xFF10B981),
                        onClick = onNavigateToPdf
                    )
                }
            }

            item {
                ResourceItemWrapper(visible = visible, delay = 500) {
                    ResourceCard(
                        title = "Writing Practice",
                        description = "Interactive drawing and Hanifi script.",
                        icon = Icons.Default.Draw,
                        color = Color(0xFFF59E0B),
                        onClick = onNavigateToDrawing
                    )
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun CategoryItemWrapper(
    modifier: Modifier,
    visible: Boolean,
    delay: Int,
    enterFromLeft: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500, delay)) + slideInHorizontally(
            animationSpec = tween(500, delay),
            initialOffsetX = { if (enterFromLeft) -50 else 50 }
        ),
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun ResourceItemWrapper(
    visible: Boolean,
    delay: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500, delay)) + slideInVertically(
            animationSpec = tween(500, delay),
            initialOffsetY = { 50 }
        )
    ) {
        content()
    }
}

@Composable
fun LibraryHeroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .shadow(12.dp, RoundedCornerShape(24.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = "Resource Library",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Master the Hanifi script with\nthese essential resources.",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.9f),
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun CategoryCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
            Text(text = subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
        }
    }
}

@Composable
fun ResourceCard(
    title: String,
    description: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
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
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                Text(text = description, fontSize = 12.sp, color = Color(0xFF64748B))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFCBD5E1))
        }
    }
}
