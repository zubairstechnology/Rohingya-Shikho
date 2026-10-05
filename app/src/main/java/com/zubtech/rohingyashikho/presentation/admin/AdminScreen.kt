package com.zubtech.rohingyashikho.presentation.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.zubtech.rohingyashikho.domain.model.AppUpdateInfo
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isAdmin by viewModel.isAdmin.collectAsState()
    
    var isChecking by remember { mutableStateOf(true) }

    LaunchedEffect(isAdmin) {
        if (isAdmin) {
            isChecking = false
        } else {
            delay(800) 
            if (!isAdmin) {
                onNavigateBack()
            } else {
                isChecking = false
            }
        }
    }

    if (isChecking) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color(0xFF4F46E5))
                Spacer(Modifier.height(16.dp))
                Text("Verifying Admin Access...", color = Color.Gray, fontSize = 14.sp)
            }
        }
        return
    }

    var textFieldValue by remember { mutableStateOf(TextFieldValue("")) }
    var updateUrl by remember { mutableStateOf("") }
    var fontSize by remember { mutableStateOf(16) }
    var isGlobalBold by remember { mutableStateOf(false) }
    var isGlobalItalic by remember { mutableStateOf(false) }
    var globalTextColor by remember { mutableStateOf(Color.Black) }
    var globalHighlightColor by remember { mutableStateOf(Color.Transparent) }
    var showNotification by remember { mutableStateOf(true) }

    var showTextColorPicker by remember { mutableStateOf(false) }
    var showBgColorPicker by remember { mutableStateOf(false) }

    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.updateInfo) {
        if (!isLoaded && uiState.updateInfo.version > 0) {
            val info = uiState.updateInfo
            textFieldValue = TextFieldValue(RichTextUtil.fromHtml(info.message))
            updateUrl = info.updateUrl
            showNotification = info.showNotification
            fontSize = info.fontSize
            isGlobalBold = info.isBold
            isGlobalItalic = info.isItalic
            globalTextColor = Color(info.textColor)
            globalHighlightColor = Color(info.highlightColor)
            isLoaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Control Panel", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E293B),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "Rich Editor",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    "Format message for users. Use the list tool for bullet points.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        EditorToolbar(
                            textFieldValue = textFieldValue,
                            onValueChange = { textFieldValue = it },
                            onToggleTextColor = { 
                                showTextColorPicker = !showTextColorPicker
                                showBgColorPicker = false
                            },
                            onToggleBgColor = { 
                                showBgColorPicker = !showBgColorPicker
                                showTextColorPicker = false
                            }
                        )

                        if (showTextColorPicker) {
                            ColorSelectionRow(
                                title = "Select Text Color",
                                onColorSelected = { 
                                    textFieldValue = updateSelectionStyle(textFieldValue, SpanStyle(color = it))
                                    showTextColorPicker = false
                                }
                            )
                        }

                        if (showBgColorPicker) {
                            ColorSelectionRow(
                                title = "Select Highlight Color",
                                onColorSelected = { 
                                    textFieldValue = updateSelectionStyle(textFieldValue, SpanStyle(background = it))
                                    showBgColorPicker = false
                                },
                                includeTransparent = true
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 180.dp, max = 400.dp),
                            color = globalHighlightColor,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            BasicTextField(
                                value = textFieldValue,
                                onValueChange = { textFieldValue = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = globalTextColor,
                                    fontSize = fontSize.sp,
                                    fontWeight = if (isGlobalBold) FontWeight.Bold else FontWeight.Normal,
                                    fontStyle = if (isGlobalItalic) FontStyle.Italic else FontStyle.Normal
                                )
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = updateUrl,
                    onValueChange = { updateUrl = it },
                    label = { Text("Action URL (e.g. Play Store Link)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Rounded.Link, null, tint = Color(0xFF4F46E5)) }
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Global Message Styling", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Size", fontSize = 12.sp, modifier = Modifier.width(40.dp))
                            Slider(
                                value = fontSize.toFloat(),
                                onValueChange = { fontSize = it.toInt() },
                                valueRange = 12f..32f,
                                modifier = Modifier.weight(1f)
                            )
                            Text("${fontSize}sp", modifier = Modifier.width(45.dp), textAlign = TextAlign.End)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FilterChip(
                                selected = isGlobalBold,
                                onClick = { isGlobalBold = !isGlobalBold },
                                label = { Text("Global Bold") },
                                leadingIcon = { if (isGlobalBold) Icon(Icons.Rounded.Check, null, Modifier.size(18.dp)) }
                            )
                            FilterChip(
                                selected = isGlobalItalic,
                                onClick = { isGlobalItalic = !isGlobalItalic },
                                label = { Text("Global Italic") },
                                leadingIcon = { if (isGlobalItalic) Icon(Icons.Rounded.Check, null, Modifier.size(18.dp)) }
                            )
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.updateAppInfo(
                            AppUpdateInfo(
                                message = RichTextUtil.toHtml(textFieldValue.annotatedString),
                                updateUrl = updateUrl,
                                version = uiState.updateInfo.version + 1,
                                showNotification = showNotification,
                                isBold = isGlobalBold,
                                isItalic = isGlobalItalic,
                                fontSize = fontSize,
                                textColor = globalTextColor.toArgb().toLong(),
                                highlightColor = globalHighlightColor.toArgb().toLong()
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                    enabled = !uiState.isUpdating
                ) {
                    if (uiState.isUpdating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Rounded.CloudUpload, null)
                        Spacer(Modifier.width(12.dp))
                        Text("PUBLISH UPDATE", fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@Composable
fun EditorToolbar(
    textFieldValue: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onToggleTextColor: () -> Unit,
    onToggleBgColor: () -> Unit
) {
    val selection = textFieldValue.selection
    val isBold = remember(textFieldValue) { 
        !selection.collapsed && textFieldValue.annotatedString.spanStyles.any { it.start <= selection.min && it.end >= selection.max && it.item.fontWeight == FontWeight.Bold }
    }
    val isItalic = remember(textFieldValue) { 
        !selection.collapsed && textFieldValue.annotatedString.spanStyles.any { it.start <= selection.min && it.end >= selection.max && it.item.fontStyle == FontStyle.Italic }
    }
    val isUnderline = remember(textFieldValue) { 
        !selection.collapsed && textFieldValue.annotatedString.spanStyles.any { it.start <= selection.min && it.end >= selection.max && it.item.textDecoration == TextDecoration.Underline }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        ControlIcon(Icons.Rounded.FormatBold, active = isBold) {
            onValueChange(toggleSelectionStyle(textFieldValue, SpanStyle(fontWeight = FontWeight.Bold)))
        }
        ControlIcon(Icons.Rounded.FormatItalic, active = isItalic) {
            onValueChange(toggleSelectionStyle(textFieldValue, SpanStyle(fontStyle = FontStyle.Italic)))
        }
        ControlIcon(Icons.Rounded.FormatUnderlined, active = isUnderline) {
            onValueChange(toggleSelectionStyle(textFieldValue, SpanStyle(textDecoration = TextDecoration.Underline)))
        }
        
        Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFFE2E8F0)).align(Alignment.CenterVertically))

        ControlIcon(Icons.Rounded.FormatListBulleted) {
            onValueChange(toggleBulletPoint(textFieldValue))
        }
        
        Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color(0xFFE2E8F0)).align(Alignment.CenterVertically))

        ControlIcon(Icons.Rounded.FormatColorText, onClick = onToggleTextColor)
        ControlIcon(Icons.Rounded.FormatColorFill, onClick = onToggleBgColor)
        
        Spacer(Modifier.weight(1f))
        
        ControlIcon(Icons.Rounded.FormatClear) {
            if (!selection.collapsed) {
                onValueChange(textFieldValue.copy(annotatedString = removeStylesInRange(textFieldValue.annotatedString, selection)))
            }
        }
    }
}

private fun toggleBulletPoint(value: TextFieldValue): TextFieldValue {
    val text = value.text
    val selection = value.selection
    
    val start = text.lastIndexOf('\n', (selection.min - 1).coerceAtLeast(-1)).let { if (it == -1) 0 else it + 1 }
    val end = text.indexOf('\n', selection.max).let { if (it == -1) text.length else it }
    
    val lines = text.substring(start, end).split('\n')
    val allBullets = lines.all { it.startsWith("• ") }
    
    val newLines = if (allBullets) {
        lines.map { it.removePrefix("• ") }
    } else {
        lines.map { if (it.startsWith("• ")) it else "• $it" }
    }
    
    val newTextPart = newLines.joinToString("\n")
    val newText = text.substring(0, start) + newTextPart + text.substring(end)
    
    // Attempt to shift spans
    val builder = AnnotatedString.Builder(newText)
    val diff = newText.length - text.length
    
    value.annotatedString.spanStyles.forEach { range ->
        if (range.end <= start) {
            builder.addStyle(range.item, range.start, range.end)
        } else if (range.start >= end) {
            builder.addStyle(range.item, range.start + diff, range.end + diff)
        }
    }
    
    return TextFieldValue(builder.toAnnotatedString(), TextRange(newText.length))
}

@Composable
fun ColorSelectionRow(title: String, onColorSelected: (Color) -> Unit, includeTransparent: Boolean = false) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val colors = listOf(Color.Black, Color.Red, Color(0xFF10B981), Color(0xFF3B82F6), Color.Yellow, Color.White)
            if (includeTransparent) {
                Box(Modifier.size(32.dp).clip(CircleShape).border(1.dp, Color.LightGray, CircleShape).clickable { onColorSelected(Color.Transparent) }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Block, null, modifier = Modifier.size(16.dp))
                }
            }
            colors.forEach { color ->
                Box(Modifier.size(32.dp).clip(CircleShape).background(color).border(1.dp, Color.LightGray, CircleShape).clickable { onColorSelected(color) })
            }
        }
    }
}

