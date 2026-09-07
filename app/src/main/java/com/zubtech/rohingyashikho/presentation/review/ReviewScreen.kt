package com.zubtech.rohingyashikho.presentation.review

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Review") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.isFinished || uiState.reviewItems.isEmpty()) {
            EmptyReviewScreen(onNavigateBack)
        } else {
            uiState.currentPair?.let { (item, _) ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Do you remember this?",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = item.scriptText,
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    AnimatedVisibility(visible = uiState.showAnswer) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.transliteration,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = item.englishMeaning,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            IconButton(onClick = { /* TODO: Play Audio */ }) {
                                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(32.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (!uiState.showAnswer) {
                        Button(
                            onClick = { viewModel.toggleAnswer() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Show Answer")
                        }
                    } else {
                        RatingButtons(onRate = { quality ->
                            viewModel.submitRating(quality)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun RatingButtons(onRate: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "How easy was it?",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RatingButton(label = "Hard", color = MaterialTheme.colorScheme.error, onClick = { onRate(1) }, modifier = Modifier.weight(1f))
            RatingButton(label = "Good", color = MaterialTheme.colorScheme.primary, onClick = { onRate(3) }, modifier = Modifier.weight(1f))
            RatingButton(label = "Easy", color = MaterialTheme.colorScheme.secondary, onClick = { onRate(5) }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun RatingButton(label: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(label)
    }
}

@Composable
fun EmptyReviewScreen(onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("No Reviews Due!", style = MaterialTheme.typography.headlineMedium)
        Text("Come back tomorrow for more.", modifier = Modifier.padding(16.dp))
        Button(onClick = onFinish) {
            Text("Back to Home")
        }
    }
}
