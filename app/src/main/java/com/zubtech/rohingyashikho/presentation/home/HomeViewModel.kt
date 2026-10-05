package com.zubtech.rohingyashikho.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.local.UserPreferences
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity
import com.zubtech.rohingyashikho.domain.model.AppUpdateInfo
import com.zubtech.rohingyashikho.domain.model.Course
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
    val isLoading: Boolean = true,
    val appUpdateInfo: AppUpdateInfo = AppUpdateInfo(),
    val lastInteractedNotificationVersion: Int = 0,
    val lastSeenNotificationVersion: Int = 0,
    val isUpdating: Boolean = false,
    val updateProgress: Float = 0f
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val progressDao: ProgressDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isUpdating = MutableStateFlow(false)
    private val _updateProgress = MutableStateFlow(0f)

    init {
        updateStreak()
    }

    val uiState: StateFlow<HomeUiState> = combine(
        contentRepository.getCourse(),
        progressDao.getUserStats(),
        userPreferences.userName,
        userPreferences.appLanguage,
        contentRepository.getAppUpdateInfo().onStart { emit(AppUpdateInfo()) },
        userPreferences.lastInteractedNotificationVersion,
        userPreferences.lastSeenNotificationVersion,
        _isUpdating,
        _updateProgress
    ) { flows ->
        val course = flows[0] as Course?
        val stats = flows[1] as UserStatsEntity?
        val userName = flows[2] as String?
        val lang = flows[3] as String
        val updateInfo = flows[4] as AppUpdateInfo
        val lastInteracted = flows[5] as Int
        val lastSeen = flows[6] as Int
        val isUpdating = flows[7] as Boolean
        val updateProgress = flows[8] as Float

        HomeUiState(
            course = course,
            streak = stats?.currentStreak ?: 0,
            totalPoints = stats?.totalPoints ?: 0,
            userName = userName ?: "Jubayer",
            appLanguage = lang,
            isLoading = course == null,
            appUpdateInfo = updateInfo,
            lastInteractedNotificationVersion = lastInteracted,
            lastSeenNotificationVersion = lastSeen,
            isUpdating = isUpdating,
            updateProgress = updateProgress
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun startInAppUpdate() {
        if (_isUpdating.value) return
        
        viewModelScope.launch {
            _isUpdating.value = true
            _updateProgress.value = 0f
            
            // Simulation of update download
            for (i in 1..100) {
                delay(50)
                _updateProgress.value = i / 100f
            }
            
            delay(1000)
            _isUpdating.value = false
            _updateProgress.value = 0f
            
            // Mark as interacted after successful update simulation
            markNotificationAsInteracted(uiState.value.appUpdateInfo.version)
        }
    }

    fun markNotificationAsInteracted(version: Int) {
        viewModelScope.launch {
            userPreferences.setLastInteractedNotificationVersion(version)
        }
    }

    fun markNotificationAsSeen(version: Int) {
        viewModelScope.launch {
            userPreferences.setLastSeenNotificationVersion(version)
        }
    }

    private fun updateStreak() {
        viewModelScope.launch {
            val stats = progressDao.getUserStats().first() ?: return@launch
            val now = System.currentTimeMillis()
            val lastDate = stats.lastLessonDate

            if (lastDate == 0L) {
                progressDao.updateUserStats(stats.copy(currentStreak = 1, lastLessonDate = now))
                return@launch
            }

            val lastCalendar = Calendar.getInstance().apply { timeInMillis = lastDate }
            val currentCalendar = Calendar.getInstance().apply { timeInMillis = now }

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
                    progressDao.updateUserStats(stats.copy(
                        currentStreak = stats.currentStreak + 1,
                        lastLessonDate = now
                    ))
                }
                diffMillis > oneDayMillis -> {
                    progressDao.updateUserStats(stats.copy(
                        currentStreak = 1,
                        lastLessonDate = now
                    ))
                }
            }
        }
    }
}