private fun toggleSelectionStyle(value: TextFieldValue, style: SpanStyle): TextFieldValue {
    val selection = value.selection
    if (selection.collapsed) return value

    val currentAnnotatedString = value.annotatedString
    val isPresent = currentAnnotatedString.spanStyles.any { it.start <= selection.min && it.end >= selection.max && isSameProperty(it.item, style) }

    val newAnnotatedString = if (isPresent) {
        removePropertyInRange(currentAnnotatedString, selection, style)
    } else {
        AnnotatedString.Builder(currentAnnotatedString).apply {
            addStyle(style, selection.min, selection.max)
        }.toAnnotatedString()
    }

    return value.copy(annotatedString = newAnnotatedString)
}

private fun updateSelectionStyle(value: TextFieldValue, style: SpanStyle): TextFieldValue {
    val selection = value.selection
    if (selection.collapsed) return value

    // Remove existing property of same type in range first (e.g. replace color)
    val cleanedString = removePropertyInRange(value.annotatedString, selection, style)
    
    val newAnnotatedString = AnnotatedString.Builder(cleanedString).apply {
        addStyle(style, selection.min, selection.max)
    }.toAnnotatedString()

    return value.copy(annotatedString = newAnnotatedString)
}

private fun isSameProperty(s1: SpanStyle, s2: SpanStyle): Boolean {
    if (s2.fontWeight != null && s1.fontWeight == s2.fontWeight) return true
    if (s2.fontStyle != null && s1.fontStyle == s2.fontStyle) return true
    if (s2.textDecoration != null && s1.textDecoration == s2.textDecoration) return true
    if (s2.color != Color.Unspecified && s1.color != Color.Unspecified) return true
    if (s2.background != Color.Transparent && s2.background != Color.Unspecified && s1.background != Color.Transparent && s1.background != Color.Unspecified) return true
    return false
}

