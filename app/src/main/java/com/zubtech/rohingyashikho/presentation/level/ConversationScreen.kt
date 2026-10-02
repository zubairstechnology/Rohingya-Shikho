package com.zubtech.rohingyashikho.presentation.level

import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.random.Random

data class Dialogue(
    val english: String,
    val rohingya: String,
    val speaker: String, // "A" or "B"
    val audioUrl: String? = null
)

data class ConversationSet(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val dialogues: List<Dialogue>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var selectedSet by remember { mutableStateOf<ConversationSet?>(null) }
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Initialize TTS
    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Initialized
            }
        }
        ttsInstance.language = Locale.US
        tts = ttsInstance
        onDispose {
            ttsInstance.stop()
            ttsInstance.shutdown()
            mediaPlayer?.release()
        }
    }

    fun playAudio(url: String?) {
        if (url.isNullOrEmpty()) return
        mediaPlayer?.stop()
        mediaPlayer?.release()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener { start() }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        selectedSet?.title ?: "Conversations",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { if (selectedSet != null) selectedSet = null else onNavigateBack() },
                        modifier = Modifier.padding(8.dp).background(Color(0xFFF1F5F9), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null, tint = Color(0xFF1E293B))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            WatermarkBackground()

            if (selectedSet == null) {
                ConversationGrid(onSetClick = { selectedSet = it })
            } else {
                DialogueChat(
                    set = selectedSet!!,
                    onSpeakEnglish = { text -> tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null) },
                    onPlayRohingya = { url -> playAudio(url) }
                )
            }
        }
    }
}

@Composable
fun WatermarkBackground() {
    val icons = listOf(
        Icons.Rounded.Chat, Icons.Rounded.Forum, Icons.Rounded.Translate,
        Icons.Rounded.RecordVoiceOver, Icons.Rounded.GraphicEq, Icons.Rounded.InterpreterMode,
        Icons.Rounded.Message, Icons.Rounded.Phone, Icons.Rounded.VoiceChat,
        Icons.Rounded.SpeakerNotes, Icons.Rounded.MarkChatUnread, Icons.Rounded.QuestionAnswer,
        Icons.Rounded.ContactSupport, Icons.Rounded.Quickreply, Icons.Rounded.Textsms,
        Icons.Rounded.Hearing, Icons.Rounded.Spellcheck, Icons.Rounded.Language,
        Icons.Rounded.AutoStories, Icons.Rounded.MenuBook, Icons.Rounded.Mic,
        Icons.Rounded.VolumeUp, Icons.Rounded.Headset, Icons.Rounded.School,
        Icons.Rounded.CastForEducation, Icons.Rounded.HistoryEdu, Icons.Rounded.Lightbulb
    )
    
    val colors = listOf(
        Color(0xFF6366F1), Color(0xFF10B981), Color(0xFFF43F5E), 
        Color(0xFFEC4899), Color(0xFFF59E0B), Color(0xFF3B82F6),
        Color(0xFF8B5CF6), Color(0xFF06B6D4), Color(0xFF14B8A6),
        Color(0xFFF97316), Color(0xFF84cc16), Color(0xFFef4444),
        Color(0xFF7C3AED), Color(0xFFDB2777), Color(0xFF2563EB)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize().alpha(0.032f)) {
        val width = maxWidth
        val height = maxHeight
        
        val iconPositions = remember(width, height) {
            val random = Random(42)
            List(85) {
                WatermarkIconData(
                    icon = icons[random.nextInt(icons.size)],
                    color = colors[random.nextInt(colors.size)],
                    size = random.nextInt(24, 46).dp,
                    x = (random.nextFloat() * width.value).dp,
                    y = (random.nextFloat() * height.value).dp,
                    rotation = random.nextFloat() * 360f
                )
            }
        }

        iconPositions.forEach { data ->
            Icon(
                imageVector = data.icon,
                contentDescription = null,
                tint = data.color,
                modifier = Modifier
                    .size(data.size)
                    .offset(x = data.x, y = data.y)
                    .rotate(data.rotation)
            )
        }
    }
}

