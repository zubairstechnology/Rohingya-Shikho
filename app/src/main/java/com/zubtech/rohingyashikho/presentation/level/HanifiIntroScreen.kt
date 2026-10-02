package com.zubtech.rohingyashikho.presentation.level

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zubtech.rohingyashikho.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HanifiIntroScreen(
    onNavigateBack: () -> Unit,
    onStartLearning: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val coroutineScope = rememberCoroutineScope()

    val slideGradients = listOf(
        Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFFEC4899))), // Purple (History)
        Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF3B82F6))), // Green (Direction)
        Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFEF4444))), // Amber (Unicode)
        Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFA855F7)))  // Indigo (Creator)
    )

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp, bottom = 100.dp)
            ) { page ->
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .shadow(24.dp, RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.08f)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(32.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(4.dp, slideGradients[page % slideGradients.size])
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                    ) {
                        when (page) {
                            0 -> HistorySlide()
                            1 -> DirectionSlide()
                            2 -> UnicodeSlide()
                            3 -> CreatorSlide()
                        }
                    }
                }
            }

            // Top Overlay (Back Navigation) - Restored with Gradient Outline
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(20.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .border(
                            width = 2.5.dp,
                            brush = Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFFF00CC))),
                            shape = CircleShape
                        )
                ) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Color(0xFF1E293B))
                }
            }

            // Bottom Navigation Overlay
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 28.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val active = pagerState.currentPage == index
                        val dotWidth by animateDpAsState(
                            targetValue = if (active) 24.dp else 8.dp,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                            label = "dotWidth"
                        )
                        val dotColor by animateColorAsState(
                            targetValue = if (active) Color(0xFF2563EB) else Color(0xFFCBD5E1),
                            label = "dotColor"
                        )
                        Box(
                            modifier = Modifier
                                .size(height = 8.dp, width = dotWidth)
                                .clip(CircleShape)
                                .background(dotColor)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < 3) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onStartLearning()
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (pagerState.currentPage < 3) "Continue" else "Start Learning",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Icon(
                            imageVector = if (pagerState.currentPage < 3) Icons.AutoMirrored.Rounded.ArrowForward else Icons.Rounded.School,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CreatorSlide() {
    val infiniteTransition = rememberInfiniteTransition(label = "creator_premium_anim")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotationAngle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val shiningBrush = Brush.sweepGradient(
        colors = listOf(
            Color(0xFF6366F1),
            Color(0xFFA855F7),
            Color(0xFFEC4899),
            Color(0xFFF59E0B),
            Color(0xFF10B981),
            Color(0xFF3B82F6),
            Color(0xFF6366F1)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Creator Image with Premium Layered Layout, Circle Shadow & Shining Rotation
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            // Layer 1: Outer Pulsing Glow (Circle Shadow)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                        alpha = 0.25f
                    }
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF6366F1), Color.Transparent)
                        ),
                        CircleShape
                    )
            )

            // Layer 2: Rotating Shining Ring
            Canvas(modifier = Modifier.fillMaxSize()) {
                rotate(rotationAngle) {
                    drawCircle(
                        brush = shiningBrush,
                        radius = size.minDimension / 2 - 8.dp.toPx(),
                        style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }

            // Layer 3: Inner White Border & Shadow
            Surface(
                modifier = Modifier
                    .size(150.dp)
                    .shadow(32.dp, CircleShape, spotColor = Color(0xFF6366F1).copy(alpha = 0.4f)),
                shape = CircleShape,
                color = Color.White,
                border = BorderStroke(4.dp, Color.White)
            ) {
                // Layer 4: The Image
                Image(
                    painter = painterResource(id = R.drawable.app_creator),
                    contentDescription = "Mohammed Zubair Nsk",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(Modifier.height(28.dp))

        Surface(
            color = Color(0xFFEEF2FF),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "ABOUT THE APP CREATOR",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = Color(0xFF6366F1),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Mohammed Zubair NSK",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = Color(0xFF0F172A)
        )
        
        Text(
            text = "Creator • UI/UX Designer • Android Developer",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6366F1),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(Modifier.height(16.dp))

        // Dynamic and Attractive Native Land Card added below the name info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Native Land",
                        tint = Color(0xFF6366F1),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Native Land".uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = Color(0xFF6366F1)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column {
                            Text("Village", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("Naisafuru (Nayapara)", fontSize = 13.sp, color = Color(0xFF334155), fontWeight = FontWeight.ExtraBold)
                        }
                        Column {
                            Text("Township", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("Maungdaw", fontSize = 13.sp, color = Color(0xFF334155), fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column {
                            Text("State", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("Arkan", fontSize = 13.sp, color = Color(0xFF334155), fontWeight = FontWeight.ExtraBold)
                        }
                        Column {
                            Text("Country", fontSize = 11.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                            Text("Myanmar", fontSize = 13.sp, color = Color(0xFF334155), fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "I’m Mohammed Zubair NSK, the creator and designer of Rohingya Shikho. I created Rohingya Shikho with the idea of making language learning simple, engaging, colourful, and accessible. The app is designed to help learners explore the Rohingya language and Hanifi script through interactive lessons, visual learning, practice, and quizzes.",
            fontSize = 15.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Justify,
            lineHeight = 22.sp
        )

        Spacer(Modifier.height(24.dp))

        CreatorSectionHeader("My Role", Icons.Rounded.AccountCircle)
        
        CreatorRoleItem(
            title = "UI/UX Design",
            description = "I design the app interface, learning pages, illustrations, icons, colours, animations, and overall user experience.",
            icon = Icons.Rounded.Palette,
            color = Color(0xFFEC4899)
        )
        
        CreatorRoleItem(
            title = "App Development",
            description = "I develop the Android application and build interactive learning features such as lessons, quizzes, audio, progress, and practice activities.",
            icon = Icons.Rounded.Code,
            color = Color(0xFF6366F1)
        )
        
        CreatorRoleItem(
            title = "Learning Design",
            description = "I organise learning content into structured levels so learners can gradually develop their knowledge and skills.",
            icon = Icons.AutoMirrored.Rounded.MenuBook,
            color = Color(0xFF10B981)
        )

        Spacer(Modifier.height(24.dp))

        CreatorSectionHeader("Why I Created Rohingya Shikho", Icons.Rounded.Lightbulb)
        Text(
            text = "Language is an important part of identity, culture, and communication. I wanted to create a modern learning experience that makes learning the Rohingya language and Hanifi script easier and more enjoyable.",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Justify,
            lineHeight = 20.sp
        )

        Spacer(Modifier.height(24.dp))

        CreatorSectionHeader("My Vision", Icons.Rounded.Visibility)
        Text(
            text = "Learn • Practice • Explore • Grow",
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A),
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Surface(
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "«Small steps can lead to big learning.»",
                modifier = Modifier.padding(16.dp),
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center,
                color = Color(0xFF475569)
            )
        }

        Spacer(Modifier.height(24.dp))

        CreatorSectionHeader("A Note to Learners", Icons.Rounded.ChatBubble)
        Text(
            text = "Thank you for being part of this learning journey. I hope Rohingya Shikho helps you discover, practise, and enjoy the Rohingya language in a new way.",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Justify,
            lineHeight = 20.sp
        )
        
        Spacer(Modifier.height(24.dp))
        
        Text(
            text = "Made with ❤️ for learning.",
            fontWeight = FontWeight.Bold,
            color = Color(0xFFEF4444),
            fontSize = 14.sp
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "Mohammed Zubair NSK\nCreator & Designer of Rohingya Shikho",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = Color(0xFF0F172A),
            lineHeight = 18.sp
        )
        
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun CreatorSectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    ) {
        Icon(icon, null, tint = Color(0xFF0F172A), modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            text = title.uppercase(),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = Color(0xFF0F172A)
        )
    }
}

@Composable
fun CreatorRoleItem(title: String, description: String, icon: ImageVector, color: Color) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            color = color.copy(alpha = 0.1f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
        }
        Column {
            Text(title, fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color(0xFF0F172A))
            Text(description, fontSize = 13.sp, color = Color(0xFF64748B), lineHeight = 18.sp, textAlign = TextAlign.Justify)
        }
    }
}

@Composable
fun HistorySlide() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            LiveImagePulse(color = Color(0xFF8B5CF6), imageRes = R.drawable.hanif)
            
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 24.dp, y = (-12).dp)
                    .rotate(8f),
                color = Color(0xFFFFD700),
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp
            ) {
                Text(
                    "EST. 1980s",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = Color(0xFF1E293B)
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        Surface(
            color = Color(0xFFF5F3FF),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "THE SCRIPT CREATOR",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = Color(0xFF8B5CF6),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }
        
        Spacer(Modifier.height(14.dp))
        
        Text(
            text = "Maulana Mohammad Hanif",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            color = Color(0xFF0F172A)
        )
        
        Spacer(Modifier.height(12.dp))
        
        Text(
            text = "In the 1980s, Maulana Hanif developed this unique script to preserve the Rohingya language and identity, ensuring our culture stays alive for generations.",
            fontSize = 15.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Justify,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun LiveImagePulse(color: Color, imageRes: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    
    val ringScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringScale"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringAlpha"
    )

    val shadowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1250, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shadowScale"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(140.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = ringScale
                    scaleY = ringScale
                    alpha = ringAlpha
                }
                .background(color, CircleShape)
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(color.copy(alpha = 0.3f), Color.Transparent),
                            center = center,
                            radius = size.minDimension / 2 * shadowScale * 1.5f
                        )
                    )
                }
        )

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Portrait",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun DirectionSlide() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            color = Color.White,
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
            shadowElevation = 0.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "direction")
                val progress by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1.2f, // Hold full word for a bit
                    animationSpec = infiniteRepeatable(
                        animation = tween(4000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "progress"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val fullText = "𐴌𐴟𐴇𐴝𐴥𐴚𐴒𐴙𐴝"
                    val codePoints = remember(fullText) {
                        val list = mutableListOf<String>()
                        var i = 0
                        while (i < fullText.length) {
                            val cp = fullText.codePointAt(i)
                            val char = String(Character.toChars(cp))
                            list.add(char)
                            i += Character.charCount(cp)
                        }
                        list
                    }
                    
                    val totalCodePoints = codePoints.size
                    val visibleCount = (progress.coerceIn(0f, 1f) * totalCodePoints).toInt()
                    val visibleText = codePoints.take(visibleCount).joinToString("")

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Box(
                                modifier = Modifier
                                    .padding(bottom = 40.dp)
                                    .width(IntrinsicSize.Max),
                                contentAlignment = Alignment.Center
                            ) {
                                // Background Ghost Text
                                Text(
                                    text = fullText,
                                    fontSize = 52.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF0F172A).copy(alpha = 0.05f),
                                    softWrap = false
                                )
                                // Typing Text (Fills Right-to-Left logically)
                                Text(
                                    text = visibleText,
                                    fontSize = 52.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF10B981),
                                    softWrap = false,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Start // This is Right in RTL
                                )
                            }
                        }

                        // Progress Track (Writing Line) - animating RTL direction
                        Box(
                            modifier = Modifier
                                .width(220.dp)
                                .height(12.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                        ) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.CenterEnd) // Fill from Right to Left
                                    .fillMaxHeight()
                                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF3B82F6), Color(0xFF10B981))
                                        ), 
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        Surface(
            color = Color(0xFFE6F4EA),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "HOW TO READ & WRITE",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = Color(0xFF10B981),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }
        
        Spacer(Modifier.height(14.dp))
        
        Text(
            text = "Right to Left Flow",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = Color(0xFF0F172A)
        )
        
        Spacer(Modifier.height(14.dp))
        
        Text(
            text = "Hanifi Rohingya is written and read from right to left, similar to Arabic. Connect letters seamlessly from right to left to capture the rhythm of our language.",
            fontSize = 15.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Justify,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun UnicodeSlide() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .background(Color(0xFFFFF7ED), CircleShape)
                    .border(2.dp, Color(0xFFF59E0B), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Language,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = Color(0xFFF59E0B)
                )
            }
            
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 10.dp, y = 10.dp)
                    .rotate(-5f),
                color = Color(0xFF6366F1),
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 4.dp
            ) {
                Text(
                    "U+10D00",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        Surface(
            color = Color(0xFFFFF7ED),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "DIGITAL STANDARD",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                color = Color(0xFFF59E0B),
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )
        }
        
        Spacer(Modifier.height(14.dp))
        
        Text(
            text = "Unicode Integration",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            color = Color(0xFF0F172A)
        )
        
        Spacer(Modifier.height(12.dp))
        
        Text(
            text = "Rohingya Hanifi was added to the Unicode Standard in 2018. This global milestone allows our script to be used on smartphones, computers, and social media apps everywhere.",
            fontSize = 15.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Justify,
            lineHeight = 24.sp
        )
    }
}
