package com.zubtech.rohingyashikho.presentation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.zubtech.rohingyashikho.R
import com.zubtech.rohingyashikho.data.local.UserPreferences
import com.zubtech.rohingyashikho.presentation.admin.AdminScreen
import com.zubtech.rohingyashikho.presentation.alphabet.*
import com.zubtech.rohingyashikho.presentation.drawing.DrawingScreen
import com.zubtech.rohingyashikho.presentation.home.HomeScreen
import com.zubtech.rohingyashikho.presentation.lesson.LessonScreen
import com.zubtech.rohingyashikho.presentation.library.LibraryScreen
import com.zubtech.rohingyashikho.presentation.navigation.Screen
import com.zubtech.rohingyashikho.presentation.level.*
import com.zubtech.rohingyashikho.presentation.onboarding.AgeInputScreen
import com.zubtech.rohingyashikho.presentation.onboarding.NameInputScreen
import com.zubtech.rohingyashikho.presentation.onboarding.OnboardingScreen
import com.zubtech.rohingyashikho.presentation.onboarding.OnboardingViewModel
import com.zubtech.rohingyashikho.presentation.progress.ProgressScreen
import com.zubtech.rohingyashikho.presentation.quiz.QuizScreen
import com.zubtech.rohingyashikho.presentation.review.ReviewScreen
import com.zubtech.rohingyashikho.presentation.settings.LanguageStrings
import com.zubtech.rohingyashikho.presentation.settings.SettingsScreen
import com.zubtech.rohingyashikho.presentation.splash.SplashScreen
import com.zubtech.rohingyashikho.presentation.ui.theme.RohingyaShikhoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var appUpdateManager: AppUpdateManager
    private val updateType = AppUpdateType.FLEXIBLE // Can be IMMEDIATE for forced updates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        appUpdateManager = AppUpdateManagerFactory.create(this)
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.registerListener(installStateUpdatedListener)
        }
        checkForAppUpdate()

        enableEdgeToEdge()
        setContent {
            val themeMode by userPreferences.themeMode.collectAsState(initial = "system")
            val darkTheme = when (themeMode) {
                "light" -> false
                "dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            val appLanguage by userPreferences.appLanguage.collectAsState(initial = "en")

            RohingyaShikhoTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RohingyaAppNavigation(appLanguage = appLanguage)
                }
            }
        }
    }

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            Toast.makeText(
                applicationContext,
                "Download successful. Restarting app in 5 seconds.",
                Toast.LENGTH_LONG
            ).show()
            lifecycleScope.launch {
                delay(5000)
                appUpdateManager.completeUpdate()
            }
        }
    }

    private fun checkForAppUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            val isUpdateAvailable = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateAllowed = when (updateType) {
                AppUpdateType.FLEXIBLE -> info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                AppUpdateType.IMMEDIATE -> info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                else -> false
            }

            if (isUpdateAvailable && isUpdateAllowed) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    updateResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build()
                )
            }
        }
    }

    private val updateResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) {
            // If the update is cancelled or fails, you can handle it here
            Toast.makeText(this, "Update failed or cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (updateType == AppUpdateType.IMMEDIATE) {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        updateResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (updateType == AppUpdateType.FLEXIBLE) {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        }
    }
}