private data class WatermarkIconData(
    val icon: ImageVector,
    val color: Color,
    val size: Dp,
    val x: Dp,
    val y: Dp,
    val rotation: Float
)

@Composable
fun ConversationGrid(onSetClick: (ConversationSet) -> Unit) {
    val sets = listOf(
        ConversationSet("Greetings & Intro", Icons.Rounded.WavingHand, Color(0xFF6366F1), mockGreetings),
        ConversationSet("Daily Activities", Icons.Rounded.Today, Color(0xFF10B981), mockDaily),
        ConversationSet("Food & Restaurant", Icons.Rounded.Restaurant, Color(0xFFF43F5E), mockFood),
        ConversationSet("Shopping & Market", Icons.Rounded.ShoppingBag, Color(0xFFEC4899), mockShopping),
        ConversationSet("Health & Hospital", Icons.Rounded.LocalHospital, Color(0xFFEF4444), mockHospital),
        ConversationSet("Travel & Directions", Icons.Rounded.Flight, Color(0xFFF59E0B), mockTravel)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sets) { set ->
            Card(
                onClick = { onSetClick(set) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(listOf(set.color, set.color.copy(alpha = 0.6f))),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        color = set.color.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Icon(set.icon, null, modifier = Modifier.padding(14.dp), tint = set.color)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(set.title, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF1E293B))
                        Text("${set.dialogues.size} sentences", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Rounded.ChevronRight, null, tint = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun DialogueChat(
    set: ConversationSet,
    onSpeakEnglish: (String) -> Unit,
    onPlayRohingya: (String?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp, top = 20.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        items(set.dialogues) { dialogue ->
            val isA = dialogue.speaker == "A"
            val bubbleColor = if (isA) Color(0xFF6366F1) else Color(0xFF10B981)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if (isA) Arrangement.Start else Arrangement.End,
                verticalAlignment = Alignment.Top
            ) {
                if (isA) {
                    AvatarIcon(Color(0xFF6366F1), "A")
                    Spacer(Modifier.width(12.dp))
                }

                Column(
                    modifier = Modifier.weight(1f, fill = false),
                    horizontalAlignment = if (isA) Alignment.Start else Alignment.End
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp,
                            bottomStart = if (isA) 4.dp else 24.dp,
                            bottomEnd = if (isA) 24.dp else 4.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isA) Color.White.copy(alpha = 0.94f) else Color(0xFFF1F5F9).copy(alpha = 0.94f)
                        ),
                        border = BorderStroke(
                            width = 3.dp,
                            brush = Brush.linearGradient(listOf(bubbleColor, bubbleColor.copy(alpha = 0.5f)))
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        modifier = Modifier.widthIn(max = 300.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header Row with En and Rh buttons at corners
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LanguagePlayButton(
                                    label = "En",
                                    icon = Icons.Rounded.VolumeUp,
                                    activeColor = Color(0xFF6366F1),
                                    onClick = { onSpeakEnglish(dialogue.english) }
                                )
                                
                                LanguagePlayButton(
                                    label = "Rh",
                                    icon = Icons.Rounded.VolumeUp,
                                    activeColor = Color(0xFF10B981),
                                    onClick = { onPlayRohingya(dialogue.audioUrl) }
                                )
                            }
                            
                            Spacer(Modifier.height(20.dp))
                            
                            // Rohingya Text with RTL Support
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = dialogue.rohingya,
                                    color = Color(0xFF0F172A),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    lineHeight = 32.sp,
                                    textAlign = TextAlign.Start, // Start in RTL is Right
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            
                            Spacer(Modifier.height(10.dp))
                            
                            // English Text (LTR)
                            Text(
                                text = dialogue.english,
                                color = Color(0xFF475569),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                if (!isA) {
                    Spacer(Modifier.width(12.dp))
                    AvatarIcon(set.color, "B")
                }
            }
        }
    }
}

@Composable
fun LanguagePlayButton(
    label: String,
    icon: ImageVector,
    activeColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(14.dp),
        color = activeColor.copy(alpha = 0.08f),
        modifier = Modifier
            .height(38.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale),
        border = BorderStroke(1.5.dp, activeColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = activeColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = activeColor,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
fun AvatarIcon(color: Color, label: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(color.copy(alpha = 0.1f), CircleShape)
            .border(2.5.dp, color, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Rounded.Person, null, tint = color, modifier = Modifier.size(26.dp))
        Surface(
            color = color,
            shape = CircleShape,
            modifier = Modifier.align(Alignment.BottomEnd).size(18.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    label, 
                    color = Color.White, 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

// Mock Data
val mockGreetings = listOf(
    Dialogue("Hello, how are you?", "𐴀𐴝𐴙𐴓𐴝𐴔 𐴀𐴝𐴓𐴝𐴙𐴑𐴟𐴔, 𐴑𐴝𐴕 𐴀𐴝𐴔", "A"),
    Dialogue("I am fine, thank you.", "𐴔𐴟𐴙𐴕 𐴁𐴝𐴓𐴝 𐴀𐴝𐴔, 𐴀𐴞𐴕𐴓𐴝", "B", "https://example.com/audio1.mp3"),
    Dialogue("What is your name?", "𐴀𐴡𐴕𐴡𐴙 𐴕𐴝𐴔 𐴑𐴞?", "A"),
    Dialogue("My name is Rohim.", "𐴀𐴝𐴕𐴝𐴔 𐴕𐴝𐴔 𐴑𐴝𐴔", "B", "https://example.com/audio2.mp3"),
    Dialogue("Where are you from?", "𐴀𐴡𐴕𐴡𐴙 𐴑𐴡𐴙𐴕𐴊𐴝 𐴀𐴝𐴔?", "A"),
    Dialogue("I am from Cox's Bazar.", "𐴔𐴟𐴙𐴕 𐴑𐴡𐴑𐴁𐴝𐴎𐴝𐴔 𐴀𐴝𐴔", "B", "https://example.com/audio3.mp3"),
    Dialogue("How old are you?", "𐴀𐴡𐴕𐴡𐴙𐴓𐴝 𐴁𐴡𐴙𐴡𐴙 𐴑𐴡𐴔?", "A"),
    Dialogue("I am twenty years old.", "𐴔𐴟𐴙𐴕 𐴑𐴟𐴊 𐴁𐴡𐴙𐴡𐴙 𐴀𐴝𐴔", "B", "https://example.com/audio4.mp3"),
    Dialogue("Nice to meet you.", "𐴀𐴡𐴕𐴡𐴙𐴓𐴝 𐴔𐴞𐴓𐴞 𐴑𐴟𐴔𐴞 𐴓𐴝𐴒𐴞𐴓", "A")
)

val mockDaily = listOf(
    Dialogue("What time is it now?", "𐴑𐴡𐴔 𐴁𐴝𐴙𐴔 𐴀𐴡𐴙𐴒𐴞?", "A"),
    Dialogue("It is ten o'clock.", "𐴊𐴡𐴙 𐴁𐴝𐴙𐴔 𐴀𐴡𐴙𐴒𐴞", "B", "https://example.com/daily1.mp3"),
    Dialogue("What are you doing?", "𐴀𐴡𐴕𐴡𐴙 𐴑𐴞 𐴑𐴝𐴔 𐴒𐴡𐴙?", "A"),
    Dialogue("I am reading a book.", "𐴔𐴟𐴙𐴕 𐴑𐴞𐴔𐴝𐴕 𐴉𐴡𐴙𐴔", "B", "https://example.com/daily2.mp3"),
    Dialogue("Are you going outside?", "𐴀𐴡𐴕𐴡𐴙 𐴁𐴝𐴙𐴔 𐴎𐴝𐴙𐴁𐴝 𐴑𐴞?", "A"),
    Dialogue("Yes, I am going to the market.", "𐴇𐴡𐴙, 𐴔𐴟𐴙𐴕 𐴁𐴝𐴔𐴝𐴊 𐴎𐴝𐴙𐴔", "B", "https://example.com/daily3.mp3"),
    Dialogue("When will you come back?", "𐴀𐴡𐴕𐴡𐴙 𐴑𐴡𐴔 𐴁𐴝𐴙𐴔 𐴀𐴝𐴙𐴁𐴝?", "A"),
    Dialogue("I will be back in an hour.", "𐴀𐴞𐴙 𐴒𐴡𐴕𐴂𐴝 𐴓𐴝𐴒𐴞𐴁𐴡", "B", "https://example.com/daily4.mp3")
)

val mockFood = listOf(
    Dialogue("I am hungry.", "𐴔𐴟𐴙𐴕 𐴂𐴟𐴓 𐴓𐴝𐴒𐴞𐴓", "A"),
    Dialogue("Let's go to a restaurant.", "𐴀𐴝𐴙 𐴇𐴡𐴂𐴝𐴕 𐴎𐴝𐴙", "B", "https://example.com/food1.mp3"),
    Dialogue("I want some rice and fish.", "𐴔𐴟𐴙𐴕 𐴁𐴝𐴙𐴕 𐴀𐴝𐴔 𐴔𐴝𐴙 𐴑𐴝𐴙𐴔", "A"),
    Dialogue("Do you want water or juice?", "𐴀𐴡𐴕𐴡𐴙 𐴉𐴝𐴕𐴞 𐴑𐴝𐴙𐴁𐴝 𐴑𐴞 𐴎𐴟𐴔?", "B", "https://example.com/food2.mp3"),
    Dialogue("Water is enough for me.", "𐴉𐴝𐴕𐴞 𐴁𐴝𐴓𐴝 𐴀𐴝𐴔", "A"),
    Dialogue("Is the food spicy?", "𐴑𐴝𐴕𐴝 𐴎𐴝𐴓 𐴑𐴞?", "B", "https://example.com/food3.mp3"),
    Dialogue("No, it is very delicious.", "𐴕𐴝, 𐴁𐴡𐴙𐴔 𐴔𐴞𐴒𐴞", "A"),
    Dialogue("I am full now.", "𐴔𐴟𐴙𐴕 𐴉𐴞𐴊 𐴁𐴡𐴔 𐴀𐴡𐴙𐴒𐴞", "B", "https://example.com/food4.mp3")
)

val mockShopping = listOf(
    Dialogue("How much is this shirt?", "𐴀𐴞 𐴁𐴝𐴙𐴔 𐴑𐴡𐴔 𐴑𐴞?", "A"),
    Dialogue("It is 500 Taka.", "𐴀𐴞𐴁𐴝 𐴉𐴝𐴙 𐴑𐴡𐴔", "B", "https://example.com/shop1.mp3"),
    Dialogue("Can you give a discount?", "𐴑𐴡𐴔 𐴒𐴡𐴙 𐴓𐴝𐴙?", "A"),
    Dialogue("I can give it for 450.", "𐴔𐴟𐴙𐴕 𐴂𐴝𐴙 𐴉𐴡𐴕𐴎𐴝𐴙 𐴓𐴡𐴔", "B", "https://example.com/shop2.mp3"),
    Dialogue("Do you have shoes?", "𐴀𐴡𐴕𐴡𐴙𐴓𐴝 𐴑𐴝𐴔𐴞𐴙𐴕 𐴀𐴝𐴔 𐴑𐴞?", "A"),
    Dialogue("Yes, what size?", "𐴇𐴡𐴙, 𐴑𐴡𐴔 𐴑𐴟𐴊𐴞?", "B", "https://example.com/shop3.mp3"),
    Dialogue("I need size nine.", "𐴔𐴟𐴙𐴕 𐴕𐴡 𐴑𐴟𐴊𐴞 𐴓𐴝𐴒𐴞𐴔", "A"),
    Dialogue("Thank you, come again.", "𐴀𐴞𐴕𐴓𐴝, 𐴀𐴝𐴙𐴕𐴊𐴝 𐴀𐴝𐴙𐴙", "B", "https://example.com/shop4.mp3")
)

val mockHospital = listOf(
    Dialogue("I feel sick.", "𐴔𐴟𐴙𐴕 𐴁𐴞𐴔𐴝𐴔 𐴓𐴝𐴒𐴞", "A"),
    Dialogue("Where does it hurt?", "𐴑𐴡𐴙𐴕𐴊𐴝 𐴑𐴝𐴔 𐴒𐴡𐴙?", "B", "https://example.com/hosp1.mp3"),
    Dialogue("My head hurts.", "𐴀𐴝𐴕𐴝𐴔 𐴔𐴝𐴔𐴝 𐴑𐴝 𐴒𐴡𐴙", "A"),
    Dialogue("You should see a doctor.", "𐴀𐴡𐴕𐴡𐴙 𐴊𐴝𐴓𐴝𐴔 𐴓𐴝 𐴎𐴝𐴙", "B", "https://example.com/hosp2.mp3"),
    Dialogue("I have a fever.", "𐴔𐴟𐴙𐴕 𐴒𐴝 𐴒𐴟𐴔", "A"),
    Dialogue("Take this medicine.", "𐴀𐴞 𐴊𐴝𐴔𐴝 𐴑𐴝𐴙", "B", "https://example.com/hosp3.mp3"),
    Dialogue("When should I take it?", "𐴑𐴡𐴔 𐴀𐴞𐴙 𐴑𐴝𐴙𐴔?", "A"),
    Dialogue("After eating.", "𐴑𐴝𐴕𐴝 𐴑𐴝𐴙 𐴓𐴝𐴙", "B", "https://example.com/hosp4.mp3")
)

val mockTravel = listOf(
    Dialogue("Where is the bus stop?", "𐴁𐴝𐴙 𐴑𐴡𐴙𐴕𐴊𐴝 𐴇𐴝𐴔?", "A"),
    Dialogue("Go straight and turn left.", "𐴇𐴞𐴊𐴝 𐴎𐴝𐴙 𐴁𐴝𐴙 𐴔𐴟𐴙𐴕", "B", "https://example.com/travel1.mp3"),
    Dialogue("I want a taxi.", "𐴔𐴟𐴙𐴕 𐴀𐴞𐴙 𐴒𐴝𐴊𐴞 𐴓𐴝𐴒𐴞𐴔", "A"),
    Dialogue("How far is the city?", "𐴑𐴝𐴔𐴝 𐴑𐴡𐴔 𐴊𐴟𐴙?", "B", "https://example.com/travel2.mp3"),
    Dialogue("It is ten miles away.", "𐴊𐴡𐴙 𐴔𐴞𐴙𐴓 𐴊𐴟𐴙", "A"),
    Dialogue("Stop here, please.", "𐴀𐴞𐴙 𐴇𐴝𐴔 𐴊𐴞𐴕", "B", "https://example.com/travel3.mp3"),
    Dialogue("Is this the right way?", "𐴀𐴞𐴁𐴝 𐴇𐴞𐴊𐴝 𐴑𐴝𐴔 𐴑𐴞?", "A"),
    Dialogue("Yes, keep going.", "𐴇𐴡𐴙, 𐴎𐴝𐴙 𐴓𐴝𐴒𐴞", "B", "https://example.com/travel4.mp3")
)
