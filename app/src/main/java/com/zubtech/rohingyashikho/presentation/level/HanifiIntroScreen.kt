package com.zubtech.rohingyashikho.presentation.level

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zubtech.rohingyashikho.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HanifiIntroScreen(
    onNavigateBack: () -> Unit,
    onStartLearning: () -> Unit
) {
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            val isWide = screenWidth > 600.dp
            val horizontalPadding = if (isWide) 64.dp else 24.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                IntroHeroCard(scrollState, visible)
                
                Column(
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding, vertical = 32.dp)
                        .widthIn(max = 900.dp)
                        .align(Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    // Section: Script Heritage & Info
                    AnimatedSection(visible, delay = 100) {
                        SectionHeader("Heritage & History", "THE HANIFI ORIGIN")
                        HanifiScriptInfoCard()
                    }

                    // Section: Reading Direction (Responsive RTL guide)
                    AnimatedSection(visible, delay = 200) {
                        SectionHeader("Reading Direction", "THE RTL FLOW")
                        ReadingGuideCard("𐴌𐴟𐴇𐴝𐴥𐴚𐴒𐴙𐴝")
                    }

                    // Section: Key Writing Principles
                    AnimatedSection(visible, delay = 300) {
                        SectionHeader("Writing Logic", "CONNECTION & FORM")
                        WritingGuideSteps()
                    }

                    // Section: Core Components (Responsive Grid)
                    AnimatedSection(visible, delay = 400) {
                        SectionHeader("Script Anatomy", "KEY COMPONENTS")
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            maxItemsInEachRow = if (isWide) 4 else 2
                        ) {
                            SmallStatCard("28", "Consonants", Color(0xFF6366F1), Modifier.weight(1f))
                            SmallStatCard("10", "Vowels", Color(0xFF10B981), Modifier.weight(1f))
                            SmallStatCard("3", "Tone Marks", Color(0xFFF59E0B), Modifier.weight(1f))
                            SmallStatCard("10", "Numerals", Color(0xFFEC4899), Modifier.weight(1f))
                        }
                    }

                    Spacer(Modifier.height(140.dp))
                }
            }

            // Interactive Sticky Top Bar with Transparent Back Background
            InteractiveTopBar(scrollState, onNavigateBack)

            // Responsive Floating CTA Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = horizontalPadding, vertical = 24.dp)
                    .navigationBarsPadding()
                    .widthIn(max = 500.dp)
            ) {
                Button(
                    onClick = onStartLearning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .shadow(24.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFF3B82F6)),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Text("START LEARNING", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, letterSpacing = 1.2.sp)
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Rounded.RocketLaunch, null, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
fun InteractiveTopBar(scrollState: ScrollState, onBack: () -> Unit) {
    val appBarAlpha by animateFloatAsState(
        targetValue = (scrollState.value / 250f).coerceIn(0f, 1f),
        label = "appBarAlpha"
    )
    
    val contentColor = if (appBarAlpha < 0.5f) Color.White else Color.Black
    
    Surface(
        color = Color.White.copy(alpha = appBarAlpha),
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = if (appBarAlpha > 0.9f) 8.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BACK Button with clear/transparent background
            Surface(
                onClick = onBack,
                color = Color.Transparent, 
                shape = RoundedCornerShape(16.dp),
                border = if (appBarAlpha >= 0.5f) BorderStroke(1.dp, Color(0xFFF1F5F9)) else null
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Back", 
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "BACK",
                        color = contentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.1.sp
                    )
                }
            }
            
            Spacer(Modifier.weight(1f))
            
            AnimatedVisibility(
                visible = scrollState.value > 250,
                enter = fadeIn() + slideInVertically(initialOffsetY = { -20 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { -20 })
            ) {
                Text(
                    "Hanifi Script",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    }
}

@Composable
fun HanifiScriptInfoCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFFFEF3C7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.HistoryEdu, null, tint = Color(0xFFD97706), modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text("About the Script", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF1E293B))
            }
            
            Spacer(Modifier.height(20.dp))
            Text(
                "The Hanifi Rohingya script was created in the 1980s by Maulana Mohammad Hanif. It is a phonetic writing system specifically designed to represent the unique sounds of the Rohingya language, ensuring its preservation and cultural identity.",
                fontSize = 15.sp, color = Color(0xFF475569), lineHeight = 24.sp
            )
            
            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(Modifier.height(20.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                InfoPoint(Icons.Rounded.Language, "Native Creation", "Developed by the community for their own linguistic needs.")
                InfoPoint(Icons.Rounded.Verified, "Unicode Standard", "Officially recognized and added to the Unicode Standard in 2018.")
                InfoPoint(Icons.Rounded.SettingsVoice, "Phonetic Clarity", "Letters correspond directly to sounds, making it easy to learn.")
            }
        }
    }
}