@Composable
fun RohingyaAppNavigation(appLanguage: String) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var backPressedOnce by remember { mutableStateOf(false) }

    val mainDestinations = listOf(
        Screen.Home.route,
        Screen.Library.route,
        Screen.Progress.route
    )

    if (mainDestinations.contains(currentDestination?.route)) {
        BackHandler {
            if (backPressedOnce) {
                (context as? Activity)?.finish()
            } else {
                backPressedOnce = true
                Toast.makeText(
                    context,
                    LanguageStrings.getText("press_back_again", appLanguage),
                    Toast.LENGTH_SHORT
                ).show()
                scope.launch {
                    delay(2000)
                    backPressedOnce = false
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Splash.route,
                modifier = Modifier.fillMaxSize()
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
                        },
                        onAdminPanelClick = {
                            navController.navigate(Screen.Admin.route)
                        }
                    )
                }
                composable(Screen.Admin.route) {
                    AdminScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Home.route) {
                    HomeScreen(
                        onLessonSelected = { lessonId ->
                            navController.navigate(Screen.LevelDetail.createRoute(lessonId))
                        },
                        onAlphabetClick = {
                            navController.navigate(Screen.Library.route)
                        },
                        onStartQuiz = { unitId ->
                            navController.navigate(Screen.Quiz.createRoute(unitId))
                        },
                        onStartReview = {
                            navController.navigate(Screen.Review.route)
                        }
                    )
                }
                composable(
                    route = Screen.LevelDetail.route,
                    arguments = listOf(navArgument("levelId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val levelId = backStackEntry.arguments?.getString("levelId") ?: "1"
                    LevelDetailScreen(
                        levelId = levelId,
                        onNavigateBack = { navController.popBackStack() },
                        onLessonSelected = { lessonId ->
                            when {
                                lessonId == "1" && levelId == "1" -> {
                                    navController.navigate(Screen.HanifiIntro.route)
                                }
                                lessonId == "2" && levelId == "1" -> {
                                    navController.navigate(Screen.Consonants.route)
                                }
                                lessonId == "3" && levelId == "1" -> {
                                    navController.navigate(Screen.Vowels.route)
                                }
                                lessonId == "4" && levelId == "1" -> {
                                    navController.navigate(Screen.AlphabetCombination.route)
                                }
                                lessonId == "5" && levelId == "1" -> {
                                    navController.navigate(Screen.Numbers.route)
                                }
                                lessonId == "1" && levelId == "2" -> {
                                    navController.navigate(Screen.WritingPractice.route)
                                }
                                lessonId == "2" && levelId == "2" -> {
                                    navController.navigate(Screen.Grammar.createRoute(levelId))
                                }
                                lessonId == "3" && levelId == "2" -> {
                                    navController.navigate(Screen.WordBuilding.route)
                                }
                                lessonId == "1" && levelId == "3" -> {
                                    navController.navigate(Screen.Grammar.createRoute(levelId))
                                }
                                lessonId == "2" && levelId == "3" -> {
                                    navController.navigate(Screen.AdvancedVocabulary.route)
                                }
                                lessonId == "1" && levelId == "4" -> {
                                    navController.navigate(Screen.AdvancedIntro.route)
                                }
                                else -> {
                                    navController.navigate(Screen.Lesson.createRoute(lessonId))
                                }
                            }
                        },
                        onConsonantsClick = {
                            navController.navigate(if (levelId == "1") Screen.Consonants.route else Screen.WritingPractice.route)
                        },
                        onVowelsClick = {
                            navController.navigate(if (levelId == "1") Screen.Vowels.route else Screen.AlphabetReference.createRoute("vowel"))
                        },
                        onCombinationClick = {
                            navController.navigate(Screen.AlphabetCombination.route)
                        },
                        onQuizClick = { quizId ->
                            navController.navigate(Screen.Quiz.createRoute(quizId))
                        },
                        onHanifiIntroClick = {
                            navController.navigate(Screen.HanifiIntro.route)
                        },
                        onNumbersClick = {
                            navController.navigate(Screen.Numbers.route)
                        },
                        onGrammarClick = {
                            navController.navigate(Screen.Grammar.createRoute(levelId))
                        },
                        onWordBuildingClick = {
                            navController.navigate(Screen.WordBuilding.route)
                        },
                        onVocabularyClick = {
                            navController.navigate(Screen.AdvancedVocabulary.route)
                        },
                        onConversationClick = {
                            navController.navigate(Screen.Conversation.route)
                        }
                    )
                }
                composable(Screen.HanifiIntro.route) {
                    HanifiIntroScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onStartLearning = {
                            navController.navigate(Screen.Consonants.route)
                        }
                    )
                }
                composable(Screen.AdvancedIntro.route) {
                    AdvancedIntroScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onStartAdvancedLearning = {
                            navController.navigate(Screen.Lesson.createRoute("1"))
                        }
                    )
                }
                composable(Screen.Consonants.route) {
                    ConsonantsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNextClick = {
                            navController.navigate(Screen.Vowels.route)
                        }
                    )
                }
                composable(Screen.Vowels.route) {
                    VowelsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNextClick = {
                            navController.navigate(Screen.Numbers.route)
                        }
                    )
                }
                composable(Screen.Numbers.route) {
                    NumbersScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.WritingPractice.route) {
                    WritingPracticeScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Screen.Grammar.route,
                    arguments = listOf(navArgument("levelId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val levelId = backStackEntry.arguments?.getString("levelId") ?: "2"
                    GrammarScreen(
                        levelId = levelId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.WordBuilding.route) {
                    WordBuildingScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.AdvancedVocabulary.route) {
                    AdvancedVocabularyScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Conversation.route) {
                    ConversationScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Screen.DrawingPractice.route,
                    arguments = listOf(navArgument("itemId") { type = NavType.StringType })
                ) {
                    DrawingScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Screen.Lesson.route,
                    arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
                ) {
                    LessonScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Screen.Quiz.route,
                    arguments = listOf(navArgument("unitId") { type = NavType.StringType })
                ) {
                    QuizScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Library.route) {
                    LibraryScreen(
                        onNavigateToConsonants = {
                            navController.navigate(Screen.Consonants.route)
                        },
                        onNavigateToVowels = {
                            navController.navigate(Screen.Vowels.route)
                        },
                        onNavigateToNumbers = {
                            navController.navigate(Screen.Numbers.route)
                        },
                        onNavigateToPdf = {
                            val pdfLink = "https://drive.google.com/file/d/1AcNWbom1UwJ46UQOzUw5OOqAXkWMO90s/view?usp=drivesdk"
                            val encodedLink = URLEncoder.encode(pdfLink, "UTF-8")
                            navController.navigate(Screen.PdfViewer.createRoute(encodedLink))
                        }
                    )
                }
                composable(
                    route = Screen.AlphabetReference.route,
                    arguments = listOf(navArgument("type") { type = NavType.StringType })
                ) { backStackEntry ->
                    val type = backStackEntry.arguments?.getString("type") ?: "consonant"
                    AlphabetReferenceScreen(
                        initialType = type,
                        onNavigateBack = { navController.popBackStack() },
                        onOpenPdf = {
                            navController.navigate(Screen.Library.route)
                        }
                    )
                }
                composable(Screen.AlphabetCombination.route) {
                    AlphabetCombinationScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(
                    route = Screen.PdfViewer.route,
                    arguments = listOf(navArgument("pdfUrl") { type = NavType.StringType })
                ) { backStackEntry ->
                    val pdfUrl = backStackEntry.arguments?.getString("pdfUrl") ?: ""
                    PdfViewerScreen(
                        pdfSource = pdfUrl,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Progress.route) {
                    ProgressScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate(Screen.Splash.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onSettingsClick = {
                            navController.navigate(Screen.Settings.route)
                        },
                        onStartReview = {
                            navController.navigate(Screen.Review.route)
                        }
                    )
                }
                composable(Screen.Review.route) {
                    ReviewScreen(
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToAdmin = { navController.navigate(Screen.Admin.route) }
                    )
                }
            }

            if (mainDestinations.contains(currentDestination?.route)) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        BottomNavBar(
                            currentRoute = currentDestination?.route,
                            appLanguage = appLanguage,
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
            }
        }
    }
}

private data class NavItemData(
    val route: String,
    val label: String,
    val painter: Painter,
    val color: Color
)

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    appLanguage: String,
    onNavigate: (String) -> Unit
) {
    val homeLabel = if (appLanguage == "rhg") "𓅒𓐮𓄿𓐮" else "Home"
    val libraryLabel = if (appLanguage == "rhg") "𓎡𓐮𓏏𓐮𓄿𓐮𓅆𓐮" else "Library"
    val profileLabel = if (appLanguage == "rhg") "𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮" else "Profile"

    val items = listOf(
        NavItemData(Screen.Home.route, homeLabel, painterResource(id = R.drawable.nav_home), Color(0xFF6366F1)),
        NavItemData(Screen.Library.route, libraryLabel, painterResource(id = R.drawable.nav_library), Color(0xFF00ACC1)),
        NavItemData(Screen.Progress.route, profileLabel, painterResource(id = R.drawable.nav_profile), Color(0xFFEC4899))
    )

    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 16.dp,
        border = BorderStroke(
            width = 2.dp,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF6366F1), Color(0xFFA855F7))
            )
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                BottomNavItem(
                    painter = item.painter,
                    label = item.label,
                    isSelected = isSelected,
                    selectedColor = item.color,
                    onClick = { onNavigate(item.route) }
                )
            }
        }
    }
}

@Composable
fun RowScope.BottomNavItem(
    painter: Painter,
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        if (isSelected) selectedColor else Color(0xFF94A3B8),
        label = "color"
    )
    val animatedScale by animateFloatAsState(
        if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = label,
                modifier = Modifier
                    .size(42.dp)
                    .scale(animatedScale),
                tint = if (label == "Home" || label == "Library" || label == "Profile" || label == "𓅒𓐮𓄿𓐮" || label == "𓎡𓐮𓏏𓐮𓄿𓐮𓅆𓐮" || label == "𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮") Color.Unspecified else animatedColor
            )
            if (isSelected) {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(animatedColor)
                )
            }
        }
    }
}
