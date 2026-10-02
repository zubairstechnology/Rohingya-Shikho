package com.zubtech.rohingyashikho.presentation.level

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zubtech.rohingyashikho.R
import java.util.Locale

data class VocabularyItem(
    val id: String? = null,
    val english: String? = null,
    val rohingyaHanifi: String? = null,
    val imageRes: Int = 0,
    val category: String? = null,
    val rohingyaAudioUrl: String? = null,
    val imageUrl: String? = null
)

data class VocabularyCategory(
    val name: String,
    val icon: ImageVector,
    val wordCount: Int,
    val gradientColors: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdvancedVocabularyScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var selectedCategory by remember { mutableStateOf<VocabularyCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    
    val vocabularyItems = remember { loadVocabularyFromAssets(context) }

    DisposableEffect(Unit) {
        val ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Success
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
                        text = if (selectedCategory == null) "Vocabulary sets" else selectedCategory!!.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedCategory != null) {
                                selectedCategory = null
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color(0xFFF1F5F9), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back", tint = Color(0xFF1E293B))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (selectedCategory == null) {
                CategorySelectionContent(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    onCategoryClick = { selectedCategory = it }
                )
            } else {
                WordListContent(
                    category = selectedCategory!!,
                    allVocabularyItems = vocabularyItems,
                    onSpeakEnglish = { text ->
                        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    onPlayRohingya = { url ->
                        playAudio(url)
                    }
                )
            }
        }
    }
}

fun loadVocabularyFromAssets(context: Context): List<VocabularyItem> {
    return try {
        val jsonString = context.assets.open("vocabulary.json").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<VocabularyItem>>() {}.type
        val allItems: List<VocabularyItem> = Gson().fromJson(jsonString, listType)
        allItems.filter { it.id != null && it.english != null }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectionContent(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onCategoryClick: (VocabularyCategory) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val searchBarBorderBrush = if (isFocused) {
        Brush.linearGradient(listOf(Color(0xFF4A68FF), Color(0xFF8B5CF6)))
    } else {
        Brush.linearGradient(listOf(Color(0xFFE2E8F0), Color(0xFFE2E8F0)))
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .shadow(if (isFocused) 12.dp else 4.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF4A68FF).copy(alpha = 0.2f))
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .border(3.dp, searchBarBorderBrush, RoundedCornerShape(20.dp))
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search collections...", color = Color(0xFF94A3B8), fontWeight = FontWeight.Black) },
                    leadingIcon = { 
                        Icon(
                            Icons.Rounded.Search, 
                            contentDescription = null, 
                            tint = if (isFocused) Color(0xFF4A68FF) else Color(0xFF94A3B8),
                            modifier = Modifier.size(24.dp)
                        ) 
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Rounded.Close, null, tint = Color(0xFF94A3B8))
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    interactionSource = interactionSource
                )
            }
        }

        item {
            Text(
                text = "Choose a collection",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E293B)
            )
        }

        item {
            val filteredCategories = mockCategories.filter { 
                searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true)
            }
            
            Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                filteredCategories.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        rowItems.forEach { category ->
                            CategoryCard(
                                category = category,
                                modifier = Modifier.weight(1f),
                                onClick = { onCategoryClick(category) }
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: VocabularyCategory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(135.dp)
            .clickable(onClick = onClick)
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(category.gradientColors),
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(category.gradientColors.first().copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp),
                        tint = category.gradientColors.first()
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    text = category.name,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
            
            Surface(
                modifier = Modifier.align(Alignment.BottomEnd),
                color = category.gradientColors.first().copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${category.wordCount}",
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = category.gradientColors.first()
                )
            }
        }
    }
}

@Composable
fun WordListContent(
    category: VocabularyCategory, 
    allVocabularyItems: List<VocabularyItem>,
    onSpeakEnglish: (String) -> Unit,
    onPlayRohingya: (String?) -> Unit
) {
    val items = allVocabularyItems.filter { 
        it.category?.trim()?.equals(category.name.trim(), ignoreCase = true) == true 
    }
    
    if (items.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "No words found in \"${category.name}\"",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(items) { item ->
                WordRowCard(
                    item = item, 
                    gradientColors = category.gradientColors,
                    onSpeakEnglish = onSpeakEnglish, 
                    onPlayRohingya = onPlayRohingya
                )
            }
        }
    }
}

@Composable
fun WordRowCard(
    item: VocabularyItem, 
    gradientColors: List<Color>,
    onSpeakEnglish: (String) -> Unit,
    onPlayRohingya: (String?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 3.dp,
                brush = Brush.linearGradient(gradientColors),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageModifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
            
            Image(
                painter = painterResource(id = if (item.imageRes != 0) item.imageRes else R.drawable.every_day),
                contentDescription = null,
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )
            
            Spacer(Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.english ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E293B)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = item.rohingyaHanifi ?: "",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = gradientColors.first(),
                    lineHeight = 32.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = { onSpeakEnglish(item.english ?: "") },
                    modifier = Modifier.size(36.dp).background(Color(0xFFEFF6FF), CircleShape).border(1.5.dp, Color(0xFF3B82F6), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Speak English",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF3B82F6)
                    )
                }
                IconButton(
                    onClick = { onPlayRohingya(item.rohingyaAudioUrl) },
                    modifier = Modifier.size(36.dp).background(Color(0xFFF0FDF4), CircleShape).border(1.5.dp, Color(0xFF10B981), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                        contentDescription = "Speak Rohingya",
                        modifier = Modifier.size(18.dp),
                        tint = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

val mockCategories = listOf(
    VocabularyCategory("People & Family", Icons.Rounded.People, 10, listOf(Color(0xFFF43F5E), Color(0xFFFB7185))),
    VocabularyCategory("Home & Daily Life", Icons.Rounded.Home, 10, listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))),
    VocabularyCategory("Food & Drinks", Icons.Rounded.Restaurant, 10, listOf(Color(0xFFF59E0B), Color(0xFFFBBF24))),
    VocabularyCategory("Nature & Environment", Icons.Rounded.Park, 10, listOf(Color(0xFF10B981), Color(0xFF34D399))),
    VocabularyCategory("Clothes & Personal Items", Icons.Rounded.Checkroom, 10, listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA))),
    VocabularyCategory("Education", Icons.Rounded.School, 10, listOf(Color(0xFF6366F1), Color(0xFF818CF8))),
    VocabularyCategory("Health & Body", Icons.Rounded.HealthAndSafety, 10, listOf(Color(0xFFEF4444), Color(0xFFF87171))),
    VocabularyCategory("Religion & Culture", Icons.Rounded.AccountBalance, 10, listOf(Color(0xFF0D9488), Color(0xFF2DD4BF))),
    VocabularyCategory("Actions & Activities", Icons.AutoMirrored.Rounded.DirectionsRun, 10, listOf(Color(0xFFEA580C), Color(0xFFFB923C))),
    VocabularyCategory("Feelings & Emotions", Icons.Rounded.EmojiEmotions, 10, listOf(Color(0xFFD946EF), Color(0xFFE879F9))),
    VocabularyCategory("Time & Numbers", Icons.Rounded.Schedule, 10, listOf(Color(0xFF64748B), Color(0xFF94A3B8))),
    VocabularyCategory("Places & Travel", Icons.Rounded.Flight, 10, listOf(Color(0xFF06B6D4), Color(0xFF22D3EE))),
    VocabularyCategory("Animals", Icons.Rounded.Pets, 10, listOf(Color(0xFF84CC16), Color(0xFFA3E635)))
)
