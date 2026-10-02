package com.zubtech.rohingyashikho.presentation.alphabet

import android.graphics.DashPathEffect
import android.graphics.Paint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity
import com.zubtech.rohingyashikho.domain.model.LessonItem
import kotlin.math.sin

@Composable
fun WritingPracticeScreen(
    onNavigateBack: () -> Unit,
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val writingItems by viewModel.consonantsForWriting.collectAsState()
    val vowelItems by viewModel.vowels.collectAsState()
    val userStats by viewModel.userStats.collectAsState()

    var selectedMode by remember { mutableStateOf("Consonant") }
    var selectedItem by remember { mutableStateOf<LessonItem?>(null) }

    val displayItems = remember(writingItems, vowelItems, selectedMode) {
        if (selectedMode == "Vowels") vowelItems else writingItems
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F766E),
                        Color(0xFF115E59),
                        Color(0xFF134E4A)
                    )
                )
            )
    ) {
        DynamicWritingBackgroundOrbs()

        if (selectedItem == null) {
            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    WritingTopBar(onNavigateBack)
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    WritingProgressCard(userStats)

                    WritingModeSwitcher(
                        selectedMode = selectedMode,
                        onModeSelected = { selectedMode = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp)),
                        color = Color(0xFFF8FAFC)
                    ) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 110.dp, start = 20.dp, end = 20.dp, top = 26.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                itemsIndexed(displayItems) { index, item ->
                                    AnimatedWritingItem(
                                        item = item,
                                        index = index,
                                        onClick = {
                                            viewModel.playAudio(item)
                                            selectedItem = item
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            selectedItem?.let { item ->
                FullDrawingMode(
                    item = item,
                    onClose = { selectedItem = null },
                    onPlayAudio = { viewModel.playAudio(item) }
                )
            }
        }
    }
}

@Composable
fun FullDrawingMode(
    item: LessonItem,
    onClose: () -> Unit,
    onPlayAudio: () -> Unit
) {
    val paths = remember { mutableStateListOf<Pair<Path, Color>>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var currentPathColor by remember { mutableStateOf(Color(0xFF10B981)) }
    var startX by remember { mutableStateOf(0f) }
    
    val density = LocalDensity.current
    val brushStrokeWidth = with(density) { 14.dp.toPx() }
    
    val displayScript = item.scriptText.replace("𐴢", "").replace("◌", "")
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val guideAlpha by infiniteTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // Drawing Canvas (Truly Full Screen)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(item.id) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            startX = offset.x
                            currentPathColor = Color(0xFF10B981) // Green for correct direction
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                        },
                        onDrag = { change, _ ->
                            currentPath?.lineTo(change.position.x, change.position.y)
                            // Rohingya Hanifi script is written Right-to-Left.
                            // If drawing moves significantly from left to right, mark as incorrect direction.
                            if (change.position.x - startX > 24f) {
                                currentPathColor = Color(0xFFEF4444) // Red for incorrect direction
                            }
                            val temp = currentPath
                            currentPath = null
                            currentPath = temp
                        },
                        onDragEnd = {
                            currentPath?.let { paths.add(Pair(it, currentPathColor)) }
                            currentPath = null
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            // Dotted Guide Layer
            Canvas(modifier = Modifier.fillMaxSize().padding(40.dp)) {
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = android.graphics.Color.parseColor("#0F766E")
                        alpha = (guideAlpha * 255).toInt()
                        textSize = 420.dp.toPx()
                        textAlign = Paint.Align.CENTER
                        style = Paint.Style.STROKE
                        strokeWidth = 5f
                        pathEffect = DashPathEffect(floatArrayOf(40f, 40f), 0f)
                        isAntiAlias = true
                    }
                    canvas.nativeCanvas.drawText(displayScript, size.width / 2, size.height / 2 + paint.textSize / 3, paint)
                }
            }

            // User Drawing Layer
            Canvas(modifier = Modifier.fillMaxSize()) {
                paths.forEach { (path, color) ->
                    drawPath(path = path, color = color, style = Stroke(width = brushStrokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
                currentPath?.let { path ->
                    drawPath(path = path, color = currentPathColor, style = Stroke(width = brushStrokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
                }
            }
        }

        // Header with Back and Delete button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(52.dp).shadow(6.dp, CircleShape).background(Color.White, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Color(0xFF0F766E))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(item.transliteration, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF0F766E))
                Text("Trace Letter", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }

            IconButton(
                onClick = { paths.clear() },
                modifier = Modifier.size(52.dp).shadow(6.dp, CircleShape).background(Color(0xFFFEF2F2), CircleShape)
            ) {
                Icon(Icons.Rounded.Delete, "Clear", tint = Color.Red)
            }
        }

        // Bottom Floating Audio Button
        FloatingActionButton(
            onClick = onPlayAudio,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(32.dp)
                .size(64.dp),
            containerColor = Color(0xFF0F766E),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun DynamicWritingBackgroundOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val width = size.width
                val height = size.height
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF0D9488).copy(alpha = 0.24f), Color.Transparent),
                    ),
                    radius = width * 0.65f,
                    center = Offset(
                        x = width * 0.25f + sin(waveOffset) * 80f,
                        y = height * 0.15f + sin(waveOffset + 1.2f) * 60f
                    )
                )

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF14B8A6).copy(alpha = 0.18f), Color.Transparent),
                    ),
                    radius = width * 0.55f,
                    center = Offset(
                        x = width * 0.75f + sin(waveOffset + 2.4f) * 70f,
                        y = height * 0.35f + sin(waveOffset) * 90f
                    )
                )
            }
    )
}

