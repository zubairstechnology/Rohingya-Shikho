package com.zubtech.rohingyashikho.presentation.home

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
import javax.inject.Inject

data class HomeUiState(
    val course: Course? = null,
    val streak: Int = 0,
    val totalPoints: Int = 0,
    val userName: String = "Jonathan",
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val progressDao: ProgressDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        contentRepository.getCourse(),
        progressDao.getUserStats(),
        userPreferences.userName
    ) { course, stats, userName ->
        HomeUiState(
            course = course,
            streak = stats?.currentStreak ?: 0,
            totalPoints = stats?.totalPoints ?: 0,
            userName = userName ?: "Jonathan",
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )
}
