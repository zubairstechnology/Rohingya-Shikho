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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
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
import kotlin.math.absoluteValue

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

    // Global Zoom State
    var globalScale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(pdfSource, retryTrigger) {
        isLoading = true
        error = null
        pdfFile = null
        downloadProgress = 0f
        
        try {
            val file = withContext(Dispatchers.IO) {
                // Extract Google Drive ID and create direct download link
                val directUrl = if (pdfSource.contains("drive.google.com")) {
                    val id = when {
                        pdfSource.contains("id=") -> pdfSource.substringAfter("id=").substringBefore("&")
                        pdfSource.contains("/file/d/") -> pdfSource.substringAfter("/file/d/").substringBefore("/")
                        pdfSource.contains("/d/") -> pdfSource.substringAfter("/d/").substringBefore("/")
                        else -> ""
                    }
                    if (id.isNotEmpty()) "https://drive.google.com/uc?export=download&id=$id" else pdfSource
                } else pdfSource

                val fileName = "book_v32_" + directUrl.hashCode() + ".pdf"
                val f = File(context.cacheDir, fileName)
                
                if (!f.exists() || f.length() < 1024 || !isPdfHeaderCorrect(f)) {
                    f.delete()
                    if (directUrl.startsWith("http")) {
                        downloadDirectPdfFile(directUrl, f) { progress ->
                            downloadProgress = progress
                        }
                    } else {
                        context.assets.open(directUrl).use { input ->
                            FileOutputStream(f).use { output -> input.copyTo(output) }
                        }
                    }
                }
                
                if (!isPdfHeaderCorrect(f)) {
                    f.delete()
                    throw Exception("Integrity check failed. Please check internet and try again.")
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
            error = e.localizedMessage ?: "Failed to open document"
        } finally {
            isLoading = false
        }
    }

    val pagerState = rememberPagerState(pageCount = { pageCount })

    LaunchedEffect(pagerState.currentPage) {
        globalScale = 1f
    }

    // Force RTL for Hanifi Book Style
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(68.dp)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Back Arrow on the Right
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.size(42.dp).background(Color(0xFFF1F5F9), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color(0xFF1E293B))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // 2. Search Icon next to Back Arrow on the Right
                        if (!isSearchActive) {
                            IconButton(
                                onClick = { isSearchActive = true },
                                modifier = Modifier.size(42.dp).background(Color(0xFF6366F1).copy(0.1f), CircleShape)
                            ) {
                                Icon(Icons.Default.Search, "Search", tint = Color(0xFF6366F1))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        // 3. Center/Left Area
                        Box(modifier = Modifier.weight(1f)) {
                            if (isSearchActive) {
                                // Search Input with Left-to-Right layout for digits/hint
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    TextField(
                                        value = searchQuery,
                                        onValueChange = { if (it.all { c -> c.isDigit() }) searchQuery = it },
                                        modifier = Modifier.fillMaxWidth().height(50.dp),
                                        placeholder = { Text("Go to page...", fontSize = 14.sp) },
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
                                        shape = RoundedCornerShape(25.dp),
                                        singleLine = true,
                                        trailingIcon = {
                                            IconButton(onClick = { isSearchActive = false }) { 
                                                Icon(Icons.Default.Close, null, tint = Color(0xFF94A3B8)) 
                                            }
                                        }
                                    )
                                }
                            } else {
                                // Standard Title Info (Aligned Right in RTL)
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Rohingya Shikho", fontWeight = FontWeight.Black, fontSize = 19.sp, color = Color(0xFF1E293B))
                                    if (pageCount > 0) {
                                        Text("Page ${pagerState.currentPage + 1} of $pageCount", fontSize = 12.sp, color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                if (pdfFile != null) {
                    PdfReaderControls(
                        currentPage = pagerState.currentPage,
                        pageCount = pageCount,
                        currentScale = globalScale,
                        onScaleChange = { globalScale = it },
                        onPageJump = { page -> scope.launch { pagerState.animateScrollToPage(page) } }
                    )
                }
            },
            containerColor = Color(0xFFF1F5F9)
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                if (isLoading) {
                    PdfBookLoading(downloadProgress)
                } else if (error != null) {
                    PdfBookError(error!!) { retryTrigger++ }
                } else if (pdfFile != null) {
                    // Realistic Book Flip Horizontal Pager
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        beyondBoundsPageCount = 2,
                        userScrollEnabled = globalScale == 1f,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        pageSpacing = 0.dp
                    ) { pageIndex ->
                        val pageOffset = ((pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction)
                        
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    cameraDistance = 14f * density
                                    val pivotX = if (pageOffset > 0f) 1f else 0f
                                    transformOrigin = TransformOrigin(pivotX, 0.5f)
                                    rotationY = -115f * pageOffset.coerceIn(-1f, 1f)
                                    alpha = (1f - pageOffset.absoluteValue * 0.45f).coerceIn(0.6f, 1f)
                                    val sc = (1f - pageOffset.absoluteValue * 0.12f).coerceIn(0.88f, 1f)
                                    scaleX = sc
                                    scaleY = sc
                                }
                                .drawBehind {
                                    if (pageOffset.absoluteValue > 0) {
                                        val shadowWidth = 35.dp.toPx()
                                        val shadowAlpha = (pageOffset.absoluteValue * 0.35f).coerceIn(0f, 0.35f)
                                        val brush = if (pageOffset > 0) {
                                            Brush.horizontalGradient(
                                                colors = listOf(Color.Black.copy(alpha = shadowAlpha), Color.Transparent),
                                                startX = size.width - shadowWidth,
                                                endX = size.width
                                            )
                                        } else {
                                            Brush.horizontalGradient(
                                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = shadowAlpha)),
                                                startX = 0f,
                                                endX = shadowWidth
                                            )
                                        }
                                        drawRect(brush)
                                    }
                                }
                        ) {
                            PdfPageZoomable(
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
}

@Composable
fun PdfPageZoomable(
    file: File,
    index: Int,
    scale: Float,
    onScaleChange: (Float) -> Unit
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    val animatedScale by animateFloatAsState(targetValue = scale, animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow), label = "zoom")
    
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
        PdfPageCard(file, index)
    }
}

@Composable
fun PdfPageCard(file: File, index: Int) {
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
        modifier = Modifier.fillMaxWidth().aspectRatio(0.707f).shadow(15.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
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
                CircularProgressIndicator(color = Color(0xFF6366F1).copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
fun PdfReaderControls(
    currentPage: Int,
    pageCount: Int,
    currentScale: Float,
    onScaleChange: (Float) -> Unit,
    onPageJump: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .shadow(24.dp, RoundedCornerShape(28.dp)),
        color = Color.White.copy(alpha = 0.98f),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Zoom Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color(0xFFF8FAFC), RoundedCornerShape(20.dp)).padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                IconButton(onClick = { onScaleChange((currentScale - 0.5f).coerceAtLeast(1f)) }) {
                    Icon(Icons.Default.ZoomOut, null, tint = Color(0xFF6366F1))
                }
                Text("${(currentScale * 100).toInt()}%", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color(0xFF1E293B))
                IconButton(onClick = { onScaleChange((currentScale + 0.5f).coerceAtMost(5f)) }) {
                    Icon(Icons.Default.ZoomIn, null, tint = Color(0xFF6366F1))
                }
            }

            // Page Pager
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color(0xFF6366F1).copy(0.08f), RoundedCornerShape(20.dp)).padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                IconButton(onClick = { onPageJump(currentPage - 1) }, enabled = currentPage > 0) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = if(currentPage > 0) Color(0xFF6366F1) else Color.LightGray)
                }
                Text("${currentPage + 1} / $pageCount", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Color(0xFF1E293B))
                IconButton(onClick = { onPageJump(currentPage + 1) }, enabled = currentPage < pageCount - 1) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = if(currentPage < pageCount - 1) Color(0xFF6366F1) else Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun PdfBookLoading(progress: Float) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(130.dp),
            color = Color(0xFF6366F1),
            strokeWidth = 12.dp,
            strokeCap = StrokeCap.Round
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text("Preparing Your Book...", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color(0xFF1E293B))
        Text("${(progress * 100).toInt()}% Synchronized", color = Color.Gray, fontSize = 15.sp)
    }
}

@Composable
fun PdfBookError(msg: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Default.CloudOff, null, modifier = Modifier.size(90.dp), tint = Color(0xFFEF4444))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Library Offline", fontWeight = FontWeight.Black, fontSize = 26.sp, color = Color(0xFF1E293B))
        Text(msg, textAlign = TextAlign.Center, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onRetry, 
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1))
        ) {
            Text("Try Reconnecting", fontWeight = FontWeight.Bold, fontSize = 17.sp)
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
        conn.connectTimeout = 40000
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        if (conn.responseCode == 200) {
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
