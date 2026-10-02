package com.zubtech.rohingyashikho.presentation.drawing

import android.graphics.DashPathEffect
import android.graphics.Paint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.presentation.ui.components.AnimatedBeautyBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawingScreen(
    onNavigateBack: () -> Unit,
    viewModel: DrawingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val paths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    val density = LocalDensity.current
    
    val brushStrokeWidth = with(density) { 16.dp.toPx() }

    val themeColor = remember(uiState.item) {
        if (uiState.item?.id?.startsWith("v") == true) Color(0xFF10B981) else Color(0xFF6366F1)
    }

    // Alpha pulsing animation for the dotted guide
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val guideAlpha by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )

    // Clear paths when switching items
    LaunchedEffect(uiState.item?.id) {
        paths.clear()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBeautyBackground(themeColor)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Drawing Studio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                            Text("Trace the character carefully", style = MaterialTheme.typography.labelSmall, color = themeColor.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.padding(8.dp).size(44.dp).shadow(4.dp, CircleShape).background(Color.White, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF1E293B))
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { paths.clear() },
                            modifier = Modifier.padding(8.dp).size(44.dp).shadow(4.dp, CircleShape).background(Color(0xFFFEF2F2), CircleShape)
                        ) {
                            Icon(Icons.Rounded.Delete, "Clear", tint = Color.Red)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = themeColor, strokeWidth = 6.dp)
                }
            } else {
                AnimatedContent(
                    targetState = uiState.item,
                    transitionSpec = {
                        if (targetState?.id != initialState?.id) {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        } else {
                            fadeIn() togetherWith fadeOut()
                        }
                    },
                    label = "StudioTransition"
                ) { item ->
                    item?.let {
                        val displayScript = it.scriptText.replace("𐴢", "").replace("◌", "")

                        Column(
                            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Dynamic Info Card
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).shadow(12.dp, RoundedCornerShape(28.dp)),
                                shape = RoundedCornerShape(28.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f))
                            ) {
                                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Surface(modifier = Modifier.size(60.dp), shape = CircleShape, color = themeColor.copy(alpha = 0.1f)) {
                                        Icon(Icons.Rounded.Brush, null, tint = themeColor, modifier = Modifier.padding(15.dp))
                                    }
                                    Spacer(Modifier.width(20.dp))
                                    Column {
                                        Text(it.transliteration, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                                        Text(it.englishMeaning, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.weight(1f))
                                    Icon(Icons.Rounded.AutoAwesome, null, tint = Color(0xFFFFD700), modifier = Modifier.size(28.dp))
                                }
                            }

                            // Interactive Studio Area
                            Row(modifier = Modifier.weight(1f).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { viewModel.navigateToPrev() },
                                    enabled = uiState.hasPrev,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null, tint = if(uiState.hasPrev) themeColor else Color.Transparent)
                                }

                                Box(
                                    modifier = Modifier.weight(1f).fillMaxHeight().padding(horizontal = 8.dp).shadow(24.dp, RoundedCornerShape(48.dp), spotColor = themeColor).background(Color.White, shape = RoundedCornerShape(48.dp)).border(4.dp, themeColor.copy(alpha = 0.05f), RoundedCornerShape(48.dp))
                                        .pointerInput(uiState.item?.id) {
                                            detectDragGestures(
                                                onDragStart = { offset -> currentPath = Path().apply { moveTo(offset.x, offset.y) } },
                                                onDrag = { change, _ ->
                                                    currentPath?.lineTo(change.position.x, change.position.y)
                                                    val temp = currentPath
                                                    currentPath = null
                                                    currentPath = temp
                                                },
                                                onDragEnd = { currentPath?.let { paths.add(it) }; currentPath = null }
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Dynamic Dotted Guide
                                    Canvas(modifier = Modifier.fillMaxSize().padding(48.dp)) {
                                        drawIntoCanvas { canvas ->
                                            val paint = Paint().apply {
                                                color = themeColor.toArgb()
                                                alpha = guideAlpha.toInt()
                                                textSize = 360.dp.toPx()
                                                textAlign = Paint.Align.CENTER
                                                style = Paint.Style.STROKE
                                                strokeWidth = 6f
                                                pathEffect = DashPathEffect(floatArrayOf(30f, 30f), 0f)
                                                isAntiAlias = true
                                            }
                                            canvas.nativeCanvas.drawText(displayScript, size.width / 2, size.height / 2 + paint.textSize / 3, paint)
                                        }
                                    }

                                    // Drawing Canvas
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        paths.forEach { path ->
                                            drawPath(path = path, color = Color(0xFF1E293B), style = Stroke(width = brushStrokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
                                        }
                                        currentPath?.let { path ->
                                            drawPath(path = path, color = themeColor, style = Stroke(width = brushStrokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = { viewModel.navigateToNext() },
                                    enabled = uiState.hasNext,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = if(uiState.hasNext) themeColor else Color.Transparent)
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Studio Control Bar
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                OutlinedButton(
                                    onClick = { paths.clear() },
                                    modifier = Modifier.weight(1f).height(64.dp),
                                    shape = RoundedCornerShape(24.dp),
                                    border = BorderStroke(2.5.dp, themeColor.copy(alpha = 0.2f))
                                ) {
                                    Icon(Icons.Rounded.History, null)
                                    Spacer(Modifier.width(10.dp))
                                    Text("Reset", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                }
                                
                                Button(
                                    onClick = onNavigateBack,
                                    modifier = Modifier.weight(1.5f).height(64.dp).shadow(12.dp, RoundedCornerShape(24.dp), spotColor = Color(0xFF10B981)),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                                ) {
                                    Icon(Icons.Default.Done, null)
                                    Spacer(Modifier.width(10.dp))
                                    Text("Done", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
