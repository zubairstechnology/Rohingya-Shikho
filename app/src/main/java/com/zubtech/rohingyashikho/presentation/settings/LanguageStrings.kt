package com.zubtech.rohingyashikho.presentation.settings

object LanguageStrings {
    fun getText(key: String, language: String): String {
        return when (language) {
            "rhg" -> when (key) {
                // General
                "app_name" -> "Rohingya Shikho"
                "back" -> "𓐮𓂝𓐮𓊎𓐮𓄿𓐮"
                "next" -> "𓄿𓐮𓆵𓏞𓐮"
                "cancel" -> "𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "start" -> "𓐬𓆵𓏏𓐮"
                "press_back_again" -> "𓐮𓂝𓐮𓊎𓐮𓄿𓐮 𓐮𓂝𓐮𓊎𓐮𓄿𓐮"
                
                // Navigation
                "nav_home" -> "𓅒𓐮𓄿𓐮"
                "nav_library" -> "𓎡𓐮𓏏𓐮𓄿𓐮𓅆𓐮"
                "nav_profile" -> "𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                
                // Settings Screen
                "settings_title" -> "⚙️ 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "app_customization" -> "𓄿𓐮𓊎𓐮𓂝𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "theme_mode" -> "𓂝𓐮𓊎𓐮𓄿𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "language_selection" -> "𓄿𓐮𓆵𓏞𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "preferences" -> "𓎡𓐮𓈷𓐮𓅆𓐮𓄿𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "audio_speed" -> "𓄿𓐮𓅱𓐮𓊎𓐮𓆵𓏏𓐮 𓂝𓐮𓊎𓐮𓄿𓐮"
                "daily_reminders" -> "𓄿𓐮𓆷𓐮𓅱𓐮𓂝𓐮"
                "choose_theme" -> "𓂝𓐮𓊎𓐮𓄿𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "select_language" -> "𓄿𓐮𓆵𓏞𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "light_mode" -> "𓐮𓂝𓐮𓊎𓐮𓄿𓐮"
                "dark_mode" -> "𓐮𓂝𓐮𓊎𓐮𓄿𓐮 𓐮𓂝𓐮𓊎𓐮𓄿𓐮"
                "system_default" -> "𓐮𓂝𓐮𓊎𓐮𓄿𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "rohingya_hanifi" -> "𓅌𓐮𓊎𓐮𓆵𓏏𓐮𓆷𓐮 (Rohingya)"
                "english" -> "English"
                
                // Home Screen
                "welcome_back" -> "𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮 𓎡𓐮𓏏𓐮!"
                "continue_learning" -> "𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮 𓎡𓐮𓏏𓐮"
                "rohingya_language" -> "𓅌𓐮𓊎𓐮𓆵𓏏𓐮𓆷𓐮 𓄿𓐮𓆵𓏞𓐮"
                "lessons_count" -> "28 𓄿𓐮𓆵𓏞𓐮"
                "hanifi_script" -> "𓅌𓐮𓊎𓐮𓆵𓏏𓐮𓆷𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "days_streak" -> "𓂝𓐮𓊎𓐮"
                "overall_progress" -> "𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮 𓎡𓐮𓏏𓐮"
                "learning_path" -> "𓅒𓐮𓄿𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "foundation_mastery" -> "𓅌𓐮𓊎𓐮𓆵𓏏𓐮𓆷𓐮 𓅒𓐮𓄿𓐮"
                "goal" -> "𓐬𓆵𓏏𓐮"
                "level" -> "𓅒𓐮𓄿𓐮"
                "sections" -> "𓄿𓐮𓆵𓏞𓐮"
                
                // Levels
                "beginner" -> "𓅒𓐮𓄿𓐮𓆵𓏏𓐮 (Beginner)"
                "elementary" -> "𓅒𓐮𓄿𓐮𓐮𓄿𓐮"
                "intermediate" -> "𓅒𓐮𓄿𓐮𓆵𓏏𓐮𓆷𓐮𓂝𓐮 (Intermediate)"
                "advanced" -> "𓅒𓐮𓄿𓐮𓆵𓏏𓐮𓆷𓐮𓂝𓐮𓊎𓐮 (Advanced)"
                
                // Lesson Items
                "script_intro" -> "𓐮𓂝𓐮𓊎𓐮𓄿𓐮 𓐬𓆵𓏏𓐮𓄿𓆵𓏏𓐮"
                "consonants" -> "𓄿𓐮𓆵𓏞𓐮 𓂝𓐮𓊎𓐮𓄿𓐮"
                "vowels" -> "𓄿𓐮𓅱𓐮𓊎𓐮𓆵𓏏𓐮"
                "combining" -> "𓎡𓐮𓈷𓐮𓅆𓐮𓄿𓐮"
                "numbers" -> "𓅌𓐮𓊎𓐮𓆵𓏏𓐮"
                
                else -> key
            }
            else -> when (key) {
                // General
                "app_name" -> "Rohingya Shikho"
                "back" -> "Back"
                "next" -> "Next"
                "cancel" -> "Cancel"
                "start" -> "Start"
                "press_back_again" -> "Press back again to exit"

                // Navigation
                "nav_home" -> "Home"
                "nav_library" -> "Library"
                "nav_profile" -> "Profile"

                // Settings Screen
                "settings_title" -> "Settings"
                "app_customization" -> "App Customization"
                "theme_mode" -> "Theme Mode"
                "language_selection" -> "Language"
                "preferences" -> "Preferences"
                "audio_speed" -> "Audio Playback Speed"
                "daily_reminders" -> "Daily Reminders"
                "choose_theme" -> "Choose Theme Mode"
                "select_language" -> "Select Language"
                "light_mode" -> "Light Mode"
                "dark_mode" -> "Dark Mode"
                "system_default" -> "System Default"
                "rohingya_hanifi" -> "Rohingya Hanifi"
                "english" -> "English"

                // Home Screen
                "welcome_back" -> "Welcome Back!"
                "continue_learning" -> "Pick up where you left"
                "rohingya_language" -> "Rohingya Language"
                "lessons_count" -> "28 Lessons"
                "hanifi_script" -> "Hanifi Script"
                "days_streak" -> "Days Streak"
                "overall_progress" -> "Overall Progress"
                "learning_path" -> "Learning Path"
                "foundation_mastery" -> "Foundation to mastery"
                "goal" -> "Goal"
                "level" -> "LEVEL"
                "sections" -> "Sections"

                // Levels
                "beginner" -> "Beginner"
                "elementary" -> "Elementary"
                "intermediate" -> "Intermediate"
                "advanced" -> "Advanced"

                // Lesson Items
                "script_intro" -> "Script Introduction"
                "consonants" -> "Consonants"
                "vowels" -> "Vowels"
                "combining" -> "Combining"
                "numbers" -> "Numbers"

                else -> key
            }
        }
    }
}
