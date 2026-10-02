package com.zubtech.rohingyashikho.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object NameInput : Screen("name_input")
    object AgeInput : Screen("age_input")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Library : Screen("library")
    object AlphabetReference : Screen("alphabet_reference?type={type}") {
        fun createRoute(type: String) = "alphabet_reference?type=$type"
    }
    object LevelDetail : Screen("level_detail/{levelId}") {
        fun createRoute(levelId: String) = "level_detail/$levelId"
    }
    object Lesson : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }
    object Quiz : Screen("quiz/{unitId}") {
        fun createRoute(unitId: String) = "quiz/$unitId"
    }
    object Review : Screen("review")
    object Progress : Screen("progress")
    object Settings : Screen("settings")
    object DrawingPractice : Screen("drawing/{itemId}") {
        fun createRoute(itemId: String) = "drawing/$itemId"
    }
    object PdfViewer : Screen("pdf_viewer?pdfUrl={pdfUrl}") {
        fun createRoute(pdfUrl: String) = "pdf_viewer?pdfUrl=$pdfUrl"
    }
    object HanifiIntro : Screen("hanifi_intro")
    object AdvancedIntro : Screen("advanced_intro")
    object Consonants : Screen("consonants")
    object Vowels : Screen("vowels")
    object AlphabetCombination : Screen("alphabet_combination")
    object Numbers : Screen("numbers")
    object WritingPractice : Screen("writing_practice")
    object Grammar : Screen("grammar/{levelId}") {
        fun createRoute(levelId: String) = "grammar/$levelId"
    }
    object WordBuilding : Screen("word_building")
    object AdvancedVocabulary : Screen("advanced_vocabulary")
    object Conversation : Screen("conversation")
}