@Composable
fun WritingTopBar(onNavigateBack: () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 22.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(44.dp)
                    .shadow(4.dp, CircleShape)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color(0xFF1E293B),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "INTERMEDIATE STAGE", 
                    fontSize = 12.sp, 
                    fontWeight = FontWeight.Black, 
                    color = Color(0xFFFFD700), 
                    letterSpacing = 2.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    "Writing Practice", 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = Color.White,
                    letterSpacing = 0.25.sp
                )
            }

            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
fun WritingModeSwitcher(
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp)
            .height(52.dp)
            .background(Color.White.copy(alpha = 0.14f), RoundedCornerShape(26.dp))
            .padding(4.dp)
    ) {
        val bias by animateFloatAsState(
            targetValue = if (selectedMode == "Letters") -1f else 1f,
            animationSpec = spring(dampingRatio = 0.84f, stiffness = Spring.StiffnessLow),
            label = "bias"
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(BiasAlignment(horizontalBias = bias, verticalBias = 0f))
                .shadow(6.dp, RoundedCornerShape(22.dp))
                .background(Brush.verticalGradient(listOf(Color.White, Color(0xFFF8FAFC))), RoundedCornerShape(22.dp))
        )
        Row(modifier = Modifier.fillMaxSize()) {
            WritingModeTab("Letters", selectedMode == "Letters") { onModeSelected("Letters") }
            WritingModeTab("Vowels", selectedMode == "Vowels") { onModeSelected("Vowels") }
        }
    }
}

@Composable
fun RowScope.WritingModeTab(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clip(RoundedCornerShape(22.dp))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label, 
            color = if (isSelected) Color(0xFF0F766E) else Color.White.copy(alpha = 0.85f), 
            fontWeight = FontWeight.Bold, 
            fontSize = 14.sp
        )
    }
}

@Composable
fun AnimatedWritingItem(item: LessonItem, index: Int, onClick: () -> Unit) {
    val state = remember { MutableTransitionState(false).apply { targetState = true } }
    AnimatedVisibility(
        visibleState = state,
        enter = fadeIn(tween(350, (index % 12) * 40)) + scaleIn(tween(350, (index % 12) * 40), initialScale = 0.82f)
    ) {
        WritingDisplayItem(item, index, onClick)
    }
}

@Composable
fun WritingDisplayItem(item: LessonItem, index: Int, onClick: () -> Unit) {
    val accentColors = listOf(Color(0xFF0D9488), Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFFEC4899), Color(0xFF3B82F6), Color(0xFF06B6D4))
    val accentColor = accentColors[index % accentColors.size]
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.90f else 1f, animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium), label = "scale")

    val volumeScale by animateFloatAsState(
        targetValue = if (isPressed) 1.2f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow),
        label = "volumeScale"
    )

    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.88f).graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interactionSource, indication = null) { onClick() },
        shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize().background(Brush.radialGradient(colors = listOf(accentColor.copy(alpha = 0.08f), Color.Transparent), center = Offset.Zero)))
            
            // Speaking Icon moved to Top Right corner with animation (TopStart in RTL)
            Icon(
                Icons.AutoMirrored.Rounded.VolumeUp, 
                null, 
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .size(16.dp)
                    .graphicsLayer {
                        scaleX = volumeScale
                        scaleY = volumeScale
                    }, 
                tint = accentColor.copy(alpha = 0.6f)
            )

            Column(modifier = Modifier.align(Alignment.Center).padding(horizontal = 6.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(text = item.scriptText, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.transliteration, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = accentColor, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun WritingProgressCard(userStats: UserStatsEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).shadow(10.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF0F766E).copy(alpha = 0.3f)), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(22.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(88.dp)) {
                val total = (userStats.consonantProgress + userStats.vowelProgress) / 2f
                CircularProgressIndicator(progress = { total }, modifier = Modifier.fillMaxSize(), strokeWidth = 8.dp, color = Color(0xFF0D9488), trackColor = Color(0xFFF1F5F9), strokeCap = StrokeCap.Round)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${(total * 100).toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                    Text("PRACTICED", fontSize = 8.sp, color = Color(0xFF0D9488), fontWeight = FontWeight.Black)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(start = 18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(140.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF0D9488)))
                    Spacer(Modifier.width(8.dp))
                    Text("Consonants", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                    Spacer(Modifier.weight(1f))
                    Text("${(userStats.consonantProgress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF0D9488))
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(140.dp)) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                    Spacer(Modifier.width(8.dp))
                    Text("Vowels", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF475569))
                    Spacer(Modifier.weight(1f))
                    Text("${(userStats.vowelProgress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color(0xFF10B981))
                }
            }
        }
    }
}
