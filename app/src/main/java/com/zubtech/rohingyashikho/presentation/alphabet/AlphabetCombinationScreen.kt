package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.presentation.ui.components.AnimatedBeautyBackground
import com.zubtech.rohingyashikho.presentation.ui.components.GrammarPulseIndicator
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
    // Important: Collect combinations so the flow is active and ViewModel can access loaded data
    val combinations by viewModel.combinations.collectAsState()
    val scope = rememberCoroutineScope()

    if (consonants.isEmpty() || vowels.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF6366F1), strokeWidth = 6.dp)
        }
        return
    }

    val displayConsonants = remember(consonants) { consonants }
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
                // Luxury Vowel Selection Bar
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
    val playingItemId by viewModel.playingItemId.collectAsState()
    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(start = 24.dp, top = 32.dp, end = 24.dp, bottom = 40.dp),
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

                val combinationId = "${consonant.id}_${vowel.id}"
                val isItemPlaying = isAudioPlaying && playingItemId == combinationId

                AnimatedVisibility(
                    visible = isVisible,
                    enter = fadeIn(tween(600, index * 10)) +
                            scaleIn(tween(600, index * 10), initialScale = 0.9f) +
                            slideInVertically(tween(600, index * 10)) { it / 4 }
                ) {
                    ModernLuxuryCombinationCard(
                        consonant = consonant,
                        vowel = vowel,
                        index = index,
                        themeColor = themeColor,
                        isPlaying = isItemPlaying
                    ) {
                        viewModel.playCombinationAudio(consonant, vowel)
                    }
                }
            }

            // Interactive Example Section integrated into the scrollable grid
            item(span = { GridItemSpan(4) }) {
                InteractiveExampleSection(
                    item = vowel,
                    themeColor = themeColor,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun ModernLuxuryCombinationCard(
    consonant: LessonItem,
    vowel: LessonItem,
    index: Int,
    themeColor: Color,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Luxury gradients for individual cells
    val gradients = remember {
        listOf(
            listOf(Color(0xFF6366F1), Color(0xFFA855F7)), // Indigo-Purple
            listOf(Color(0xFFEC4899), Color(0xFFF43F5E)), // Pink-Rose
            listOf(Color(0xFF10B981), Color(0xFF059669)), // Emerald-Green
            listOf(Color(0xFFF59E0B), Color(0xFFD97706)), // Amber-Orange
            listOf(Color(0xFF3B82F6), Color(0xFF2563EB)), // Blue-Indigo
            listOf(Color(0xFF8B5CF6), Color(0xFF7C3AED)), // Violet-Purple
            listOf(Color(0xFFF97316), Color(0xFFEA580C)), // Orange-Red
            listOf(Color(0xFF06B6D4), Color(0xFF0891B2)), // Cyan-Teal
            listOf(Color(0xFF14B8A6), Color(0xFF0D9488)), // Teal-Green
            listOf(Color(0xFFEF4444), Color(0xFFDC2626))  // Red-DarkRed
        )
    }
    val currentGradient = gradients[index % gradients.size]
    
    // Shining animation ONLY when pressed
    val infiniteTransition = rememberInfiniteTransition(label = "shining")
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = 450f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shineOffset"
    )

    // Interaction states - removed isPlaying to prevent "shrunken/held" visual during audio
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val shadowIntensity by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        label = "shadow"
    )
    
    // Border brush logic: white shine ONLY on press. Bold static color when playing.
    val borderBrush = when {
        isPressed -> {
            Brush.linearGradient(
                colors = listOf(
                    currentGradient[0],
                    currentGradient[1],
                    Color.White.copy(alpha = 0.9f),
                    currentGradient[0],
                    currentGradient[1]
                ),
                start = Offset(shineOffset, shineOffset),
                end = Offset(shineOffset + 100f, shineOffset + 100f)
            )
        }
        isPlaying -> {
            // Bold solid gradient while playing - removed shifting shine and transparency
            Brush.linearGradient(
                colors = currentGradient,
                start = Offset.Zero,
                end = Offset.Infinite
            )
        }
        else -> {
            // Subtle static gradient for idle state
            Brush.linearGradient(
                colors = currentGradient.map { it.copy(alpha = 0.7f) },
                start = Offset.Zero,
                end = Offset.Infinite
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .scale(scale)
            .shadow(
                elevation = shadowIntensity,
                shape = RoundedCornerShape(24.dp),
                spotColor = currentGradient[0].copy(alpha = if (isPlaying) 0.6f else 0.4f)
            )
            .border(
                width = if (isPressed || isPlaying) 4.dp else 1.5.dp,
                brush = borderBrush,
                shape = RoundedCornerShape(24.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val sakinSign = "𐴢"
                val dottedCircle = "◌"
                val carrierA = "𐴀"
                
                val baseConsonant = consonant.scriptText.replace(sakinSign, "").trim()
                val cleanVowel = vowel.scriptText
                    .replace(dottedCircle, "")
                    .replace(carrierA, "")
                    .replace(sakinSign, "")
                    .trim()
                
                Text(
                    text = "${baseConsonant}${cleanVowel}",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isPlaying) currentGradient[0] else Color(0xFF1E293B),
                    lineHeight = 44.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun InteractiveExampleSection(
    item: LessonItem,
    themeColor: Color,
    viewModel: AlphabetViewModel
) {
    val isPlaying by viewModel.isAudioPlaying.collectAsState()
    val playingId by viewModel.playingItemId.collectAsState()
    val isItemPlaying = isPlaying && playingId == item.id

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 40.dp)
        ) {
            // Header exactly like Grammar Screen
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(themeColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.AutoAwesome, null, tint = themeColor, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(
                        text = "INTERACTIVE EXAMPLE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = themeColor,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = "Real-world usage in context",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Premium Card styled like Grammar Example Carousel Items
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(36.dp),
                        spotColor = themeColor.copy(alpha = 0.5f)
                    ),
                shape = RoundedCornerShape(36.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(
                    width = 2.5.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(themeColor, Color(0xFFFFD700), Color(0xFF00E5FF))
                    )
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Top Section: Illustration & Focus
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = themeColor.copy(alpha = 0.1f)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.graphic1),
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "CASE STUDY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF94A3B8),
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Character Practice",
                                fontSize = 11.sp,
                                color = themeColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        
                        // English TTS Button
                        Surface(
                            color = themeColor.copy(alpha = 0.08f),
                            shape = CircleShape,
                            onClick = { viewModel.speakEnglish(item.englishMeaning) }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.RecordVoiceOver,
                                null,
                                tint = themeColor,
                                modifier = Modifier.padding(10.dp).size(22.dp)
                            )
                        }
                    }

                    // English Section
                    Column {
                        Text(
                            text = "ENGLISH MEANING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = item.englishMeaning,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B),
                            lineHeight = 30.sp
                        )
                    }

                    // Artistic Divider
                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color(0xFFF1F5F9)))

                    // Rohingya Section with Pronunciation
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.HistoryEdu, null, tint = themeColor.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "ROHINGYA (HANIFI)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = themeColor
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        ) {
                            // Audio Button with Pulse
                            Surface(
                                color = if (isItemPlaying) Color(0xFFFFD700) else themeColor.copy(alpha = 0.08f),
                                shape = CircleShape,
                                onClick = { viewModel.playAudio(item) }
                            ) {
                                Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                                    if (isItemPlaying) {
                                        GrammarPulseIndicator(color = themeColor, modifier = Modifier.size(44.dp))
                                    }
                                    Icon(
                                        imageVector = if (isItemPlaying) Icons.Rounded.MusicNote else Icons.AutoMirrored.Rounded.VolumeUp,
                                        null,
                                        tint = if (isItemPlaying) Color.White else themeColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            
                            Spacer(Modifier.width(16.dp))
                            
                            Text(
                                text = item.scriptText,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}
