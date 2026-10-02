package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.domain.model.LessonItem

enum class AlphabetTab { CONSONANTS, VOWELS }

@Composable
fun AlphabetReferenceScreen(
    initialType: String = "consonant",
    onNavigateBack: () -> Unit,
    onOpenPdf: () -> Unit,
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val consonants by viewModel.consonants.collectAsState()
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

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(themeColor, themeColor.copy(alpha = 0.6f))
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                AlphabetTopBar(onNavigateBack = onNavigateBack, onOpenPdf = onOpenPdf)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "REFERENCE GUIDE",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = if (selectedTab == AlphabetTab.CONSONANTS) "Consonants" else "Vowels",
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = if (selectedTab == AlphabetTab.CONSONANTS) "${consonants.size} Hanifi Characters" else "${vowels.size} Hanifi Characters",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = if (selectedTab == AlphabetTab.CONSONANTS) Icons.Rounded.GridView else Icons.Rounded.Category,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(90.dp)
                            .rotate(15f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(36.dp, RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp), spotColor = Color.Black.copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp)),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AlphabetCategoryToggle(
                            selectedTab = selectedTab,
                            themeColor = themeColor,
                            onTabSelected = { selectedTab = it }
                        )

                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                            },
                            modifier = Modifier.fillMaxSize().weight(1f),
                            label = "TabTransition"
                        ) { tab ->
                            val currentItems = if (tab == AlphabetTab.CONSONANTS) consonants else vowels
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(4),
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(bottom = 32.dp, start = 20.dp, end = 20.dp, top = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    itemsIndexed(currentItems) { index, item ->
                                        AnimatedAlphabetItemReference(
                                            item = item, 
                                            index = index,
                                            clickCount = clickCounts[item.id] ?: 0,
                                            onClick = { viewModel.playAudio(item) }
                                        )
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
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .height(56.dp)
            .background(Color(0xFFF1F5F9), RoundedCornerShape(20.dp))
            .padding(4.dp)
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
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .background(Color.White, RoundedCornerShape(16.dp))
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
    val textColor = if (isSelected) selectedColor else Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold
        )
    }
}

@Composable
fun AnimatedAlphabetItemReference(
    item: LessonItem,
    index: Int,
    clickCount: Int,
    onClick: () -> Unit
) {
    val state = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(tween(400, (index % 12) * 30)) + scaleIn(tween(400, (index % 12) * 30), initialScale = 0.9f)
    ) {
        AlphabetItemDesignCard(item, index, clickCount, onClick)
    }
}

@Composable
fun AlphabetTopBar(onNavigateBack: () -> Unit, onOpenPdf: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.25f), CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
        }

        Text(
            text = "Script Guide",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )

        IconButton(
            onClick = onOpenPdf,
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.25f), CircleShape)
        ) {
            Icon(
                Icons.AutoMirrored.Rounded.MenuBook,
                contentDescription = "Open PDF",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AlphabetItemDesignCard(
    item: LessonItem,
    index: Int,
    clickCount: Int,
    onClick: () -> Unit
) {
    val accentColors = listOf(
        Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF59E0B),
        Color(0xFFEC4899), Color(0xFF3B82F6), Color(0xFF8B5CF6)
    )
    val accentColor = accentColors[index % accentColors.size]
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val volumeScale by animateFloatAsState(
        targetValue = if (isPressed) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "volumeScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = accentColor.copy(alpha = 0.25f))
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Volume Icon moved to Top Right (TopEnd in Ltr Provider)
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = null,
                    tint = accentColor.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(16.dp)
                        .graphicsLayer {
                            scaleX = volumeScale
                            scaleY = volumeScale
                        }
                )

                if (clickCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .size(18.dp)
                            .background(accentColor.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = clickCount.toString(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = accentColor
                        )
                    }
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = item.scriptText,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF1E293B)
                        )
                    }
                }
            }
        }
    }
}
