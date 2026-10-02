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
fun AdvancedIntroScreen(
    onNavigateBack: () -> Unit,
    onStartAdvancedLearning: () -> Unit
) {
    val scrollState = rememberScrollState()
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        containerColor = Color(0xFFF0FDF4) // Light green for advanced/mastery look
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(padding)) {
            val screenWidth = maxWidth
            val isWide = screenWidth > 600.dp
            val horizontalPadding = if (isWide) 64.dp else 24.dp

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                AdvancedHeroCard(scrollState, visible)

                Column(
                    modifier = Modifier
                        .padding(horizontal = horizontalPadding, vertical = 32.dp)
                        .widthIn(max = 900.dp)
                        .align(Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(40.dp)
                ) {
                    // Section: Advanced Mastery & Scope
                    AnimatedSection(visible, delay = 100) {
                        SectionHeader("Mastery & Eloquence", "THE ADVANCED DISCOURSE")
                        AdvancedScopeCard()
                    }

                    // Section: Interactive Grammar Highlight
                    AnimatedSection(visible, delay = 200) {
                        SectionHeader("Complex Text Structures", "FORMAL READING FLOW")
                        AdvancedLiteratureCard("𐴌𐴟𐴇𐴝𐴥𐴚𐴒𐴙𐴝 𐴓𐴢𐴘𐴝𐴯𐴝𐴥𐴕")
                    }

                    // Section: Grammar Pillars
                    AnimatedSection(visible, delay = 300) {
                        SectionHeader("Linguistic Pillars", "GRAMMAR & FORMAL SYNTAX")
                        AdvancedGrammarPillars()
                    }

                    // Section: Advanced Metrics (Responsive Grid)
                    AnimatedSection(visible, delay = 400) {
                        SectionHeader("Curriculum Breakdown", "MASTERY COMPONENTS")
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            maxItemsInEachRow = if (isWide) 4 else 2
                        ) {
                            SmallStatCard("500+", "Rich Words", Color(0xFF16A34A), Modifier.weight(1f))
                            SmallStatCard("30+", "Idioms", Color(0xFF0D9488), Modifier.weight(1f))
                            SmallStatCard("15+", "Formal Texts", Color(0xFFEAB308), Modifier.weight(1f))
                            SmallStatCard("100%", "Fluency Goal", Color(0xFF2563EB), Modifier.weight(1f))
                        }
                    }

                    Spacer(Modifier.height(140.dp))
                }
            }

            // Interactive Sticky Top Bar
            AdvancedInteractiveTopBar(scrollState, onNavigateBack)

            // Responsive Floating CTA Button
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = horizontalPadding, vertical = 24.dp)
                    .navigationBarsPadding()
                    .widthIn(max = 500.dp)
            ) {
                Button(
                    onClick = onStartAdvancedLearning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .shadow(24.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF14532D))
                ) {
                    Text("LAUNCH ADVANCED LESSONS", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, letterSpacing = 1.2.sp)
                    Spacer(Modifier.width(12.dp))
                    Icon(Icons.Rounded.AutoAwesome, null, modifier = Modifier.size(22.dp))
                }
            }
        }
    }
}

@Composable
fun AnimatedSection(
    visible: Boolean,
    delay: Int,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 600, delayMillis = delay)) +
                slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(durationMillis = 600, delayMillis = delay))
    ) {
        content()
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = subtitle,
            color = Color(0xFF16A34A),
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
fun SmallStatCard(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(Modifier.height(4.dp))
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AdvancedHeroCard(scrollState: ScrollState, visible: Boolean) {
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
            painter = painterResource(id = R.drawable.every_day),
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
                        0.5f to Color.Black.copy(alpha = 0.3f),
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
                color = Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF4ADE80))
            ) {
                Text(
                    "ADVANCED LEVEL",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4ADE80)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Literature & Mastery",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 44.sp
            )
            Text(
                "Achieve perfect fluency, appreciate rich historical documents and formal conversation patterns.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun AdvancedInteractiveTopBar(scrollState: ScrollState, onBack: () -> Unit) {
    val appBarAlpha by animateFloatAsState(
        targetValue = (scrollState.value / 250f).coerceIn(0f, 1f),
        label = "appBarAlpha"
    )

    val contentColor = if (appBarAlpha < 0.5f) Color.White else Color(0xFF14532D)

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
            Surface(
                onClick = onBack,
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp),
                border = if (appBarAlpha >= 0.5f) BorderStroke(1.dp, Color(0xFFDCFCE7)) else null
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
                    "Advanced Mastery",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = Color(0xFF14532D),
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    }
}

