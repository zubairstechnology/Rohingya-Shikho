package com.zubtech.rohingyashikho.presentation.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.local.UserPreferences
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.domain.model.Course
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressUiState(
    val totalItems: Int = 0,
    val masteredItems: Int = 0,
    val levelProgress: Map<String, Float> = emptyMap(),
    val userName: String = "Learner",
    val streak: Int = 0,
    val totalPoints: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val progressDao: ProgressDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<ProgressUiState> = combine(
        contentRepository.getCourse(),
        progressDao.getAllProgress(),
        progressDao.getUserStats(),
        userPreferences.userName
    ) { course, progressList, stats, prefUserName ->
        if (course == null) return@combine ProgressUiState(isLoading = false)

        val progressMap = progressList.associateBy { it.lessonItemId }
        val masteredCount = progressList.count { it.masteryPercentage >= 100 }
        
        val levelProgress = course.levels.associate { level ->
            val levelItems = level.units.flatMap { it.lessons }.flatMap { it.items }
            val levelItemCount = levelItems.size
            val levelMasteredCount = levelItems.count { item ->
                (progressMap[item.id]?.masteryPercentage ?: 0) >= 100
            }
            level.id to if (levelItemCount > 0) levelMasteredCount.toFloat() / levelItemCount else 0f
        }

        val totalItemCount = course.levels.flatMap { it.units }.flatMap { it.lessons }.flatMap { it.items }.size

        ProgressUiState(
            totalItems = totalItemCount,
            masteredItems = masteredCount,
            levelProgress = levelProgress,
            userName = prefUserName ?: stats?.userName ?: "Learner",
            streak = stats?.currentStreak ?: 0,
            totalPoints = stats?.totalPoints ?: 0,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressUiState()
    )

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearUserProfile()
            onComplete()
        }
    }
}
