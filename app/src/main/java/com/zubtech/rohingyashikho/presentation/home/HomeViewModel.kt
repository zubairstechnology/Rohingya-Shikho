package com.zubtech.rohingyashikho.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.local.UserPreferences
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.domain.model.Course
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val course: Course? = null,
    val streak: Int = 0,
    val totalPoints: Int = 0,
    val userName: String = "Jubayer",
    val appLanguage: String = "en",
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val progressDao: ProgressDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    init {
        updateStreak()
    }

    val uiState: StateFlow<HomeUiState> = combine(
        contentRepository.getCourse(),
        progressDao.getUserStats(),
        userPreferences.userName,
        userPreferences.appLanguage
    ) { course, stats, userName, lang ->
        HomeUiState(
            course = course,
            streak = stats?.currentStreak ?: 0,
            totalPoints = stats?.totalPoints ?: 0,
            userName = userName ?: "Jubayer",
            appLanguage = lang,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    private fun updateStreak() {
        viewModelScope.launch {
            val stats = progressDao.getUserStats().first() ?: return@launch
            val now = System.currentTimeMillis()
            val lastDate = stats.lastLessonDate

            if (lastDate == 0L) {
                // First time starting a streak
                progressDao.updateUserStats(stats.copy(currentStreak = 1, lastLessonDate = now))
                return@launch
            }

            val lastCalendar = Calendar.getInstance().apply { timeInMillis = lastDate }
            val currentCalendar = Calendar.getInstance().apply { timeInMillis = now }

            // Start of day normalization
            lastCalendar.set(Calendar.HOUR_OF_DAY, 0)
            lastCalendar.set(Calendar.MINUTE, 0)
            lastCalendar.set(Calendar.SECOND, 0)
            lastCalendar.set(Calendar.MILLISECOND, 0)

            currentCalendar.set(Calendar.HOUR_OF_DAY, 0)
            currentCalendar.set(Calendar.MINUTE, 0)
            currentCalendar.set(Calendar.SECOND, 0)
            currentCalendar.set(Calendar.MILLISECOND, 0)

            val diffMillis = currentCalendar.timeInMillis - lastCalendar.timeInMillis
            val oneDayMillis = 24 * 60 * 60 * 1000L

            when {
                diffMillis == oneDayMillis -> {
                    // It's the next day, streak continues
                    progressDao.updateUserStats(stats.copy(
                        currentStreak = stats.currentStreak + 1,
                        lastLessonDate = now
                    ))
                }
                diffMillis > oneDayMillis -> {
                    // Streak broken (more than 1 day passed)
                    progressDao.updateUserStats(stats.copy(
                        currentStreak = 1,
                        lastLessonDate = now
                    ))
                }
                // If diffMillis == 0, it's the same day, no update needed to streak count
            }
        }
    }
}