@Composable
fun InfoPoint(icon: ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(36.dp).background(Color(0xFFEFF6FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = Color(0xFF3B82F6))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
            Text(desc, fontSize = 13.sp, color = Color(0xFF64748B), lineHeight = 18.sp)
        }
    }
}

@Composable
fun IntroHeroCard(scrollState: ScrollState, visible: Boolean) {
    val configuration = LocalConfiguration.current
    val heroHeight = (configuration.screenHeightDp * 0.45f).dp.coerceAtLeast(320.dp)
    val yOffset = scrollState.value * 0.45f
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(heroHeight)
            .graphicsLayer { translationY = yOffset }
    ) {
        Image(
            painter = painterResource(id = R.drawable.graphic1),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.5f to Color.Black.copy(alpha = 0.2f),
                        1f to Color.Black.copy(alpha = 0.95f)
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
                .graphicsLayer {
                    alpha = (1f - (scrollState.value / 450f)).coerceIn(0f, 1f)
                }
        ) {
            Surface(
                color = Color.Transparent, // Clearer badge
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFFFD700))
            ) {
                Text(
                    "INTRODUCTION",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD700)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "The Hanifi Script",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 46.sp
            )
            Text(
                "A script born from resilience, designed for the unique melody of the Rohingya tongue.",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun ReadingGuideCard(text: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "readingDirection")
    val dotOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing), RepeatMode.Restart),
        label = "dotOffset"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).background(Color(0xFFEFF6FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.HistoryEdu, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text("Right-to-Left Direction", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = Color(0xFF1E293B))
            }
            
            Spacer(Modifier.height(20.dp))
            Text(
                "Unlike English, Hanifi Rohingya is read from right to left. Follow the animated guide below!",
                fontSize = 15.sp, color = Color(0xFF64748B), lineHeight = 22.sp
            )
            
            Spacer(Modifier.height(32.dp))
            
            // Responsive Box for Rohingya text
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(24.dp))
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                val density = LocalDensity.current
                // Responsive medium size font
                val responsiveFontSize = (this@BoxWithConstraints.maxWidth.value * 0.12f).coerceIn(28f, 48f).sp
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Directional Indicator Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color(0xFFE2E8F0), CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.1f)
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .graphicsLayer { 
                                    val travelWidth = with(density) { (this@BoxWithConstraints.maxWidth - 20.dp).toPx() }
                                    translationX = -dotOffset * travelWidth 
                                }
                                .background(Color(0xFF3B82F6), CircleShape)
                        )
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("START", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF3B82F6))
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.ArrowForward, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(14.dp))
                    }
                    
                    // Rohingya Text with RTL Direction and Responsive Medium Size
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = text, 
                            fontSize = responsiveFontSize,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.ArrowBack, null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("FINISH", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF94A3B8))
                    }
                }
            }
        }
    }
}

@Composable
fun WritingGuideSteps() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        WritingStepBox(1, "Connective Flow", "Letters connect to each other smoothly, creating a beautiful cursive look.", Icons.Rounded.Gesture)
        WritingStepBox(2, "No Case System", "There are no capital letters. The script is designed for simplicity and speed.", Icons.Rounded.Abc)
        WritingStepBox(3, "Melody Markers", "Specific tone marks represent the pitch, essential for Rohingya words.", Icons.Rounded.MusicNote)
    }
}

@Composable
fun WritingStepBox(num: Int, title: String, desc: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFFF1F5F9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$num", fontWeight = FontWeight.Black, color = Color(0xFF1E293B), fontSize = 18.sp)
        }
        Spacer(Modifier.width(20.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = Color(0xFF3B82F6))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color(0xFF1E293B))
            }
            Text(desc, fontSize = 14.sp, color = Color(0xFF64748B), modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            text = subtitle.uppercase(),
            color = Color(0xFF3B82F6),
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            text = title,
            color = Color(0xFF1E293B),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun AnimatedSection(visible: Boolean, delay: Int, content: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(700, delay)) + 
                fadeIn(animationSpec = tween(700, delay))
    ) {
        content()
    }
}

@Composable
fun SmallStatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 32.sp, fontWeight = FontWeight.Black, color = color)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B))
        }
    }
}
