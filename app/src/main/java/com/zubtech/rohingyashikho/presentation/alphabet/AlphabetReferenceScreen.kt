package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.presentation.ui.components.AnimatedBeautyBackground

enum class AlphabetTab { CONSONANTS, VOWELS }

@Composable
fun AlphabetReferenceScreen(
    initialType: String = "consonant",
    onNavigateBack: () -> Unit,
    onItemClick: (String) -> Unit,
    onOpenPdf: () -> Unit,
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val consonants by viewModel.consonantsForWriting.collectAsState()
    val vowels by viewModel.vowels.collectAsState()
    val allProgress by viewModel.allProgress.collectAsState()
    
    val clickCounts = remember(allProgress) {
        allProgress.associate { it.lessonItemId to it.clickCount }
    }

    var selectedTab by remember { 
        mutableStateOf(if (initialType == "vowel") AlphabetTab.VOWELS else AlphabetTab.CONSONANTS) 
    }

    val themeColor by animateColorAsState(
        targetValue = if (selectedTab == AlphabetTab.CONSONANTS) Color(0xFF6366F1) else Color(0xFF10B981),
        animationSpec = tween(600),
        label = "themeColor"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBeautyBackground(themeColor)

        Scaffold(
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                AlphabetTopBar(onNavigateBack = onNavigateBack, onOpenPdf = onOpenPdf)
                
                AlphabetHeroCard(
                    title = if (selectedTab == AlphabetTab.CONSONANTS) "Consonants" else "Vowels",
                    count = if (selectedTab == AlphabetTab.CONSONANTS) "${consonants.size} Letters" else "${vowels.size} Letters",
                    themeColor = themeColor
                )
                
                AlphabetCategoryToggle(
                    selectedTab = selectedTab,
                    themeColor = themeColor,
                    onTabSelected = { selectedTab = it }
                )
                
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)),
                    color = Color.White.copy(alpha = 0.95f),
                    shadowElevation = 12.dp
                ) {
                    AnimatedContent(
                        targetState = selectedTab,
                        transitionSpec = {
                            if (targetState == AlphabetTab.VOWELS) {
                                slideInHorizontally { it } + fadeIn() togetherWith
                                        slideOutHorizontally { -it } + fadeOut()
                            } else {
                                slideInHorizontally { -it } + fadeIn() togetherWith
                                        slideOutHorizontally { it } + fadeOut()
                            }.using(SizeTransform(clip = false))
                        },
                        label = "TabTransition"
                    ) { tab ->
                        val currentItems = if (tab == AlphabetTab.CONSONANTS) consonants else vowels
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 32.dp, start = 20.dp, end = 20.dp, top = 28.dp),
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                itemsIndexed(currentItems) { index, item ->
                                    AnimatedAlphabetItem(
                                        item = item, 
                                        index = index,
                                        icon = Icons.Rounded.Draw,
                                        clickCount = clickCounts[item.id],
                                        accentColor = themeColor
                                    ) {
                                        onItemClick(item.id)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlphabetCategoryToggle(
    selectedTab: AlphabetTab,
    themeColor: Color,
    onTabSelected: (AlphabetTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .height(60.dp)
            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(22.dp))
            .padding(6.dp)
    ) {
        val bias by animateFloatAsState(
            targetValue = if (selectedTab == AlphabetTab.CONSONANTS) -1f else 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow),
            label = "bias"
        )

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(BiasAlignment(horizontalBias = bias, verticalBias = 0f))
                .shadow(8.dp, RoundedCornerShape(18.dp))
                .background(Color.White, RoundedCornerShape(18.dp))
        )

        Row(modifier = Modifier.fillMaxSize()) {
            AlphabetToggleButton(
                text = "Consonants",
                isSelected = AlphabetTab.CONSONANTS == selectedTab,
                selectedColor = themeColor,
                onClick = { onTabSelected(AlphabetTab.CONSONANTS) },
                modifier = Modifier.weight(1f)
            )
            AlphabetToggleButton(
                text = "Vowels",
                isSelected = AlphabetTab.VOWELS == selectedTab,
                selectedColor = themeColor,
                onClick = { onTabSelected(AlphabetTab.VOWELS) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AlphabetToggleButton(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) selectedColor else Color.White,
        label = "textColor"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
        )
    }
}

@Composable
fun AnimatedAlphabetItem(
    item: LessonItem,
    index: Int,
    icon: ImageVector = Icons.Rounded.RecordVoiceOver,
    isCurrentlyPlaying: Boolean = false,
    clickCount: Int? = null,
    accentColor: Color = Color(0xFF6366F1),
    onClick: () -> Unit
) {
    val state = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(tween(500, (index % 12) * 50)) + scaleIn(tween(500, (index % 12) * 50), initialScale = 0.8f)
    ) {
        AlphabetItemCard(item, index, icon, isCurrentlyPlaying, clickCount, accentColor, onClick)
    }
}

@Composable
fun AlphabetTopBar(onNavigateBack: () -> Unit, onOpenPdf: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(44.dp)
                .background(Color.White, CircleShape)
                .shadow(6.dp, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B), modifier = Modifier.size(22.dp))
        }

        Text(
            text = "Writing Studio",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.25f),
            onClick = onOpenPdf
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.MenuBook,
                contentDescription = "Open PDF",
                tint = Color.White,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun AlphabetHeroCard(title: String, count: String, themeColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .height(130.dp),
        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(themeColor, themeColor.copy(alpha = 0.7f))
                    )
                )
        ) {
            Box(modifier = Modifier.size(110.dp).offset(x = (-30).dp, y = (-30).dp).background(Color.White.copy(0.15f), CircleShape))
            
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(64.dp).background(Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Brush,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(24.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                    Text(text = count, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                }

                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    null,
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}

@Composable
fun AlphabetItemCard(
    item: LessonItem,
    index: Int,
    icon: ImageVector,
    isCurrentlyPlaying: Boolean = false,
    clickCount: Int? = null,
    accentColor: Color,
    onClick: () -> Unit
) {
    val pastelColors = listOf(
        Color(0xFFE3F2FD), Color(0xFFFCE4EC), Color(0xFFE8F5E9), Color(0xFFFFF3E0),
        Color(0xFFF3E5F5), Color(0xFFEFEBE9), Color(0xFFF1F8E9), Color(0xFFE0F7FA)
    )
    val accentColors = listOf(
        Color(0xFF1E88E5), Color(0xFFD81B60), Color(0xFF43A047), Color(0xFFFB8C00),
        Color(0xFF8E24AA), Color(0xFF6D4C41), Color(0xFF7CB342), Color(0xFF00ACC1)
    )

    val colorIdx = index % pastelColors.size
    val bgColor = pastelColors[colorIdx]
    val itemAccentColor = if (isCurrentlyPlaying) accentColor else accentColors[colorIdx]

    val infiniteTransition = rememberInfiniteTransition(label = "hover")
    val floatingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floating"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (isCurrentlyPlaying) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "playingScale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .graphicsLayer { 
                translationY = floatingOffset
                scaleX = cardScale
                scaleY = cardScale
            }
            .shadow(
                elevation = if (isCurrentlyPlaying) 20.dp else 10.dp, 
                shape = RoundedCornerShape(26.dp), 
                spotColor = itemAccentColor.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (isCurrentlyPlaying) BorderStroke(2.5.dp, accentColor) else null
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Box(modifier = Modifier.fillMaxSize().background(bgColor.copy(alpha = 0.35f))) {
                
                // Top-Left: Decorative Accent
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(GenericShape { size, _ ->
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(0f, size.height)
                            close()
                        })
                        .background(itemAccentColor.copy(alpha = 0.25f))
                        .align(Alignment.TopStart)
                )

                Icon(
                    imageVector = if (isCurrentlyPlaying) Icons.AutoMirrored.Rounded.VolumeUp else icon,
                    contentDescription = null,
                    tint = itemAccentColor,
                    modifier = Modifier
                        .padding(6.dp)
                        .size(18.dp)
                        .align(Alignment.TopStart)
                )

                // Progress Badge
                if (clickCount != null) {
                    Surface(
                        modifier = Modifier.padding(6.dp).align(Alignment.TopEnd),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.8f)
                    ) {
                        Text(
                            text = clickCount.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = itemAccentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Center Content: Rohingya Script
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = item.scriptText,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.rotate(if (index % 2 == 0) -2f else 2f)
                        )
                    }
                }
            }
        }
    }
}