@Composable
fun AdvancedScopeCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color(0xFFDCFCE7), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Gavel, null, tint = Color(0xFF16A34A), modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text("Sophisticated Expression", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color(0xFF1E293B))
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Welcome to the ultimate learning chapter. Here, you transition from basic communication to intellectual fluency. You will discover deep historical narratives, intricate grammar nuances, complex idioms, and multi-layered tone structures.",
                fontSize = 15.sp, color = Color(0xFF475569), lineHeight = 24.sp
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AdvancedInfoPoint(Icons.Rounded.Translate, "Formal Vocabulary", "Learn elevated words used in literature, broadcast news, and formal assemblies.")
                AdvancedInfoPoint(Icons.Rounded.MenuBook, "Complex Sentence Modifiers", "Master sub-clauses, relative conjunctions, and advanced past/future conditional forms.")
                AdvancedInfoPoint(Icons.Rounded.RecordVoiceOver, "Perfect Pitch & Intonation", "Refine tone modifications across long compound sentences.")
            }
        }
    }
}

@Composable
fun AdvancedInfoPoint(icon: ImageVector, title: String, desc: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.size(36.dp).background(Color(0xFFE8F5E9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = Color(0xFF16A34A))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1E293B))
            Text(desc, fontSize = 13.sp, color = Color(0xFF64748B), lineHeight = 18.sp)
        }
    }
}

@Composable
fun AdvancedLiteratureCard(text: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "advancedReading")
    val sweepProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = EaseInOutQuad), RepeatMode.Reverse),
        label = "sweepProgress"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(32.dp),
        border = BorderStroke(1.dp, Color(0xFFE8F5E9)),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(28.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).background(Color(0xFFE6F4EA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.MenuBook, null, tint = Color(0xFF13532D), modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text("Literary Rhythm Reading", fontWeight = FontWeight.ExtraBold, fontSize = 19.sp, color = Color(0xFF1E293B))
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Advanced texts integrate deep vowel combinations and beautiful continuous curves. Observe the elegant flow of advanced phrase reading below.",
                fontSize = 15.sp, color = Color(0xFF64748B), lineHeight = 22.sp
            )

            Spacer(Modifier.height(32.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF4FBF7), RoundedCornerShape(24.dp))
                    .padding(28.dp),
                contentAlignment = Alignment.Center
            ) {
                val density = LocalDensity.current
                val responsiveFontSize = (this@BoxWithConstraints.maxWidth.value * 0.11f).coerceIn(26f, 44f).sp

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color(0xFFE2E8F0), CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.12f)
                                .fillMaxHeight()
                                .align(Alignment.CenterEnd)
                                .graphicsLayer {
                                    val travelWidth = with(density) { (this@BoxWithConstraints.maxWidth - 24.dp).toPx() }
                                    translationX = -sweepProgress * travelWidth
                                }
                                .background(Color(0xFF16A34A), CircleShape)
                        )
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("RTL FOCUS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF16A34A))
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Rounded.ArrowForward, null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                    }

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = text,
                            fontSize = responsiveFontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                            modifier = Modifier.padding(vertical = 12.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AutoAwesome, null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("FLUENT APPRECIATION", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFF64748B))
                    }
                }
            }
        }
    }
}

@Composable
fun AdvancedGrammarPillars() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        AdvancedPillarBox(1, "Historical Context", "Analyze original Hanifi manuscripts, documenting culture and poetry.", Icons.Rounded.HistoryEdu)
        AdvancedPillarBox(2, "Grammatical Synthesis", "Construct compound paragraphs with precise temporal and honorific particles.", Icons.Rounded.Interests)
        AdvancedPillarBox(3, "Conversational Artistry", "Express nuances, metaphors, and native colloquial humor with absolute confidence.", Icons.Rounded.RecordVoiceOver)
    }
}

@Composable
fun AdvancedPillarBox(num: Int, title: String, desc: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(24.dp))
            .border(1.dp, Color(0xFFE8F5E9), RoundedCornerShape(24.dp))
            .padding(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(Color(0xFFE8F5E9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$num", fontWeight = FontWeight.Black, color = Color(0xFF14532D), fontSize = 18.sp)
        }
        Spacer(Modifier.width(20.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, modifier = Modifier.size(18.dp), tint = Color(0xFF16A34A))
                Spacer(Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = Color(0xFF1E293B))
            }
            Text(desc, fontSize = 14.sp, color = Color(0xFF64748B), modifier = Modifier.padding(top = 4.dp))
        }
    }
}
