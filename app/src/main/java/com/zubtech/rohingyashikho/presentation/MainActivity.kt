package com.zubtech.rohingyashikho.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zubtech.rohingyashikho.presentation.alphabet.AlphabetReferenceScreen
import com.zubtech.rohingyashikho.presentation.alphabet.PdfViewerScreen
import com.zubtech.rohingyashikho.presentation.drawing.DrawingScreen
import com.zubtech.rohingyashikho.presentation.home.HomeScreen
import com.zubtech.rohingyashikho.presentation.lesson.LessonScreen
import com.zubtech.rohingyashikho.presentation.navigation.Screen
import com.zubtech.rohingyashikho.presentation.onboarding.AgeInputScreen
import com.zubtech.rohingyashikho.presentation.onboarding.NameInputScreen
import com.zubtech.rohingyashikho.presentation.onboarding.OnboardingScreen
import com.zubtech.rohingyashikho.presentation.onboarding.OnboardingViewModel
import com.zubtech.rohingyashikho.presentation.progress.ProgressScreen
import com.zubtech.rohingyashikho.presentation.quiz.QuizScreen
import com.zubtech.rohingyashikho.presentation.review.ReviewScreen
import com.zubtech.rohingyashikho.presentation.settings.SettingsScreen
import com.zubtech.rohingyashikho.presentation.splash.SplashScreen
import com.zubtech.rohingyashikho.presentation.ui.theme.AppBackground
import com.zubtech.rohingyashikho.presentation.ui.theme.AppCardDark
import com.zubtech.rohingyashikho.presentation.ui.theme.AppPrimary
import com.zubtech.rohingyashikho.presentation.ui.theme.RohingyaShikhoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RohingyaShikhoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppBackground
                ) {
                    RohingyaAppNavigation()
                }
            }
        }
    }
}

@Composable
fun RohingyaAppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    val mainDestinations = listOf(
        Screen.Home.route,
        Screen.AlphabetReference.route,
        Screen.Review.route,
        Screen.Progress.route
    )

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (currentDestination?.route in mainDestinations) {
                CustomBottomNavigation(
                    currentRoute = currentDestination?.route,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onAnimationFinished = {
                        scope.launch {
                            if (onboardingViewModel.isOnboardingCompleted()) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Screen.NameInput.route) {
                                    popUpTo(Screen.Splash.route) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }
            composable(Screen.NameInput.route) {
                NameInputScreen(
                    viewModel = onboardingViewModel,
                    onNext = {
                        navController.navigate(Screen.AgeInput.route)
                    }
                )
            }
            composable(Screen.AgeInput.route) {
                AgeInputScreen(
                    viewModel = onboardingViewModel,
                    onNext = {
                        navController.navigate(Screen.Onboarding.route)
                    }
                )
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onStartLearning = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.NameInput.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onLessonSelected = { lessonId ->
                        navController.navigate(Screen.Lesson.createRoute(lessonId))
                    },
                    onAlphabetClick = {
                        navController.navigate(Screen.AlphabetReference.route)
                    },
                    onStartQuiz = { unitId ->
                        navController.navigate(Screen.Quiz.createRoute(unitId))
                    },
                    onStartReview = {
                        navController.navigate(Screen.Review.route)
                    }
                )
            }
            composable(Screen.AlphabetReference.route) {
                AlphabetReferenceScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onItemClick = { itemId ->
                        navController.navigate(Screen.DrawingPractice.createRoute(itemId))
                    },
                    onOpenPdf = {
                        navController.navigate(Screen.PdfViewer.route)
                    }
                )
            }
            composable(
                route = Screen.Lesson.route,
                arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
            ) {
                LessonScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onPracticeWriting = { itemId ->
                         navController.navigate(Screen.DrawingPractice.createRoute(itemId))
                    }
                )
            }
            composable(
                route = Screen.Quiz.route,
                arguments = listOf(navArgument("unitId") { type = NavType.StringType })
            ) {
                QuizScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Review.route) {
                ReviewScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Progress.route) {
                ProgressScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Screen.DrawingPractice.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) {
                DrawingScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.PdfViewer.route) {
                PdfViewerScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun CustomBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            shape = RoundedCornerShape(36.dp),
            color = AppCardDark,
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    icon = Icons.Rounded.Home,
                    label = "Home",
                    isSelected = currentRoute == Screen.Home.route,
                    onClick = { onNavigate(Screen.Home.route) }
                )
                BottomNavItem(
                    icon = Icons.Rounded.MenuBook,
                    label = "Alphabet",
                    isSelected = currentRoute == Screen.AlphabetReference.route,
                    onClick = { onNavigate(Screen.AlphabetReference.route) }
                )
                BottomNavItem(
                    icon = Icons.Rounded.Bookmark,
                    label = "Review",
                    isSelected = currentRoute == Screen.Review.route,
                    onClick = { onNavigate(Screen.Review.route) }
                )
                BottomNavItem(
                    icon = Icons.Rounded.Person,
                    label = "Profile",
                    isSelected = currentRoute == Screen.Progress.route,
                    onClick = { onNavigate(Screen.Progress.route) }
                )
            }
        }
    }
}

@Composable
fun BottomNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isSelected) AppPrimary else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = if (isSelected) 20.dp else 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(26.dp)
            )
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = label,
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}
