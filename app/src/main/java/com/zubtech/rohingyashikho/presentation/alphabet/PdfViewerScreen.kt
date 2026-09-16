package com.zubtech.rohingyashikho.presentation.alphabet

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PdfViewerScreen(
    onNavigateBack: () -> Unit,
    pdfSource: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var pageCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var retryTrigger by remember { mutableIntStateOf(0) }

    // UI Search State
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Global Zoom State - Reset on page change for "targeted page zoom"
    var globalScale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(pdfSource, retryTrigger) {
        isLoading = true
        error = null
        pdfFile = null
        downloadProgress = 0f
        
        try {
            val file = withContext(Dispatchers.IO) {
                val fileName = "qaida_book_v27_" + pdfSource.hashCode() + ".pdf"
                val f = File(context.cacheDir, fileName)
                
                if (!f.exists() || f.length() < 1024 || !isPdfHeaderCorrect(f)) {
                    f.delete()
                    if (pdfSource.startsWith("http")) {
                        downloadDirectPdfFile(pdfSource, f) { progress ->
                            downloadProgress = progress
                        }
                    } else {
                        context.assets.open(pdfSource).use { input ->
                            FileOutputStream(f).use { output -> input.copyTo(output) }
                        }
                    }
                }
                
                if (!isPdfHeaderCorrect(f)) {
                    f.delete()
                    throw Exception("Document integrity failed. Please try again.")
                }
                f
            }
            
            withContext(Dispatchers.IO) {
                val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(fd)
                pageCount = renderer.pageCount
                renderer.close()
                fd.close()
            }
            pdfFile = file
        } catch (e: Exception) {
            error = e.localizedMessage ?: "Failed to connect to document server"
        } finally {
            isLoading = false
        }
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })

    // Reset zoom when swiping to a new page
    LaunchedEffect(pagerState.currentPage) {
        globalScale = 1f
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                    color = Color.White,
                    shadowElevation = 10.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Back Button - Fixed on Left
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF1E293B))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // Dynamic Center Content (Search or Title)
                        Box(modifier = Modifier.weight(1f)) {
                            AnimatedContent(
                                targetState = isSearchActive,
                                transitionSpec = {
                                    (fadeIn(animationSpec = tween(400)) + expandHorizontally())
                                        .togetherWith(fadeOut(animationSpec = tween(400)) + shrinkHorizontally())
                                }, label = "top_bar_anim"
                            ) { searching ->
                                if (searching) {
                                    TextField(
                                        value = searchQuery,
                                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length < 4) searchQuery = it },
                                        modifier = Modifier.fillMaxWidth().height(52.dp).shadow(2.dp, RoundedCornerShape(26.dp)),
                                        placeholder = { Text("Jump to page...", fontSize = 14.sp) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Go),
                                        keyboardActions = KeyboardActions(onGo = {
                                            val pageNum = searchQuery.toIntOrNull()
                                            if (pageNum != null && pageNum in 1..pageCount) {
                                                scope.launch { pagerState.animateScrollToPage(pageNum - 1) }
                                                isSearchActive = false
                                                searchQuery = ""
                                                focusManager.clearFocus()
                                            }
                                        }),
                                        colors = TextFieldDefaults.colors(
                                            focusedContainerColor = Color(0xFFF1F5F9),
                                            unfocusedContainerColor = Color(0xFFF1F5F9),
                                            focusedIndicatorColor = Color(0xFF6366F1),
                                            unfocusedIndicatorColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(26.dp),
                                        singleLine = true,
                                        trailingIcon = {
                                            IconButton(onClick = { isSearchActive = false; searchQuery = "" }) {
                                                Icon(Icons.Default.Close, null, tint = Color(0xFF94A3B8))
                                            }
                                        },
                                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF6366F1)) }
                                    )
                                } else {
                                    Column {
                                        Text("Rohingya Shikho", fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF1E293B))
                                        if (pageCount > 0) {
                                            Text("Page ${pagerState.currentPage + 1} of $pageCount", fontSize = 12.sp, color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Search Button - Fixed on Right
                        if (!isSearchActive) {
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(
                                onClick = { isSearchActive = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color(0xFF6366F1), CircleShape)
                                    .shadow(4.dp, CircleShape)
                            ) {
                                Icon(Icons.Default.Search, "Search", tint = Color.White)
                            }
                        }
                    }
                }
            },
            bottomBar = {
                if (pdfFile != null) {
                    PdfActionBottomBar(
                        currentPage = pagerState.currentPage,
                        pageCount = pageCount,
                        currentScale = globalScale,
                        onScaleChange = { globalScale = it },
                        onPageJump = { page -> scope.launch { pagerState.animateScrollToPage(page) } }
                    )
                }
            },
            containerColor = Color(0xFFF8FAFC)
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (isLoading) {
                    DynamicLoadingScreen(downloadProgress)
                } else if (error != null) {
                    DynamicErrorScreen(error!!) { retryTrigger++ }
                } else if (pdfFile != null) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
                        pageSpacing = 24.dp,
                        beyondBoundsPageCount = 1,
                        userScrollEnabled = globalScale == 1f
                    ) { pageIndex ->
                        ZoomablePdfPage(
                            file = pdfFile!!,
                            index = pageIndex,
                            scale = if (pagerState.currentPage == pageIndex) globalScale else 1f,
                            onScaleChange = { if (pagerState.currentPage == pageIndex) globalScale = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ZoomablePdfPage(
    file: File,
    index: Int,
    scale: Float,
    onScaleChange: (Float) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    val animatedScale by animateFloatAsState(targetValue = scale, animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow), label = "zoom_anim")
    
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        onScaleChange((scale * zoomChange).coerceIn(1f, 5f))
        if (scale > 1.1f) {
            offset += offsetChange
        } else {
            offset = Offset.Zero
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .transformable(state = state)
            .graphicsLayer(
                scaleX = animatedScale,
                scaleY = animatedScale,
                translationX = offset.x,
                translationY = offset.y
            ),
        contentAlignment = Alignment.Center
    ) {
        HighQualityPdfPage(file, index)
    }
}

@Composable
fun HighQualityPdfPage(file: File, index: Int) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(index) {
        withContext(Dispatchers.IO) {
            try {
                val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                val renderer = PdfRenderer(fd)
                val page = renderer.openPage(index)
                val bmp = Bitmap.createBitmap((page.width * 2.5).toInt(), (page.height * 2.5).toInt(), Bitmap.Config.ARGB_8888)
                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmap = bmp
                page.close(); renderer.close(); fd.close()
            } catch (e: Exception) { Log.e("PdfViewer", "Render error") }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().aspectRatio(0.72f),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                CircularProgressIndicator(color = Color(0xFF6366F1).copy(alpha = 0.2f), strokeWidth = 4.dp)
            }
        }
    }
}

