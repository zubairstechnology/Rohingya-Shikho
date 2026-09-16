package com.zubtech.rohingyashikho.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object NameInput : Screen("name_input")
    object AgeInput : Screen("age_input")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object AlphabetReference : Screen("alphabet_reference")
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
}
