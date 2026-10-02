package com.zubtech.rohingyashikho.presentation.level

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.presentation.alphabet.AlphabetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBuildingScreen(
    onNavigateBack: () -> Unit,
    viewModel: AlphabetViewModel = hiltViewModel()
) {
    val consonants by viewModel.consonants.collectAsState()
    val vowels by viewModel.vowels.collectAsState()
    
    var currentWord by remember { mutableStateOf(listOf<String>()) }
    val themeColor = Color(0xFF0D9488) // Intermediate Teal

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0F766E), Color(0xFF14B8A6))
                )
            )
    ) {
        // Decorative background elements
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = (-50).dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Word Building",
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .size(40.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Workspace / Result Area
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(24.dp)
                        .shadow(12.dp, RoundedCornerShape(32.dp)),
                    color = Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (currentWord.isEmpty()) {
                            Text(
                                "Start building a word...",
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    currentWord.forEach { char ->
                                        Text(
                                            text = char,
                                            fontSize = 48.sp,
                                            fontWeight = FontWeight.Black,
                                            color = themeColor
                                        )
                                    }
                                }
                            }
                            
                            IconButton(
                                onClick = { if (currentWord.isNotEmpty()) currentWord = currentWord.dropLast(1) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                            ) {
                                Icon(Icons.Rounded.Backspace, null, tint = Color(0xFFEF4444))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Control Buttons
                Row(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { currentWord = emptyList() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Clear All", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { /* Save or check word logic */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4ADE80)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Rounded.CheckCircle, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Finish Word", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Selection Area
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 42.dp, topEnd = 42.dp),
                    color = Color(0xFFF8FAFC)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "Consonants",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(consonants) { item ->
                                BuildPartCard(item.scriptText.replace("𐴢", ""), themeColor) {
                                    currentWord = currentWord + it
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            "Vowels & Tones",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(Modifier.height(12.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(vowels) { item ->
                                val cleanChar = item.scriptText.replace("◌", "").replace("𐴀", "")
                                if (cleanChar.isNotEmpty()) {
                                    BuildPartCard(cleanChar, Color(0xFFEC4899)) {
                                        currentWord = currentWord + it
                                    }
                                }
                            }
                            // Add common tones if not in vowels
                            val tones = listOf("𐴤", "𐴥", "𐴦", "𐴧", "𐴣")
                            items(tones) { tone ->
                                BuildPartCard(tone, Color(0xFFF59E0B)) {
                                    currentWord = currentWord + it
                                }
                            }
                        }
                        
                        Spacer(Modifier.height(32.dp))
                        
                        // Tip Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.Lightbulb, null, tint = Color(0xFFF59E0B))
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "Tip: Start with a consonant, add a vowel sign, and then apply tones for complex sounds.",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BuildPartCard(char: String, color: Color, onClick: (String) -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.9f else 1f, label = "scale")

    Surface(
        modifier = Modifier
            .size(70.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource, null) { onClick(char) },
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        border = BorderStroke(1.5.dp, color.copy(alpha = 0.2f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = char,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
