package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.presentation.ui.theme.AppBackground
import com.zubtech.rohingyashikho.presentation.ui.theme.AppPrimary
import com.zubtech.rohingyashikho.presentation.ui.theme.AppTextSecondary

@Composable
fun AlphabetReferenceScreen(
    onNavigateBack: () -> Unit,
    onItemClick: (String) -> Unit,
    onOpenPdf: () -> Unit,
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    Crossfade(
        targetState = selectedCategory,
        animationSpec = tween(600, easing = EaseInOutQuart),
        label = "category_transition"
    ) { category ->
        if (category == null) {
            AlphabetSelectionContent(
                onNavigateBack = onNavigateBack,
                onCategoryClick = { selectedCategory = it },
                onOpenPdf = onOpenPdf
            )
        } else {
            AlphabetGridContent(
                title = category,
                onBack = { selectedCategory = null },
                viewModel = viewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetSelectionContent(
    onNavigateBack: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onOpenPdf: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rohingya Shikho", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenPdf) {
                        Icon(
                            Icons.Rounded.MenuBook, 
                            contentDescription = "Open Qaida PDF",
                            tint = AppPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
            )
        },
        containerColor = AppBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }
            
            item {
                AnimatedAlphabetCard(
                    title = "Hanifi Alphabet",
                    subtitle = "Consonants, Vowels & Numbers",
                    icon = Icons.Rounded.MenuBook,
                    color = Color(0xFFE8F5E9),
                    delay = 0,
                    onClick = { onCategoryClick("Hanifi Alphabet") }
                )
            }
            
            item {
                AnimatedAlphabetCard(
                    title = "Arabic Script",
                    subtitle = "Traditional Rohingya writing",
                    icon = Icons.Rounded.Translate,
                    color = Color(0xFFFFF3E0),
                    delay = 100,
                    onClick = { onCategoryClick("Arabic Script") }
                )
            }
            
            item {
                AnimatedAlphabetCard(
                    title = "Latin Rohingya",
                    subtitle = "Romanized script system",
                    icon = Icons.Rounded.Abc,
                    color = Color(0xFFE3F2FD),
                    delay = 200,
                    onClick = { onCategoryClick("Latin Rohingya") }
                )
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun AnimatedAlphabetCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    delay: Int,
    onClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(600, delay)) + slideInHorizontally(tween(600, delay)) { -40 }
    ) {
        AlphabetSystemCard(title, subtitle, icon, color, onClick)
    }
}

@Composable
fun AlphabetSystemCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
                Text(text = subtitle, fontSize = 14.sp, color = AppTextSecondary, fontWeight = FontWeight.Medium)
            }
            
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.Black.copy(alpha = 0.7f), modifier = Modifier.size(28.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlphabetGridContent(
    title: String,
    onBack: () -> Unit,
    viewModel: AlphabetViewModel
) {
    val alphabetItems by viewModel.alphabetItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.playAlphabetSong() }) {
                        Icon(Icons.Rounded.PlayCircle, contentDescription = "Play All", tint = AppPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppBackground)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // CONSONANTS SECTION
            item(span = { GridItemSpan(4) }) {
                RibbonHeader(title = "𐴇𐴝𐴕𐴞𐴉𐴞 𐴇𐴠𐴌𐴉𐴠")
            }
            
            itemsIndexed(alphabetItems) { index, item ->
                AnimatedLetterCard(item = item, index = index, onClick = { viewModel.playAudio(item) })
            }

            // VOWELS SECTION
            item(span = { GridItemSpan(4) }) {
                BoxHeader(title = "𐴇𐴝𐴌𐴝𐴑𐴝𐴕")
            }
            
            itemsIndexed(alphabetItems.take(5)) { index, item ->
                AnimatedLetterCard(item = item, index = index + 100, onClick = { viewModel.playAudio(item) })
            }

            // NUMBERS SECTION
            item(span = { GridItemSpan(4) }) {
                BrushHeader(title = "𐴀𐴝𐴕𐴑𐴡 𐴇𐴠𐴌𐴉𐴠")
            }
            
            items(10) { index ->
                val numberItem = LessonItem(
                    id = "num_$index",
                    scriptText = "${10 - index}", // Mock script
                    transliteration = "${10 - index}",
                    englishMeaning = "",
                    audioFileRef = ""
                )
                AnimatedLetterCard(
                    item = numberItem,
                    index = index + 200,
                    onClick = { viewModel.playAudio(numberItem) }
                )
            }
        }
    }
}

@Composable
fun AnimatedLetterCard(
    item: LessonItem,
    index: Int,
    onClick: () -> Unit
) {
    val state = remember { MutableTransitionState(false).apply { targetState = true } }
    val delay = (index % 4) * 100 + (index / 4) * 50
    
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(tween(500, delay)) + scaleIn(tween(500, delay), initialScale = 0.8f)
    ) {
        LetterCard(item = item, onClick = onClick)
    }
}

@Composable
fun RibbonHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer(rotationZ = -2f)
                .background(AppPrimary, RoundedCornerShape(4.dp))
                .padding(horizontal = 40.dp, vertical = 10.dp)
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }
    }
}

@Composable
fun BoxHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .border(BorderStroke(2.dp, Color.Black), RoundedCornerShape(8.dp))
                .padding(4.dp)
                .background(Color.White, RoundedCornerShape(4.dp))
                .padding(horizontal = 32.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun BrushHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFFB300))))
                .padding(horizontal = 36.dp, vertical = 6.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun LetterCard(
    item: LessonItem,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.94f else 1f, label = "press_scale")

    Card(
        modifier = Modifier
            .aspectRatio(0.9f)
            .scale(scale)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.transliteration,
                fontSize = 12.sp,
                color = AppPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
            
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.scriptText,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
