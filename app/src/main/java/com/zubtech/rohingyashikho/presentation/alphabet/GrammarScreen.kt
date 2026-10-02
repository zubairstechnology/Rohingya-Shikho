package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun GrammarScreen(
    levelId: String,
    onNavigateBack: () -> Unit,
    viewModel: GrammarViewModel = hiltViewModel()
) {
    val isAdvanced = levelId == "3"
    val isIntermediate = levelId == "2"
    
    val highlightedNativeIndex by viewModel.highlightedNativeIndex.collectAsState()
    val highlightedEnglishIndex by viewModel.highlightedEnglishIndex.collectAsState()
    val playingAudioType by viewModel.playingAudioType.collectAsState()

    val basicGrammar = remember {
        listOf(
            GrammarModel(
                name = "Sakin",
                nativeName = "𐴏𐴝𐴥𐴑𐴦𐴞𐴕",
                scriptSign = "𐴢",
                description = "Marks a consonant vowel-less or silent stop.",
                nativeDescription = "𐴎𐴠 𐴇𐴡𐴥𐴌𐴗𐴧𐴝𐴃𐴢 𐴀𐴝𐴉𐴡𐴌 ، 𐴀𐴞𐴉𐴡𐴌 ، 𐴀𐴟𐴉𐴡𐴌 ، 𐴀𐴠𐴉𐴡𐴌 𐴀𐴝𐴌 𐴀𐴡𐴉𐴡𐴌 𐴕𐴡 𐴀𐴝𐴘𐴁𐴡𐴤 𐴇𐴥𐴁𐴝 𐴏𐴝𐴥𐴑𐴦𐴞𐴕 𐴀𐴡𐴘. 𐴌𐴟𐴇𐴝𐴥𐴚𐴒𐴙𐴝 𐴎𐴟𐴁𐴝𐴕𐴡𐴃𐴢 𐴀𐴠𐴑𐴧𐴟 𐴓𐴝𐴃𐴧𐴝𐴃𐴥𐴢 𐴏𐴝𐴥𐴑𐴦𐴞𐴕 𐴈𐴡𐴀𐴒𐴧𐴗𐴝𐴤 𐴀𐴡𐴥𐴘 𐴉𐴝𐴌.",
                example = "𐴊𐴡𐴜𐴕𐴢",
                exampleEnglish = "Don (Silent Stop)",
                accentColor = Color(0xFF0F766E),
                nativeAudioFile = "sakin_native.mp3",
                nativeWordTimings = listOf(0, 500, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000, 5500, 6000, 6500, 7000, 7500, 8000, 8500, 9000, 9500),
                englishAudioFile = "sakin_english.mp3",
                englishWordTimings = listOf(0, 400, 800, 1200, 1600, 2000, 2400, 2800),
                examples = listOf(
                    GrammarExample("Burial", "𐴊𐴡𐴜𐴕𐴢"),
                    GrammarExample("Forest", "𐴁𐴝𐴕𐴢"),
                    GrammarExample("Crowd", "𐴒𐴡𐴕𐴢"),
                    GrammarExample("Salt", "𐴓𐴡𐴕𐴢"),
                    GrammarExample("Mind", "𐴔𐴡𐴕𐴢"),
                    GrammarExample("Body", "𐴃𐴡𐴕𐴢"),
                    GrammarExample("Who", "𐴑𐴡𐴕𐴢"),
                    GrammarExample("Drink", "𐴉𐴝𐴕"),
                    GrammarExample("Respect", "𐴔𐴝𐴕"),
                    GrammarExample("Life", "𐴎𐴡𐴕")
                )
            ),
            GrammarModel(
                name = "Naghorna",
                nativeName = "𐴕𐴝𐴈𐴡𐴕𐴧𐴝",
                scriptSign = "𐴣",
                description = "Adds nasalization accent/sound to the vowels.",
                nativeDescription = "𐴕𐴝𐴈𐴡𐴕𐴧𐴝 𐴀𐴠𐴀𐴕 𐴀𐴟𐴒𐴧𐴗𐴤𐴝 𐴀𐴝𐴁𐴝𐴏𐴡𐴌𐴠𐴥 𐴈𐴡𐴀 𐴎𐴞𐴁𐴝𐴥 𐴒𐴝𐴓𐴡𐴌 𐴀𐴝𐴁𐴝𐴏𐴡𐴌 𐴉𐴟𐴥𐴖𐴝𐴣𐴃𞴞 𐴕𐴝𐴈𐴡𐴃𐴧𐴟𐴥 𐴕𞴞𞴘𞴡𞴓",
                example = "𐴌𐴡𐴜𐴕𐴣𐴔𐴢",
                exampleEnglish = "Rom (Nasalized)",
                accentColor = Color(0xFF0D9488),
                nativeAudioFile = "naghorna_native.mp3",
                nativeWordTimings = listOf(0, 600, 1200, 1800, 2400, 3000, 3600, 4200, 4800, 5400, 6000, 6600),
                examples = listOf(
                    GrammarExample("Pomegranate", "𐴌𐴡𐴣𐴔"),
                    GrammarExample("Mango", "𐴀𐴝𐴣𐴔"),
                    GrammarExample("Work", "𐴑𐴝𐴣𐴔"),
                    GrammarExample("Price", "𐴊𐴝𐴣𐴔"),
                    GrammarExample("Name", "𐴕𐴝𐴣𐴔"),
                    GrammarExample("Ferry", "𐴎𐴝𐴣𐴔"),
                    GrammarExample("Long", "𐴓𐴝𐴣𐴔"),
                    GrammarExample("Evening", "𐴏𐴝𐴣𐴔"),
                    GrammarExample("Copper", "𐴃𐴝𐴣𐴔"),
                    GrammarExample("Year", "𐴘𐴝𐴣𐴔")
                )
            ),
            GrammarModel(
                name = "Harbay",
                nativeName = "𐴇𐴝𐴥𐴌 𐴁𐴝𐴤𐴘",
                scriptSign = "𐴤",
                description = "Marks a consonant soft sound stop.",
                nativeDescription = "𐴇𐴝𐴥𐴌𐴁𐴝𐴤𐴘 𐴈𐴡𐴀𐴠 𐴀𐴠𐴀𐴕 𐴀𐴟𐴒𐴗𐴧𐴝𐴤 𐴎𐴟𐴌 𐴈𐴡𐴔 𐴀𐴡𐴦𐴓𐴝 𐴀𐴝𐴁𐴝𐴏𐴌𞴠𞴥 𞴎𞴞𞴁𞴝𐴤 𞴒𐴝𐴓𐴡𐴃𐴧𐴟𐴥 𞴕𞴞𞴘𞴡𞴙𞴓𞴃𞴠 𞴎𞴟𞴌 𞴈𞴡𞴔 𞴀𞴡𐴥𐴕𞴦𐴡 𞴎𞴡𐴌𞴘𞴝 𞴀𞴠𞴑𞴧𞴠𞴕𞴝 𞴇𐴝𐴥𐴌 𐴀𐴝𐴁𐴝𐴏 𐴀𐴝𐴘𞴧𞴠𞴥 郁𞴝𞴕 𞴓𞴝𞴒𞴠.",
                example = "𐴁𐴝𐴤𐴌𐴡",
                exampleEnglish = "Baro (Soft Stop)",
                accentColor = Color(0xFF8B5CF6),
                nativeAudioFile = "harbay_native.mp3",
                nativeWordTimings = listOf(0, 550, 1100, 1650, 2200, 2750, 3300, 3850, 4400, 4950, 5500, 6050, 6600, 7150, 7700, 8250, 8800, 9350),
                examples = listOf(
                    GrammarExample("Twelve", "𐴁𐴝\u200C𐴤𐴌𐴡"),
                    GrammarExample("Four", "𐴏𐴝\u200C𐴤𐴌𐴡"),
                    GrammarExample("Kill", "𐴔𐴝\u200C𐴤𐴌𐴡"),
                    GrammarExample("Star", "𐴃𐴝\u200C𐴤𐴌𐴡"),
                    GrammarExample("Do", "𐴑𐴝\u200C𐴤𐴌𐴡"),
                    GrammarExample("Lose", "𐴇𐴝\u200C𐴤𐴌𐴡"),
                    GrammarExample("Read", "𐴉𐴝\u200C𐴤𐴌𐴡"),
                    GrammarExample("Fight", "𐴓𐴝\u200C𐴤𐴌𞴡"),
                    GrammarExample("Current", "𐴎𐴝\u200C𐴤𐴌𐴡"),
                    GrammarExample("Door", "𐴊𐴝\u200C𐴤𐴌𐴡")
                )
            ),
            GrammarModel(
                name = "Tela",
                nativeName = "𐴃𐴠𐴓𐴝",
                scriptSign = "𐴥",
                description = "Represents low-falling tone marker.",
                nativeDescription = "𐴃𐴠𐴓𐴝 𐴎𞴞𞴁𞴝𐴥𞴘𞴠 𞴀𞴝𞴁𐴝𐴏𐴡𐴌 𞴓𞴝𞴔𞴝𞴘𞴠 𞴏𞴞𞴕𞴢 𞴒𐴡𐴌𞴠𞴥𞴿",
                example = "𐴁𐴝𐴤𐴌𐴡",
                exampleEnglish = "Baro (Low Tone)",
                accentColor = Color(0xFFEC4899),
                nativeAudioFile = "tela_native.mp3",
                nativeWordTimings = listOf(0, 700, 1400, 2100, 2800, 3500, 4200, 4900),
                examples = listOf(
                    GrammarExample("Twelve (Low)", "𐴁𐴝\u200C𐴥𐴌𐴡"),
                    GrammarExample("Four (Low)", "𐴏𐴝\u200C𐴥𐴌𐴡"),
                    GrammarExample("Kill (Low)", "𐴔𐴝\u200C𐴥𐴌𐴡"),
                    GrammarExample("Star (Low)", "𐴃𐴝\u200C𐴥𐴌𐴡"),
                    GrammarExample("Do (Low)", "𐴑𐴝\u200C𐴥𐴌𐴡"),
                    GrammarExample("Lose (Low)", "𐴇𐴝\u200C𐴥𐴌𐴡"),
                    GrammarExample("Read (Low)", "𐴉𐴝\u200C𐴥𐴌𐴡"),
                    GrammarExample("Fight (Low)", "𐴓𐴝\u200C𐴥𐴌𐴡"),
                    GrammarExample("Current (Low)", "𐴎𐴝\u200C𐴥𐴌𐴡"),
                    GrammarExample("Door (Low)", "𐴊𐴝\u200C𐴥𐴌𐴡")
                )
            ),
            GrammarModel(
                name = "Tana",
                nativeName = "𐴃𐴝𐴕𐴝",
                scriptSign = "𐴦",
                description = "Represents high-rising long vowel tone marker.",
                nativeDescription = "𐴃𐴝𐴕𐴝 𐴎𞴞𞴁𞴝𐴥𞴘𞴠 𞴀𞴝𞴁𐴝𐴏𐴡𐴌 𞴟𞴌𞴠𞴥 𞴃𞴝𞴕𞴞𞴘𞴝 𞴏𞴞𞴕𞴢 𞴡𐴌𞴠𞴥𞴿",
                example = "𐴔𐴝𐴥𐴌𐴡",
                exampleEnglish = "Maro (High Tone)",
                accentColor = Color(0xFFF59E0B),
                nativeAudioFile = "tana_native.mp3",
                nativeWordTimings = listOf(0, 700, 1400, 2100, 2800, 3500, 4200, 4900),
                examples = listOf(
                    GrammarExample("Kill (High)", "𐴔𐴝\u200C𐴦𐴌𐴡"),
                    GrammarExample("Twelve (High)", "𐴁𐴝\u200C𐴦𐴌𐴡"),
                    GrammarExample("Four (High)", "𐴏𐴝\u200C𐴦𐴌𐴡"),
                    GrammarExample("Star (High)", "𐴃𐴝\u200C𐴦𐴌𐴡"),
                    GrammarExample("Do (High)", "𐴑𐴝\u200C𐴦𐴌𐴡"),
                    GrammarExample("Lose (High)", "𐴇𐴝\u200C𐴦𐴌𐴡"),
                    GrammarExample("Read (High)", "𐴉𐴝\u200C𐴦𐴌𐴡"),
                    GrammarExample("Fight (High)", "𐴓𐴝\u200C𐴦𐴌𐴡"),
                    GrammarExample("Current (High)", "𐴎𐴝\u200C𐴦𐴌𐴡"),
                    GrammarExample("Door (High)", "𐴊𐴝\u200C𐴦𐴌𐴡")
                )
            ),
            GrammarModel(
                name = "Tossi",
                nativeName = "𐴃𐴡𐴏𐴧𞴞",
                scriptSign = "𐴧",
                description = "Gemination sign used to double the consonant sound.",
                nativeDescription = "𐴃𐴡𐴏𐴧𞴞 𐴎𞴞𞴁\u200C𞴝𞴥𞴘𞴠 𞴇𞴝\u200C𞴌𞴉𐴡\u200C𞴌 𞴀\u200C𞴝u200C𞴁\u200C𞴝u200C𞴏𞴡u200C𞴌u200C𞴠 𞴊𞴟u200C𞴁𞴡u200C𞴌𞴝 𞴡\u200C𞴌𞴞𞴥 𞴇𞴡𞴘𞴝 𞴎𞴝𞴘𞴠𞴿",
                example = "𐴄𐴝𐴦𐴌𐴡",
                exampleEnglish = "Darro (Double Sound)",
                accentColor = Color(0xFF3B82F6),
                nativeAudioFile = "tossi_native.mp3",
                nativeWordTimings = listOf(0, 650, 1300, 1950, 2600, 3250, 3900, 4550),
                examples = listOf(
                    GrammarExample("Sit", "𐴁𐴡\u200C𐴏𞴞"),
                    GrammarExample("Stick", "𐴇𐴡\u200C𐴏𞴞"),
                    GrammarExample("Mosque", "𐴔𐴡\u200C𐴏𞴞"),
                    GrammarExample("Case", "𐴑𐴡\u200C𐴏𞴞"),
                    GrammarExample("Yarn", "𐴎𐴡\u200C𐴏𞴞"),
                    GrammarExample("Take", "𐴓𐴡\u200C𐴏𞴞"),
                    GrammarExample("Fruit", "𐴉𐴡\u200C𐴏𞴞"),
                    GrammarExample("Give", "𐴊𐴡\u200C𐴏𞴞"),
                    GrammarExample("Rope", "𐴌𐴡\u200C𐴏𞴞"),
                    GrammarExample("Wait", "𐴃𐴡\u200C𐴏")
                )
            )
        )
    }

    val advancedGrammar = remember {
        listOf(
            GrammarModel(
                name = "Advanced Tones",
                nativeName = "𐴃𐴝𐴕𐴝 𐴃𐴠𐴓𐴝 (𐴤𐴥)",
                scriptSign = "◌𐴤𐴥",
                description = "Complex combining tone markers for inflection.",
                nativeDescription = "𐴏𐴟𐴌𐴡𐴌 𐴓𐴝𐴔𐴝𐴘𞴝 𞴀𞴝 𞴟𞴌𞴠𞴥 𞴃𞴝𞴕𞴞𞴘𞴝 𞴞𞴓𞴝𞴘𞴝 𞴡𞴘𞴝 𞴎𞴝𞴘𞴠𞴿",
                example = "𐴁𐴝𐴤𐴌𐴡𐴥",
                exampleEnglish = "Baro-Tana (Inflected)",
                accentColor = Color(0xFF059669),
                examples = List(10) { i -> GrammarExample("Tone Inflection ${i + 1}", "𐴁𐴝\u200C𐴤𐴌𐴡𐴥") }
            ),
            GrammarModel(
                name = "Sentence Structure",
                nativeName = "𐴎𐴟𐴔𐴓𐴝 𐴁\u200C𐴡𐴕\u200C𐴝𐴕",
                scriptSign = "𐴖𐴕",
                description = "Subject-Object-Verb rule structures in Hanifi script.",
                nativeDescription = "𐴇𐴝𐴕𞴞𞴉𞴞 𞴓𞴠𞴈𞴝 𞴝𞴎𞴠 𞴡𞴘𞴓𞴝 𞴝𞴀𞴠𞴓𢃢 𞴃𞴝𞴌𞴡𞴌 𞴝𞴟𢃢 𞴝𞴈𞴠𞴌𞴡𞴃𢃢 𞴠𢃢 𞴝𞴘𞴠𞴿",
                example = "𐴔𐴝\u200C𐴥 𐴁𐴝\u200C𐴤𐴌𐴡 𐴇𐴝\u200C𐴕𢃢",
                exampleEnglish = "SOV Order Structure",
                accentColor = Color(0xFF10B981),
                examples = listOf(
                    GrammarExample("I eat rice", "𐴔𐴝\u200C𐴥 𐴁𐴝\u200C𐴤𐴌𐴡 𐴇𐴝\u200C\u200C𐴕𢃢"),
                    GrammarExample("He goes home", "𐴇𐴞𐴃𐴠 𐴒\u200C𐴡𐴌\u200C𢃢 𐴎𐴝\u200C𐴘𐴠"),
                    GrammarExample("They play football", "𐴇𐴞𐴃𐴝𐴌𐴝 𐴁\u200C𐴡𐴓𢃢 𐴈𐴠𐴓𐴠"),
                    GrammarExample("She reads a book", "𐴇𐴞𐴃\u200C𐴞𞴘𞴠 𞴑𞴞𞴃𞴝𞴁𢃢 𞴡𞴌𞠠"),
                    GrammarExample("We drink water", "𐴔𐴝\u200C\u200C𐴌𐴝 𐴉𐴝𐴕 𞴝𞴘"),
                    GrammarExample("I see a bird", "𐴔𐴝\u200C\u200C𐴥 𐴉𐴝 "),
                    GrammarExample("You come here", "𐴃𐴟𐴘 𐴇 𞴝𞴘𞴡"),
                    GrammarExample("He loves mother", "𐴇 𞴝𐴥 𞴡𞴇𞴡𞴁𞴧𞴡𞴃𢃢 𞴡𞴌"),
                    GrammarExample("It is raining", "𐴎𐴡𐴌 𐴇𐴡𐴘 𐴓𐴝\u200C𐴒𐴠"),
                    GrammarExample("Sun rises", "𐴁𐴠𐴓𢃢 𐴟𐴑𐴠")
                )
            ),
            GrammarModel(
                name = "Conjunction Flow",
                nativeName = "𐴓𞴞𞴕𢃢𐴒𐴡𐴌𐴡𐴕𞴞",
                scriptSign = "◌𐴦𐴢",
                description = "Flow restrictions between high vowels & gemination.",
                nativeDescription = "𐴀𐴝𐴁𐴝𐴏𐴡𐴌 𐴓𞴞𞴕𢃢 𞴡𐴌𞴡𐴕 𞴝𞴥 𞴡  𢃢 𞴡𞴌𞴥𞴿",
                example = "𐴄𐴝\u200C𐴦𐴌𐴡𢃢",
                exampleEnglish = "Linked Stem Flow",
                accentColor = Color(0xFF047857),
                examples = List(10) { i -> GrammarExample("Flow Case ${i + 1}", "𐴄𐴝\u200C𐴦𐴌𐴡𢃢") }
            )
        )
    }

    val currentList = if (isAdvanced) advancedGrammar else basicGrammar
    var selectedIndex by remember { mutableIntStateOf(0) }
    val selectedItem = currentList.getOrNull(selectedIndex) ?: currentList[0]

    val themeColors = when {
        isAdvanced -> listOf(Color(0xFF059669), Color(0xFF34D399))
        isIntermediate -> listOf(Color(0xFF0F766E), Color(0xFF14B8A6))
        else -> listOf(Color(0xFF4338CA), Color(0xFF818CF8))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(themeColors))
    ) {
        GrammarDynamicMeshHero()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                GrammarTopBar(levelId = levelId, onNavigateBack = onNavigateBack)
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Horizontal Rule Selector (The Tabs)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    currentList.forEachIndexed { index, item ->
                        val isSelected = selectedIndex == index
                        val scale by animateFloatAsState(if (isSelected) 1.1f else 1f, label = "scale")
                        
                        Card(
                            modifier = Modifier
                                .width(95.dp)
                                .height(85.dp)
                                .graphicsLayer { scaleX = scale; scaleY = scale }
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { 
                                        selectedIndex = index 
                                        viewModel.stopAudio()
                                    }
                                ),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color.White else Color.White.copy(alpha = 0.15f)
                            ),
                            border = BorderStroke(
                                width = 3.dp,
                                brush = if (isSelected) {
                                    Brush.linearGradient(
                                        colors = listOf(item.accentColor, Color(0xFFFFD700), Color(0xFF00E5FF))
                                    )
                                } else {
                                    Brush.linearGradient(
                                        colors = listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.1f))
                                    )
                                }
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = item.scriptSign,
                                    fontSize = if (item.scriptSign.length > 2) 14.sp else 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isSelected) item.accentColor else Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = item.name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFF0F172A) else Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 45.dp, topEnd = 45.dp),
                    color = Color(0xFFF8FAFC)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(40.dp)
                                .alpha(0.03f),
                            verticalArrangement = Arrangement.SpaceEvenly,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Rounded.Class, null, modifier = Modifier.size(150.dp))
                            Icon(Icons.Rounded.School, null, modifier = Modifier.size(130.dp))
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 0.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AnimatedContent(
                                targetState = selectedItem,
                                transitionSpec = {
                                    (fadeIn(tween(400)) + scaleIn(initialScale = 0.95f)).togetherWith(fadeOut(tween(300)))
                                },
                                label = "grammar_content"
                            ) { item ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    // Symbol Box with Outline - Adjusted for screenshot request
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .background(item.accentColor.copy(alpha = 0.05f), CircleShape)
                                            .border(
                                                width = 5.dp,
                                                color = Color(0xFF0F172A),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        GrammarPulseIndicator(color = item.accentColor, modifier = Modifier.size(60.dp))
                                        Text(
                                            text = item.scriptSign,
                                            fontSize = if (item.scriptSign.length > 2) 20.sp else 40.sp,
                                            fontWeight = FontWeight.Black,
                                            color = item.accentColor,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Titles
                                    Text(
                                        text = item.name,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF0F172A),
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = item.nativeName,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = item.accentColor,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )

                                    Spacer(modifier = Modifier.height(18.dp))

                                    // Rule Card with Highlighted Audio Support
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(26.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier.size(36.dp).background(item.accentColor.copy(alpha = 0.1f), CircleShape),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(Icons.Rounded.Gavel, null, tint = item.accentColor, modifier = Modifier.size(18.dp))
                                                    }
                                                    Spacer(Modifier.width(10.dp))
                                                    Text(
                                                        text = "GRAMMAR RULE",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = item.accentColor,
                                                        letterSpacing = 1.1.sp
                                                    )
                                                }
                                            }
                                            
                                            Spacer(Modifier.height(12.dp))
                                            
                                            // English Highlighted Text
                                            val englishWords = item.description.split(Regex("\\s+"))
                                            val englishAnnotated = buildAnnotatedString {
                                                englishWords.forEachIndexed { index, word ->
                                                    if (index == highlightedEnglishIndex) {
                                                        withStyle(style = SpanStyle(background = Color(0xFFFFD700), color = Color.Black)) {
                                                            append(word)
                                                        }
                                                    } else {
                                                        append(word)
                                                    }
                                                    if (index < englishWords.size - 1) append(" ")
                                                }
                                            }

                                            Text(
                                                text = englishAnnotated,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B),
                                                lineHeight = 22.sp,
                                                textAlign = TextAlign.Justify,
                                                modifier = Modifier.fillMaxWidth()
                                            )

                                            Spacer(modifier = Modifier.height(10.dp))

                                            // English Audio Button below English text
                                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                                val isEnglishPlaying = playingAudioType == "english" || playingAudioType == "tts"
                                                IconButton(
                                                    onClick = { 
                                                        if (item.englishAudioFile.isNotEmpty()) {
                                                            viewModel.playEnglishSong(item.englishAudioFile, item.englishWordTimings) 
                                                        } else {
                                                            viewModel.speakEnglish(item.description)
                                                        }
                                                    },
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .background(
                                                            if (isEnglishPlaying) Color(0xFFFFD700) else item.accentColor.copy(alpha = 0.12f),
                                                            CircleShape
                                                        )
                                                ) {
                                                    Icon(
                                                        imageVector = if (isEnglishPlaying) Icons.Rounded.MusicNote else Icons.AutoMirrored.Rounded.VolumeUp, 
                                                        contentDescription = "English Audio",
                                                        tint = if (isEnglishPlaying) Color.White else item.accentColor,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                            
                                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
                                            
                                            // Word-by-word Highlighted Native Description with Justify Alignment & RTL
                                            val nativeWords = item.nativeDescription.split(Regex("\\s+"))
                                            val annotatedNative = buildAnnotatedString {
                                                nativeWords.forEachIndexed { index, word ->
                                                    if (index == highlightedNativeIndex) {
                                                        withStyle(style = SpanStyle(background = Color(0xFFFFD700), color = Color.Black)) {
                                                            append(word)
                                                        }
                                                    } else {
                                                        append(word)
                                                    }
                                                    if (index < nativeWords.size - 1) append(" ")
                                                }
                                            }

                                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                                Text(
                                                    text = annotatedNative,
                                                    fontSize = 19.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF334155),
                                                    lineHeight = 28.sp,
                                                    modifier = Modifier.fillMaxWidth(),
                                                    textAlign = TextAlign.Justify
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(14.dp))

                                            // Native Song icon button below Hanifi script
                                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                                val isNativePlaying = playingAudioType == "native"
                                                IconButton(
                                                    onClick = { viewModel.playNativeSong(item.nativeAudioFile, item.nativeWordTimings) },
                                                    modifier = Modifier
                                                        .size(44.dp)
                                                        .background(
                                                            if (isNativePlaying) Color(0xFFFFD700) else item.accentColor.copy(alpha = 0.12f),
                                                            CircleShape
                                                        )
                                                ) {
                                                    Icon(
                                                        imageVector = if (isNativePlaying) Icons.Rounded.MusicNote else Icons.AutoMirrored.Rounded.VolumeUp,
                                                        contentDescription = "Song",
                                                        tint = if (isNativePlaying) Color.White else item.accentColor,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // ENHANCED: 10 Usage Examples Creative Carousel Design
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .background(item.accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Rounded.AutoAwesome, null, tint = item.accentColor, modifier = Modifier.size(22.dp))
                                            }
                                            Spacer(Modifier.width(14.dp))
                                            Column {
                                                Text(
                                                    text = "INTERACTIVE EXAMPLES",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = item.accentColor,
                                                    letterSpacing = 1.2.sp
                                                )
                                                Text(
                                                    text = "Swipe to explore 10 real-world cases",
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(18.dp))

                                        val pagerState = rememberPagerState(pageCount = { item.examples.size })
                                        
                                        HorizontalPager(
                                            state = pagerState,
                                            modifier = Modifier.fillMaxWidth().height(280.dp),
                                            contentPadding = PaddingValues(horizontal = 40.dp),
                                            pageSpacing = 16.dp
                                        ) { page ->
                                            val example = item.examples[page]
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .graphicsLayer {
                                                        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                                        val absOffset = kotlin.math.abs(pageOffset).coerceIn(0f, 1f)
                                                        scaleX = 0.85f + (1f - 0.85f) * (1f - absOffset)
                                                        scaleY = 0.85f + (1f - 0.85f) * (1f - absOffset)
                                                        alpha = 0.6f + (1f - 0.6f) * (1f - absOffset)
                                                        rotationY = pageOffset * 20f
                                                    }
                                                    .shadow(
                                                        elevation = 24.dp,
                                                        shape = RoundedCornerShape(36.dp),
                                                        spotColor = item.accentColor.copy(alpha = 0.5f)
                                                    ),
                                                shape = RoundedCornerShape(36.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                                border = BorderStroke(
                                                    width = 2.5.dp,
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(item.accentColor, Color(0xFFFFD700), Color(0xFF00E5FF))
                                                    )
                                                )
                                            ) {
                                                Column(
                                                    modifier = Modifier.padding(24.dp).fillMaxSize(),
                                                    verticalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    // Header inside card
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Box(
                                                            modifier = Modifier
                                                                .size(28.dp)
                                                                .background(item.accentColor, CircleShape),
                                                            contentAlignment = Alignment.Center
                                                        ) {
                                                            Text("${page + 1}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                        }
                                                        Spacer(Modifier.width(10.dp))
                                                        Text(
                                                            text = "CASE STUDY",
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Black,
                                                            color = Color(0xFF94A3B8),
                                                            letterSpacing = 1.5.sp
                                                        )
                                                        Spacer(Modifier.weight(1f))
                                                        Surface(
                                                            color = item.accentColor.copy(alpha = 0.08f),
                                                            shape = CircleShape,
                                                            onClick = { viewModel.speakEnglish(example.english) }
                                                        ) {
                                                            Icon(Icons.AutoMirrored.Rounded.VolumeUp, null, tint = item.accentColor, modifier = Modifier.padding(8.dp).size(20.dp))
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
                                                            text = example.english,
                                                            fontSize = 19.sp,
                                                            fontWeight = FontWeight.ExtraBold,
                                                            color = Color(0xFF1E293B),
                                                            lineHeight = 24.sp
                                                        )
                                                    }

                                                    // Centered Artistic Divider
                                                    Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(Color(0xFFF1F5F9)))

                                                    // Rohingya Section
                                                    Column(horizontalAlignment = Alignment.End) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(Icons.Rounded.HistoryEdu, null, tint = item.accentColor.copy(alpha = 0.4f), modifier = Modifier.size(16.dp))
                                                            Spacer(Modifier.width(6.dp))
                                                            Text(
                                                                text = "ROHINGYA (HANIFI)",
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = item.accentColor
                                                            )
                                                        }
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.End,
                                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                                        ) {
                                                            Surface(
                                                                color = item.accentColor.copy(alpha = 0.08f),
                                                                shape = CircleShape,
                                                                onClick = { viewModel.speakEnglish(example.native) }
                                                            ) {
                                                                Box(
                                                                    modifier = Modifier.size(36.dp),
                                                                    contentAlignment = Alignment.Center
                                                                ) {
                                                                    // Mini dynamic breathing/living effect box
                                                                    GrammarPulseIndicator(color = item.accentColor, modifier = Modifier.size(36.dp))
                                                                    Icon(
                                                                        Icons.AutoMirrored.Rounded.VolumeUp, 
                                                                        null, 
                                                                        tint = item.accentColor, 
                                                                        modifier = Modifier.size(20.dp)
                                                                    )
                                                                }
                                                            }
                                                            Spacer(Modifier.width(12.dp))
                                                            Text(
                                                                text = example.native,
                                                                fontSize = 32.sp,
                                                                fontWeight = FontWeight.Black,
                                                                color = Color(0xFF0F172A),
                                                                textAlign = TextAlign.End
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        // Modern Indicators
                                        Row(
                                            Modifier.wrapContentHeight().fillMaxWidth().padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            repeat(item.examples.size) { iteration ->
                                                val isSelected = pagerState.currentPage == iteration
                                                val width by animateDpAsState(if (isSelected) 28.dp else 10.dp, label = "width")
                                                val height by animateDpAsState(if (isSelected) 10.dp else 10.dp, label = "height")
                                                val color = if (isSelected) item.accentColor else item.accentColor.copy(alpha = 0.2f)
                                                Box(
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .clip(CircleShape)
                                                        .background(color)
                                                        .size(width = width, height = height)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GrammarPulseIndicator(color: Color, modifier: Modifier = Modifier.size(110.dp)) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.5f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Restart), label = "scale"
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(2200), RepeatMode.Restart), label = "alpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .background(color.copy(alpha = 0.25f), CircleShape)
    )
}

@Composable
fun GrammarDynamicMeshHero() {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val animValue by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10000, easing = LinearEasing), RepeatMode.Reverse),
        label = "anim"
    )

    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        Box(
            modifier = Modifier.size(380.dp).align(Alignment.TopEnd).offset(x = 160.dp, y = (-120).dp + (animValue * 40).dp)
                .background(Color.White.copy(alpha = 0.08f), CircleShape)
        )
        Box(
            modifier = Modifier.size(220.dp).align(Alignment.BottomStart).offset(x = (-70).dp, y = 60.dp - (animValue * 30).dp)
                .rotate(animValue * 45f).background(Color.White.copy(alpha = 0.04f), RoundedCornerShape(50.dp))
        )
    }
}

@Composable
fun GrammarTopBar(levelId: String, onNavigateBack: () -> Unit) {
    val levelText = when(levelId) {
        "3" -> "LEVEL 03"
        else -> "LEVEL 02"
    }
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(levelText, color = Color(0xFFFFD700), fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text("Grammar Guide", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
            }

            Spacer(Modifier.size(48.dp))
        }
    }
}

data class GrammarExample(
    val english: String,
    val native: String
)

data class GrammarModel(
    val name: String,
    val nativeName: String,
    val scriptSign: String,
    val description: String,
    val nativeDescription: String,
    val example: String,
    val exampleEnglish: String,
    val accentColor: Color,
    val nativeAudioFile: String = "",
    val nativeWordTimings: List<Long> = emptyList(),
    val englishAudioFile: String = "",
    val englishWordTimings: List<Long> = emptyList(),
    val examples: List<GrammarExample> = emptyList()
)