private fun removePropertyInRange(annotatedString: AnnotatedString, range: TextRange, style: SpanStyle): AnnotatedString {
    val builder = AnnotatedString.Builder(annotatedString.text)
    annotatedString.spanStyles.forEach { spanRange ->
        val item = spanRange.item
        if (isSameProperty(item, style)) {
            // Split or skip
            if (spanRange.end <= range.min || spanRange.start >= range.max) {
                builder.addStyle(item, spanRange.start, spanRange.end)
            } else {
                if (spanRange.start < range.min) builder.addStyle(item, spanRange.start, range.min)
                if (spanRange.end > range.max) builder.addStyle(item, range.max, spanRange.end)
            }
        } else {
            builder.addStyle(item, spanRange.start, spanRange.end)
        }
    }
    return builder.toAnnotatedString()
}

private fun removeStylesInRange(annotatedString: AnnotatedString, range: TextRange): AnnotatedString {
    val builder = AnnotatedString.Builder(annotatedString.text)
    annotatedString.spanStyles.forEach { spanStyleRange ->
        if (spanStyleRange.end <= range.min || spanStyleRange.start >= range.max) {
            builder.addStyle(spanStyleRange.item, spanStyleRange.start, spanStyleRange.end)
        } else {
            if (spanStyleRange.start < range.min) builder.addStyle(spanStyleRange.item, spanStyleRange.start, range.min)
            if (spanStyleRange.end > range.max) builder.addStyle(spanStyleRange.item, range.max, spanStyleRange.end)
        }
    }
    return builder.toAnnotatedString()
}

@Composable
fun ControlIcon(icon: ImageVector, active: Boolean = false, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(40.dp).clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = if (active) Color(0xFF4F46E5).copy(alpha = 0.1f) else Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, if (active) Color(0xFF4F46E5) else Color(0xFFE2E8F0))
    ) {
        Icon(icon, null, modifier = Modifier.padding(10.dp), tint = if (active) Color(0xFF4F46E5) else Color(0xFF64748B))
    }
}
