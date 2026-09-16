package com.zubtech.rohingyashikho.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.presentation.ui.theme.*

@Composable
fun HomeScreen(
    onLessonSelected: (String) -> Unit,
    onAlphabetClick: () -> Unit,
    onStartQuiz: (String) -> Unit,
    onStartReview: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) isLoaded = true
    }

    Scaffold(
        topBar = {
            HomeTopBar(
                userName = uiState.userName
            )
        },
        containerColor = Color.White
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        AnimatedVisibility(
                            visible = isLoaded,
                            enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 4 }
                        ) {
                            ContinueLearningCard(
                                progress = 0.69f,
                                onContinue = { /* TODO */ }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        ScriptLearningCard(
                            progress = 0.34f,
                            streak = uiState.streak
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        LearningPathSection(onGoalClick = {})

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            LevelCard(
                                level = 1,
                                title = "Basic",
                                sections = 6,
                                items = listOf("Hanifi Scri...", "28 Conson...", "10 Vowels")
                            )
                        }
                        item {
                            LevelCard(
                                level = 2,
                                title = "Vowel Signs",
                                sections = 8,
                                items = listOf("Short Vowels", "Long Vowels", "Special Signs"),
                                bgColor = Color(0xFFFAF5FF)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.graphic1),
                    contentDescription = "Profile",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Rohingya Shikho",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Keep learning, $userName",
                    fontSize = 13.sp,
                    color = AppTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        IconButton(
            onClick = { /* Notification */ },
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFF1F5F9), CircleShape)
        ) {
            Icon(
                Icons.Rounded.Notifications, 
                contentDescription = "Notifications", 
                tint = Color(0xFF1E293B),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ContinueLearningCard(
    progress: Float,
    onContinue: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clickable { onContinue() },
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                    )
                )
            )

            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Explore, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "Pick up Where you left", 
                        color = Color.White.copy(alpha = 0.9f), 
                        fontSize = 14.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Rohingya langua...", 
                    color = Color.White, 
                    fontSize = 32.sp, 
                    fontWeight = FontWeight.Black,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.MenuBook, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "28 Lessons", 
                            color = Color.White, 
                            fontSize = 13.sp, 
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            // Circular progress with Icon
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(24.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = progress,
                        color = Color(0xFFFFD700),
                        trackColor = Color.White.copy(alpha = 0.2f),
                        strokeWidth = 7.dp,
                        modifier = Modifier.size(86.dp)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${(progress * 100).toInt()}%", 
                            color = Color.White, 
                            fontWeight = FontWeight.Black, 
                            fontSize = 18.sp
                        )
                        Icon(
                            Icons.Rounded.School, 
                            null, 
                            tint = Color.White.copy(alpha = 0.5f), 
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Illustration
            Image(
                painter = painterResource(id = R.drawable.graphic1),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(170.dp)
                    .offset(x = 10.dp, y = 15.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ScriptLearningCard(progress: Float, streak: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(28.dp), spotColor = Color.Black.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFF007AFF), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.MenuBook, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Rohingya", 
                    fontWeight = FontWeight.Black, 
                    fontSize = 18.sp, 
                    color = Color.Black
                )
                Text(
                    "Hanifi Script Learning", 
                    color = Color.Gray, 
                    fontSize = 13.sp, 
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Color(0xFF6366F1),
                    trackColor = Color(0xFFF1F5F9)
                )
            }
            
            Spacer(Modifier.width(16.dp))
            
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Rounded.LocalFireDepartment, 
                        null, 
                        tint = Color(0xFFFF5722), 
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "$streak Days", 
                        fontWeight = FontWeight.Black, 
                        fontSize = 15.sp, 
                        color = Color.Black
                    )
                }
                Text(
                    "${(progress * 100).toInt()}%", 
                    fontSize = 11.sp, 
                    color = Color.Gray, 
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LearningPathSection(onGoalClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFF6366F1), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Explore, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
        
        Spacer(Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Learning Path", 
                fontWeight = FontWeight.Black, 
                fontSize = 22.sp, 
                color = Color.Black
            )
            Text(
                "Foundation to mastery", 
                color = Color.Gray, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Medium
            )
        }
        
        Surface(
            onClick = onGoalClick,
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.EmojiEvents, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    "Your Goal", 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = Color.Black
                )
                Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
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
    bgColor: Color = Color(0xFFF0F9FF)
) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .shadow(4.dp, RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    color = Color(0xFF007AFF), 
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        "LEVEL $level", 
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), 
                        color = Color.White, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Black
                    )
                }
                
                Spacer(Modifier.height(10.dp))
                
                Text(
                    title, 
                    fontWeight = FontWeight.Black, 
                    fontSize = 24.sp, 
                    color = Color.Black
                )
                
                Spacer(Modifier.height(6.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.MenuBook, null, tint = Color(0xFF007AFF), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "$sections Sections", 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = Color(0xFF007AFF)
                    )
                }
                
                Spacer(Modifier.height(20.dp))
                
                items.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.padding(bottom = 6.dp)
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle, 
                            null, 
                            tint = Color(0xFF007AFF), 
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            item, 
                            fontSize = 13.sp, 
                            color = Color(0xFF475569),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxHeight()
            ) {
                Image(
                    painterResource(id = R.drawable.graphic1), 
                    null, 
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.Fit
                )
                
                Spacer(Modifier.height(20.dp))
                
                IconButton(
                    onClick = {}, 
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White, CircleShape)
                        .shadow(6.dp, CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null, tint = Color(0xFF007AFF), modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}
