package com.zubtech.rohingyashikho.presentation.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.presentation.ui.theme.AppPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeInputScreen(
    viewModel: OnboardingViewModel,
    onNext: () -> Unit
) {
    val age by viewModel.userAge.collectAsState()
    val scrollState = rememberScrollState()

    // Floating and Pulse animation for an attractive 3D look
    val infiniteTransition = rememberInfiniteTransition(label = "hero_animation")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // --- Responsive Decorative Blobs ---
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = (-100).dp, y = (-50).dp)
                .size(320.dp)
                .clip(CircleShape)
                .background(AppPrimary.copy(alpha = 0.05f))
                .blur(80.dp)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 120.dp)
                .size(380.dp)
                .clip(CircleShape)
                .background(AppPrimary.copy(alpha = 0.08f))
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

            // Main Hero Image with Animation
            Image(
                painter = painterResource(id = R.drawable.age),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 350.dp, max = 550.dp)
                    .offset(y = offsetY.dp)
                    .scale(scale),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Pill-shaped Input Field
            OutlinedTextField(
                value = age,
                onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.updateAge(it) },
                placeholder = { Text("Enter your age", color = Color.Gray.copy(alpha = 0.4f)) },
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
                    containerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pill-shaped Next Button
            Button(
                onClick = onNext,
                enabled = age.isNotBlank(),
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
