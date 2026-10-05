package com.zubtech.rohingyashikho.presentation.settings

object LanguageStrings {
    fun getText(key: String, language: String): String {
        return when (language) {
            "rhg" -> when (key) {
                // General
                "app_name" -> "𐴌𐴟𐴇𐴝𐴥𐴚𐴒𐴙𐴝 𐴬𐴞𐴈𐴡"
                "back" -> "𐴉𐴠𐴌𐴝"
                "next" -> "𐴓𐴝𐴥𐴇𐴠"
                "cancel" -> "𐴇𐴝𐴥𐴌𐴞𐴓"
                "start" -> "𐴬𐴟𐴌𐴟"
                "press_back_again" -> "𐴀𐴝𐴌 𐴀𐴠𐴑𐴁𐴝𐴌 𐴉𐴠𐴌𐴝 𐴃𐴞𐴉𐴡"
                
                // Navigation
                "nav_home" -> "𐴂𐴝𐴌𐴝"
                "nav_library" -> "𐴑𐴠𐴃𐴝𐴁𐴈𐴝𐴥𐴓𐴝"
                "nav_profile" -> "𐴉𐴡𐴌𐴉𐴝𐴞𐴓"
                
                // Settings Screen
                "settings_title" -> "𐴀𐴠𐴬𐴠𐴬𐴝𐴬𐴝"
                "app_customization" -> "𐴀𐴠𐴞𐴂𐴢 𐴬𐴝𐴥𐴬𐴝"
                "theme_mode" -> "𐴌𐴡𐴚 𐴬𐴥𐴝𐴬𐴝"
                "language_selection" -> "𐴅𐴟𐴁𐴝𐴥𐴚 𐴬𐴥𐴝𐴬𐴝"
                "preferences" -> "𐴉𐴡𐴬𐴡𐴓"
                "audio_speed" -> "𐴀𐴝𐴥𐴅𐴝 𐴬𐴥𐴝𐴬𐴝"
                "daily_reminders" -> "𐴇𐴝𐴥𐴁𐴝𐴌 𐴬𐴥𐴝𐴬𐴝"
                "choose_theme" -> "𐴌𐴡𐴚 𐴁𐴝𐴬𐴡"
                "select_language" -> "𐴅𐴟𐴁𐴝𐴥𐴚 𐴁𐴝𐴬𐴡"
                "light_mode" -> "𐴉𐴡𐴌"
                "dark_mode" -> "𐴀𐴝𐴥𐴊𐴝𐴌"
                "system_default" -> "𐴬𐴞𐴬𐴂𐴡𐴥𐴔"
                "rohingya_hanifi" -> "𐴌𐴟𐴇𐴝𐴥𐴚𐴒𐴙𐴝 (𐴇𐴝𐴥𐴓𐴞𐴉𐴞)"
                "english" -> "English"

                // Admin
                "admin_control_panel" -> "𐴀𐴠𐴊𐴔𐴞𐴓 𐴑𐴡𐴓𐴃𐴟𐴌𐴟𐴓 𐴉𐴠𐴓𐴝"
                "admin_panel_summary" -> "𐴀𐴠𐴞𐴂𐴢 𐴀𐴝𐴥𐴌𐴟 𐴔𐴝𐴓 𐴬𐴝𐴥𐴬𐴝"
                
                // Home Screen
                "welcome_back" -> "𐴈𐴟𐴬 𐴀𐴝𐴔𐴡𐴊𐴡𐴓!"
                "continue_learning" -> "𐴬𐴞𐴈𐴡𐴓 𐴀𐴝𐴌 𐴬𐴟𐴌𐴟 𐴒𐴡𐴌𐴡"
                "rohingya_language" -> "𐴌𐴟𐴇𐴝𐴥𐴚𐴒𐴙𐴝 𐴅𐴟𐴁𐴝𐴥𐴚"
                "lessons_count" -> "𐴲𐴸 𐴬𐴞𐴈𐴡𐴓"
                "hanifi_script" -> "𐴇𐴝𐴥𐴓𐴞𐴉𐴞 𐴬𐴝𐴥𐴬𐴝"
                "days_streak" -> "𐴊𐴞𐴓"
                "overall_progress" -> "𐴬𐴞𐴈𐴡𐴓 𐴇𐴝𐴥𐴓"
                "learning_path" -> "𐴬𐴞𐴈𐴡𐴓 𐴌𐴝𐴬𐴃𐴝"
                "foundation_mastery" -> "𐴬𐴞𐴈𐴡𐴓 𐴀𐴝𐴬𐴡𐴓"
                "goal" -> "𐴔𐴡𐴑𐴬𐴡𐴊"
                "level" -> "𐴊𐴝𐴉𐴝"
                "sections" -> "𐴇𐴞𐴬𐴬𐴝"
                
                // Levels
                "beginner" -> "𐴬𐴟𐴌𐴟 𐴒𐴡𐴌𐴞𐴥𐴓𐴠"
                "elementary" -> "𐴔𐴝𐴬𐴝𐴔𐴝𐴬𐴞"
                "intermediate" -> "𐴊𐴡𐴌𐴔𐴞𐴥𐴝𐴓"
                "advanced" -> "𐴀𐴟𐴉𐴡𐴌𐴠"
                
                // Lesson Items
                "script_intro" -> "𐴬𐴝𐴥𐴬𐴝 𐴬𐴞𐴈𐴡"
                "consonants" -> "𐴇𐴝𐴥𐴌𐴟𐴉𐴢 𐴀𐴝𐴥𐴊𐴝"
                "vowels" -> "𐴇𐴝𐴥𐴌𐴟𐴉𐴢 𐴀𐴟𐴥"
                "combining" -> "𐴔𐴞𐴓𐴡𐴓"
                "numbers" -> "𐴓𐴡𐴔𐴁𐴡𐴌"
                
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

                // Admin
                "admin_control_panel" -> "Admin Control Panel"
                "admin_panel_summary" -> "Manage app updates and content"

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
