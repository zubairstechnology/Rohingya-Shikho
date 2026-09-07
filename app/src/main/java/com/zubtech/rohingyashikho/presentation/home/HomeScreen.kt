package com.zubtech.rohingyashikho.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
                points = uiState.totalPoints,
                userName = uiState.userName
            )
        },
        containerColor = AppBackground
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 4 }
                    ) {
                        ContinueLearningCard(
                            title = "Pick up Where you left",
                            lessonName = "Advanced Rohingya",
                            progress = 0.69f,
                            onContinue = { /* TODO */ }
                        )
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = isLoaded,
                        enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { it / 2 }
                    ) {
                        CategorySection(
                            onAlphabetClick = onAlphabetClick,
                            onQuizClick = { onStartQuiz("1") }
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(120.dp)) }
            }
        }
    }
}

@Composable
fun HomeTopBar(points: Int, userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Profile Avatar
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppPrimary.copy(alpha = 0.1f))
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
                    text = "Keep learning, $userName!",
                    fontSize = 13.sp,
                    color = AppTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Surface(
            color = AppPrimary.copy(alpha = 0.1f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.height(40.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.Diamond, 
                    contentDescription = null, 
                    tint = DiamondGold, 
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = points.toString(), 
                    color = AppPrimary, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun ContinueLearningCard(
    title: String,
    lessonName: String,
    progress: Float,
    onContinue: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onContinue() },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title, 
                        color = Color.White, 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Continue your journey to mastery", 
                        color = Color.White.copy(alpha = 0.5f), 
                        fontSize = 13.sp
                    )
                }
                
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = progress,
                        color = AppPrimary,
                        strokeWidth = 6.dp,
                        trackColor = Color.White.copy(alpha = 0.1f),
                        modifier = Modifier.size(58.dp).scale(scale)
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%", 
                        color = Color.White, 
                        fontSize = 13.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(28.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = lessonName, 
                    color = Color.White, 
                    fontSize = 26.sp, 
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f)
                )
                
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "28 Lessons", 
                                color = Color.White, 
                                fontWeight = FontWeight.Bold, 
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.Diamond, 
                            contentDescription = null, 
                            tint = DiamondGold, 
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "40/100", 
                            color = Color.White, 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFBC02D), Color(0xFFFFA000))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                 Text(
                    text = "ROHINGYA", 
                    color = Color.White.copy(alpha = 0.95f), 
                    fontWeight = FontWeight.Black, 
                    fontSize = 36.sp, 
                    letterSpacing = 6.sp
                )
                 
                 // Decorative circle
                 Box(
                     modifier = Modifier
                         .align(Alignment.BottomEnd)
                         .offset(x = 20.dp, y = 20.dp)
                         .size(80.dp)
                         .clip(CircleShape)
                         .background(Color.White.copy(alpha = 0.1f))
                 )
            }
        }
    }
}

data class HomeCategory(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val onClick: () -> Unit
)

@Composable
fun CategorySection(onAlphabetClick: () -> Unit, onQuizClick: () -> Unit) {
    val categories = listOf(
        HomeCategory(
            "Alphabet & Words",
            "Learn Hanifi script",
            Icons.Rounded.MenuBook,
            Color(0xFFE8F5E9),
            onAlphabetClick
        ),
        HomeCategory(
            "Pronunciation",
            "Listen and repeat",
            Icons.Rounded.VolumeUp,
            Color(0xFFE3F2FD),
            {}
        ),
        HomeCategory(
            "Daily Chat",
            "Real-life scenarios",
            Icons.Rounded.Chat,
            Color(0xFFFFF3E0),
            {}
        ),
        HomeCategory(
            "Quizzes",
            "Test knowledge",
            Icons.Rounded.Quiz,
            Color(0xFFF3E5F5),
            onQuizClick
        ),
        HomeCategory(
            "Writing",
            "Master your skills",
            Icons.Rounded.Edit,
            Color(0xFFE0F2F1),
            {}
        ),
        HomeCategory(
            "Advanced",
            "Structured learning",
            Icons.Rounded.Flag,
            Color(0xFFFCE4EC),
            {}
        )
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Challenge", 
                fontSize = 22.sp, 
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            TextButton(onClick = { /* TODO */ }) {
                Text("See All", color = AppPrimary, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))

        categories.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    ChallengeCard(
                        title = item.title,
                        subtitle = item.subtitle,
                        backgroundColor = item.backgroundColor,
                        icon = item.icon,
                        modifier = Modifier.weight(1f),
                        onClick = item.onClick
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun ChallengeCard(
    title: String,
    subtitle: String,
    backgroundColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .height(180.dp)
            .scale(scale),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon, 
                    contentDescription = null, 
                    tint = Color.Black.copy(alpha = 0.8f), 
                    modifier = Modifier.size(26.dp)
                )
            }
            Column {
                Text(
                    text = title, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 16.sp, 
                    color = Color.Black,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle, 
                    fontSize = 12.sp, 
                    color = Color.Gray.copy(alpha = 0.8f), 
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