@Composable
fun PdfActionBottomBar(
    currentPage: Int,
    pageCount: Int,
    currentScale: Float,
    onScaleChange: (Float) -> Unit,
    onPageJump: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp)
            .shadow(32.dp, RoundedCornerShape(32.dp)),
        color = Color.White.copy(alpha = 0.98f),
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Zoom Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color(0xFFF8FAFC), RoundedCornerShape(20.dp)).padding(4.dp)
            ) {
                IconButton(
                    onClick = { onScaleChange((currentScale - 0.5f).coerceAtLeast(1f)) },
                    modifier = Modifier.size(40.dp).background(Color.White, CircleShape).shadow(2.dp, CircleShape)
                ) {
                    Icon(Icons.Rounded.ZoomOut, null, modifier = Modifier.size(22.dp), tint = Color(0xFF6366F1))
                }
                
                Text(
                    "${(currentScale * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFF1E293B)
                )
                
                IconButton(
                    onClick = { onScaleChange((currentScale + 0.5f).coerceAtMost(5f)) },
                    modifier = Modifier.size(40.dp).background(Color.White, CircleShape).shadow(2.dp, CircleShape)
                ) {
                    Icon(Icons.Rounded.ZoomIn, null, modifier = Modifier.size(22.dp), tint = Color(0xFF6366F1))
                }
            }

            // Navigation
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color(0xFF6366F1).copy(0.12f), RoundedCornerShape(24.dp)).padding(4.dp)
            ) {
                IconButton(
                    onClick = { onPageJump(currentPage - 1) },
                    enabled = currentPage > 0,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = if(currentPage > 0) Color(0xFF6366F1) else Color(0xFFCBD5E1), modifier = Modifier.size(24.dp))
                }
                
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(horizontal = 6.dp).shadow(1.dp, RoundedCornerShape(16.dp))
                ) {
                    Text(
                        "${currentPage + 1} / $pageCount",
                        modifier = Modifier.padding(horizontal = 6.dp),
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )
                }

                IconButton(
                    onClick = { onPageJump(currentPage + 1) },
                    enabled = currentPage < pageCount - 1,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = if(currentPage < pageCount - 1) Color(0xFF6366F1) else Color(0xFFCBD5E1), modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}

