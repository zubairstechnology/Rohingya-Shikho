package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.presentation.ui.components.AnimatedBeautyBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AlphabetCombinationScreen(
    onNavigateBack: () -> Unit,
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val consonants by viewModel.consonants.collectAsState()
    val vowels by viewModel.vowels.collectAsState()
    val scope = rememberCoroutineScope()

    if (consonants.isEmpty() || vowels.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF6366F1), strokeWidth = 6.dp)
        }
        return
    }

    // Display all consonants including dda, ka, na, rra, sha, nga
    val displayConsonants = remember(consonants) { consonants }
    
    // Remain only the five primary vowels as requested
    val mainVowels = remember(vowels) { vowels.take(5) }
    val pagerState = rememberPagerState(pageCount = { mainVowels.size })

    val sectionColors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFFEC4899), // Pink
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B), // Amber
        Color(0xFF3B82F6)  // Blue
    )

    val currentThemeColor by animateColorAsState(
        targetValue = sectionColors[pagerState.currentPage % sectionColors.size],
        animationSpec = tween(1000),
        label = "themeColor"
    )

    var globalScale by remember { mutableFloatStateOf(1f) }
    LaunchedEffect(pagerState.currentPage) {
        globalScale = 1f
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBeautyBackground(currentThemeColor)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Alphabet Combination",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                "Page ${pagerState.currentPage + 1} of 5",
                                style = MaterialTheme.typography.labelSmall,
                                color = currentThemeColor.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(44.dp)
                                .shadow(4.dp, CircleShape)
                                .background(Color.White, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF1E293B))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.White.copy(alpha = 0.85f)
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Luxury Vowel Selection Bar (5 Vowels)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 24.dp)
                        .height(72.dp)
                        .shadow(16.dp, RoundedCornerShape(36.dp)),
                    color = Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(36.dp)
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                        edgePadding = 16.dp,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            if (pagerState.currentPage < tabPositions.size) {
                                Box(
                                    Modifier
                                        .tabIndicatorOffset(tabPositions[pagerState.currentPage])
                                        .fillMaxHeight()
                                        .padding(horizontal = 6.dp, vertical = 8.dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(currentThemeColor.copy(alpha = 0.15f))
                                        .border(2.5.dp, currentThemeColor, RoundedCornerShape(28.dp))
                                )
                            }
                        }
                    ) {
                        mainVowels.forEachIndexed { index, vowel ->
                            val isSelected = pagerState.currentPage == index
                            Tab(
                                selected = isSelected,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                text = {
                                    val cleanVowelText = vowel.scriptText
                                        .replace("◌", "")
                                        .replace("𐴀", "")
                                        .replace("𐴢", "")
                                    Text(
                                        text = cleanVowelText,
                                        fontSize = if (isSelected) 34.sp else 24.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        color = if (isSelected) currentThemeColor else Color(0xFF94A3B8)
                                    )
                                }
                            )
                        }
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top,
                    pageSpacing = 24.dp,
                    userScrollEnabled = globalScale == 1f,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) { pageIndex ->
                    val currentVowel = mainVowels[pageIndex]
                    val themeColor = sectionColors[pageIndex % sectionColors.size]
                    
                    var offset by remember { mutableStateOf(Offset.Zero) }
                    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
                        globalScale = (globalScale * zoomChange).coerceIn(1f, 4f)
                        offset = if (globalScale > 1f) offset + offsetChange else Offset.Zero
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .transformable(state = state)
                            .graphicsLayer(
                                scaleX = globalScale,
                                scaleY = globalScale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    ) {
                        VowelHeroCard(currentVowel, themeColor)
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp)),
                            color = Color.White.copy(alpha = 0.95f),
                            shadowElevation = 12.dp
                        ) {
                            AnimatedBeautyGrid(displayConsonants, currentVowel, themeColor, viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VowelHeroCard(vowel: LessonItem, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(bottom = 12.dp)
            .shadow(24.dp, RoundedCornerShape(40.dp), spotColor = color)
            .background(
                Brush.linearGradient(
                    colors = listOf(color, color.copy(alpha = 0.85f), color.copy(alpha = 0.7f)),
                    start = Offset.Zero,
                    end = Offset.Infinite
                ),
                RoundedCornerShape(40.dp)
            )
            .clip(RoundedCornerShape(40.dp))
    ) {
        Icon(
            imageVector = Icons.Rounded.Lightbulb,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.2f),
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 40.dp, y = 20.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .border(3.5.dp, Color.White.copy(alpha = 0.45f), CircleShape),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.25f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val cleanVowelText = vowel.scriptText
                            .replace("◌", "")
                            .replace("𐴀", "")
                            .replace("𐴢", "")
                        Text(
                            cleanVowelText,
                            color = Color.White,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Icon(
                    Icons.Rounded.AutoAwesome,
                    null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.align(Alignment.TopEnd).size(22.dp).offset(x = 4.dp, y = (-4).dp)
                )
            }
            
            Spacer(Modifier.width(24.dp))
            
            Column {
                Surface(
                    color = Color.White.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "VOWEL FOCUS",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = vowel.transliteration,
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun AnimatedBeautyGrid(
    consonants: List<LessonItem>,
    vowel: LessonItem,
    themeColor: Color,
    viewModel: AlphabetViewModel
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 180.dp), // Bottom padding for better scroll
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(consonants) { index, consonant ->
                var isVisible by remember { mutableStateOf(false) }
                LaunchedEffect(vowel.id) { 
                    isVisible = false
                    delay(5)
                    isVisible = true
                }

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, index * 10)) +
                            scaleIn(tween(600, index * 10), initialScale = 0.9f) +
                            slideInVertically(tween(600, index * 10)) { it / 4 }
                ) {
                    ModernLuxuryCombinationCard(consonant, vowel, themeColor) {
                        viewModel.playAudio(consonant)
                    }
                }
            }
        }
    }
}

@Composable
fun ModernLuxuryCombinationCard(
    consonant: LessonItem,
    vowel: LessonItem,
    themeColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val shadowIntensity by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        label = "shadow"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .scale(scale)
            .shadow(
                elevation = shadowIntensity,
                shape = RoundedCornerShape(24.dp),
                spotColor = themeColor.copy(alpha = 0.4f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = themeColor.copy(alpha = 0.15f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val sakinSign = "𐴢"
            val dottedCircle = "◌"
            val carrierA = "𐴀"
            
            // Extract base consonant by removing any existing sakin to avoid double sakin
            val baseConsonant = consonant.scriptText.replace(sakinSign, "").trim()
            
            // Extract clean vowel
            val cleanVowel = vowel.scriptText
                .replace(dottedCircle, "")
                .replace(carrierA, "")
                .replace(sakinSign, "")
                .trim()
            
            Text(
                text = "${baseConsonant}${cleanVowel}",
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B),
                lineHeight = 44.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
