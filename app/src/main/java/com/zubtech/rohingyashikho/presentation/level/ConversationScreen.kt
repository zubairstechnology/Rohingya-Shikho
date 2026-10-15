package com.zubtech.rohingyashikho.presentation.level

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.audio.AudioPlayer
import com.zubtech.rohingyashikho.data.audio.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class Dialogue(
    val english: String,
    val rohingya: String,
    val speaker: String, // "A" or "B"
    val englishAudioUrl: String? = null,
    val rohingyaAudioUrl: String? = null
)

data class ConversationSet(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val dialogues: List<Dialogue>
)

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val ttsManager: TtsManager,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    fun playAudio(url: String?, text: String) {
        if (!url.isNullOrEmpty()) {
            audioPlayer.playUrl(url)
        } else {
            viewModelScope.launch {
                ttsManager.speak(text)
            }
        }
    }

    fun playRohingya(url: String?) {
        if (url.isNullOrEmpty()) return
        audioPlayer.playUrl(url)
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
        ttsManager.stop()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    onNavigateBack: () -> Unit,
    viewModel: ConversationViewModel = hiltViewModel()
) {
    var selectedSet by remember { mutableStateOf<ConversationSet?>(null) }

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
                    onPlayEnglish = { url, text -> viewModel.playAudio(url, text) },
                    onPlayRohingya = { url -> viewModel.playRohingya(url) }
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
        ConversationSet("Travel & Directions", Icons.Rounded.Flight, Color(0xFFF59E0B), mockTravel),
        ConversationSet("Family & Home", Icons.Rounded.FamilyRestroom, Color(0xFF8B5CF6), mockFamily),
        ConversationSet("Weather & Nature", Icons.Rounded.Cloud, Color(0xFF06B6D4), mockWeather),
        ConversationSet("Time & Date", Icons.Rounded.Schedule, Color(0xFF14B8A6), mockTime),
        ConversationSet("Education", Icons.Rounded.School, Color(0xFFF97316), mockEducation),
        ConversationSet("Work & Jobs", Icons.Rounded.Work, Color(0xFF84cc16), mockWork),
        ConversationSet("Hobbies", Icons.Rounded.SportsSoccer, Color(0xFFef4444), mockHobbies),
        ConversationSet("Emergency", Icons.Rounded.Warning, Color(0xFFD32F2F), mockEmergency),
        ConversationSet("Home Items", Icons.Rounded.Home, Color(0xFF795548), mockHomeItems),
        ConversationSet("Emotions", Icons.Rounded.Mood, Color(0xFFFF4081), mockEmotions),
        ConversationSet("Technology", Icons.Rounded.Computer, Color(0xFF607D8B), mockTech),
        ConversationSet("Animals", Icons.Rounded.Pets, Color(0xFF4CAF50), mockAnimals),
        ConversationSet("Colors", Icons.Rounded.Palette, Color(0xFFFFC107), mockColors),
        ConversationSet("Numbers", Icons.Rounded.Numbers, Color(0xFF2196F3), mockNumbers),
        ConversationSet("Culture", Icons.Rounded.Public, Color(0xFF9C27B0), mockCulture)
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
    onPlayEnglish: (String?, String) -> Unit,
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
                    AvatarIcon(bubbleColor, "A")
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
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LanguagePlayButton(
                                    label = "En",
                                    icon = Icons.Rounded.VolumeUp,
                                    activeColor = Color(0xFF6366F1),
                                    onClick = { onPlayEnglish(dialogue.englishAudioUrl, dialogue.english) }
                                )
                                
                                LanguagePlayButton(
                                    label = "Rh",
                                    icon = Icons.Rounded.VolumeUp,
                                    activeColor = Color(0xFF10B981),
                                    onClick = { onPlayRohingya(dialogue.rohingyaAudioUrl) }
                                )
                            }
                            
                            Spacer(Modifier.height(20.dp))
                            
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = dialogue.rohingya,
                                    color = Color(0xFF0F172A),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black,
                                    lineHeight = 32.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            
                            Spacer(Modifier.height(10.dp))
                            
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
                    AvatarIcon(bubbleColor, "B")
                }
            }
        }
    }
}

@Composable
fun AvatarIcon(color: Color, label: String) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(2.dp, color)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, fontWeight = FontWeight.Bold, color = color)
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

val mockGreetings = emptyList<Dialogue>()
val mockDaily = emptyList<Dialogue>()
val mockFood = emptyList<Dialogue>()
val mockShopping = emptyList<Dialogue>()
val mockHospital = emptyList<Dialogue>()
val mockTravel = emptyList<Dialogue>()
val mockFamily = emptyList<Dialogue>()
val mockWeather = emptyList<Dialogue>()
val mockTime = emptyList<Dialogue>()
val mockEducation = emptyList<Dialogue>()
val mockWork = emptyList<Dialogue>()
val mockHobbies = emptyList<Dialogue>()
val mockGreetingsSet = listOf(
    Dialogue("Hello", "𐴀𐴝𐴏𐴧𐴝𐴓𐴝𐴔𐴟 𐴀𐴝𐴓𐴝𐴙𐴑𐴟𐴔", "A"),
    Dialogue("Hello (Response)", "𐴖𐴝𐴓𐴝𐴙𐴑𐴟𐴔 𐴀𐴝𐴏𐴧𐴝𐴓𐴝𐴔", "B"),
    Dialogue("How are you?", "𐴑𐴠𐴕𐴠 𐴀𐴝𐴌?", "A"),
    Dialogue("I am fine, thank you.", "𐴔𐴟𐴦𐴙 𐴁𐴝𐴓𐴝 𐴀𐴝𐴌, 𐴐𐴟𐴑𐴟𐴌𐴙𐴝𐴒𐴟𐴎𐴝𐴌", "B"),
    Dialogue("What is your name?", "𐴀𐴞𐴕𐴞 𐴕𐴝𐴔 𐴑𐴞?", "A"),
    Dialogue("My name is Zubair.", "𐴀𐴝𐴦𐴙 𐴕𐴝𐴔 𐴎𐴟𐴁𐴝𐴙𐴌", "B"),
    Dialogue("Nice to meet you.", "𐴀𐴞𐴕𐴞 𐴓𐴡𐴒𐴠 𐴓𐴡𐴑 𐴀𐴡𐴙 𐴁𐴝𐴓𐴝 𐴓𐴝𐴒𐴙𐴓", "A"),
    Dialogue("Nice to meet you too.", "𐴔𐴟𐴦𐴙 𐴁𐴞 𐴁𐴝𐴓𐴝 𐴓𐴝𐴒𐴙𐴓", "B")
)
val mockEmergency = emptyList<Dialogue>()
val mockHomeItems = emptyList<Dialogue>()
val mockEmotions = emptyList<Dialogue>()
val mockTech = emptyList<Dialogue>()
val mockAnimals = emptyList<Dialogue>()
val mockColors = emptyList<Dialogue>()
val mockNumbers = emptyList<Dialogue>()
val mockCulture = emptyList<Dialogue>()