@Composable
fun DynamicLoadingScreen(progress: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Restart), label = "rotation"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(animation = tween(1000, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "pulse"
    )

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.scale(pulse)) {
            // Background Track
            Canvas(modifier = Modifier.size(160.dp)) {
                drawCircle(color = Color(0xFFF1F5F9), radius = size.minDimension / 2, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round))
            }
            // Pulsing Glow
            Surface(modifier = Modifier.size(130.dp).scale(pulse), shape = CircleShape, color = Color(0xFF6366F1).copy(0.05f)) {}
            
            // Circular Progress
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(160.dp).rotate(rotation),
                color = Color(0xFF6366F1),
                strokeWidth = 14.dp,
                strokeCap = StrokeCap.Round
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "${(progress * 100).toInt()}%", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Black, color = Color(0xFF1E293B))
                Text("FETCHING", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6366F1), letterSpacing = 2.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(60.dp))
        Text("Connecting to Library...", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFF1E293B))
        Text("Preparing your books for offline use", fontSize = 15.sp, color = Color(0xFF64748B), modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun DynamicErrorScreen(msg: String, onRetry: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "error_anim")
    val shake by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(animation = tween(500, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "shake"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(180.dp).graphicsLayer(translationX = shake).shadow(24.dp, CircleShape),
            shape = CircleShape,
            color = Color(0xFFFFF1F2)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.SignalWifiOff, null, modifier = Modifier.size(90.dp), tint = Color(0xFFF43F5E))
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Text("Internet Problem", fontWeight = FontWeight.Black, fontSize = 28.sp, color = Color(0xFF1E293B))
        Spacer(modifier = Modifier.height(16.dp))
        Text("We couldn't reach the document server. Please check your data or Wi-Fi.", textAlign = TextAlign.Center, color = Color(0xFF64748B), fontSize = 17.sp, lineHeight = 26.sp)
        
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().height(68.dp).shadow(20.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 12.dp)
        ) {
            Icon(Icons.Rounded.Refresh, null, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Text("Try Again", fontWeight = FontWeight.Black, fontSize = 19.sp)
        }
    }
}

private fun isPdfHeaderCorrect(f: File) = f.exists() && f.inputStream().use { 
    val b = ByteArray(4); it.read(b); String(b) == "%PDF" 
}

private fun downloadDirectPdfFile(url: String, f: File, onProgress: (Float) -> Unit) {
    var cur = url
    for (i in 0..10) {
        val conn = URL(cur).openConnection() as HttpURLConnection
        conn.instanceFollowRedirects = true
        conn.connectTimeout = 30000
        conn.setRequestProperty("User-Agent", "Mozilla/5.0")
        if (conn.responseCode == 200) {
            if (conn.contentType?.contains("html") == true) {
                val h = conn.inputStream.bufferedReader().readText()
                val t = "confirm=([a-zA-Z0-9_\\-]+)".toRegex().find(h)?.groupValues?.get(1)
                if (t != null) { cur = if (cur.contains("?")) "$cur&confirm=$t" else "$cur?confirm=$t"; continue }
            }
            val s = conn.contentLength
            conn.inputStream.use { input -> FileOutputStream(f).use { out ->
                val b = ByteArray(65536); var r: Int; var t = 0
                while (input.read(b).also { r = it } != -1) { 
                    out.write(b, 0, r)
                    t += r
                    if (s > 0) onProgress(t.toFloat() / s) 
                }
            }}
            return
        } else if (conn.responseCode in 300..399) {
            cur = conn.getHeaderField("Location") ?: cur
        }
    }
}
