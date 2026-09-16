package com.zubtech.rohingyashikho.presentation.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.presentation.ui.theme.AppPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameInputScreen(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit
) {
    val name by viewModel.userName.collectAsState()
    val scrollState = rememberScrollState()

    // Floating animation
    val infiniteTransition = rememberInfiniteTransition(label = "hero_anim")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Watermark Decorative Elements ---
        LearningWatermarks()

        // --- Responsive Decorative Blobs ---
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-100).dp, y = (-50).dp)
                .size(320.dp)
                .clip(CircleShape)
                .background(AppPrimary.copy(alpha = 0.04f))
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 120.dp)
                .size(380.dp)
                .clip(CircleShape)
                .background(AppPrimary.copy(alpha = 0.06f))
                .blur(100.dp)
        )

        // --- Main Content ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Main Hero Image
            Image(
                painter = painterResource(id = R.drawable.name),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 350.dp, max = 550.dp)
                    .offset(y = offsetY.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Pill-shaped Input Field
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.updateName(it) },
                placeholder = { Text("Enter your name", color = Color.Gray.copy(alpha = 0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Person,
                        contentDescription = null,
                        tint = AppPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = AppPrimary,
                    unfocusedBorderColor = AppPrimary.copy(alpha = 0.2f),
                    containerColor = Color.White.copy(alpha = 0.9f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Next Button
            Button(
                onClick = onNext,
                enabled = name.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppPrimary,
                    disabledContainerColor = AppPrimary.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(34.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Next", 
                        fontSize = 22.sp, 
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun LearningWatermarks() {
    Box(modifier = Modifier.fillMaxSize()) {
        WatermarkIcon(Icons.Rounded.MenuBook, Modifier.align(Alignment.TopEnd).offset(x = 20.dp, y = 100.dp).rotate(15f))
        WatermarkIcon(Icons.Rounded.School, Modifier.align(Alignment.CenterStart).offset(x = (-30).dp, y = (-150).dp).rotate(-20f))
        WatermarkIcon(Icons.Rounded.Draw, Modifier.align(Alignment.BottomStart).offset(x = 10.dp, y = (-100).dp).rotate(-10f))
        WatermarkIcon(Icons.Rounded.AutoStories, Modifier.align(Alignment.CenterEnd).offset(x = 40.dp, y = 200.dp).rotate(25f))
        WatermarkIcon(Icons.Rounded.Create, Modifier.align(Alignment.TopStart).offset(x = 40.dp, y = 50.dp).rotate(-15f))
    }
}

@Composable
fun WatermarkIcon(icon: ImageVector, modifier: Modifier) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = modifier.size(100.dp).alpha(0.03f),
        tint = Color.Black
    )
}
