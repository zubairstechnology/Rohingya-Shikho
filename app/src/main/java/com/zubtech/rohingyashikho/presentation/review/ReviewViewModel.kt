package com.zubtech.rohingyashikho.presentation.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.local.entity.ReviewItemEntity
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import com.zubtech.rohingyashikho.domain.usecase.UpdateReviewItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReviewUiState(
    val reviewItems: List<Pair<LessonItem, ReviewItemEntity>> = emptyList(),
    val currentItemIndex: Int = 0,
    val showAnswer: Boolean = false,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true
) {
    val currentPair: Pair<LessonItem, ReviewItemEntity>? get() = reviewItems.getOrNull(currentItemIndex)
}

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val progressDao: ProgressDao,
    private val updateReviewItemUseCase: UpdateReviewItemUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    init {
        loadReviewItems()
    }

    private fun loadReviewItems() {
        viewModelScope.launch {
            combine(
                progressDao.getDueReviewItems(System.currentTimeMillis()),
                contentRepository.getCourse()
            ) { dueEntities, course ->
                if (course == null) return@combine emptyList<Pair<LessonItem, ReviewItemEntity>>()
                
                val allItems = course.levels.flatMap { it.units }.flatMap { it.lessons }.flatMap { it.items }
                dueEntities.mapNotNull { entity ->
                    allItems.find { it.id == entity.lessonItemId }?.let { it to entity }
                }
            }.collect { items ->
                _uiState.update { it.copy(reviewItems = items, isLoading = false) }
            }
        }
    }

    fun toggleAnswer() {
        _uiState.update { it.copy(showAnswer = !it.showAnswer) }
    }

    fun submitRating(quality: Int) {
        val currentState = _uiState.value
        val currentPair = currentState.currentPair ?: return

        viewModelScope.launch {
            updateReviewItemUseCase(currentPair.first.id, quality, currentPair.second)
            
            val nextIndex = currentState.currentItemIndex + 1
            if (nextIndex < currentState.reviewItems.size) {
                _uiState.update { it.copy(currentItemIndex = nextIndex, showAnswer = false) }
            } else {
                _uiState.update { it.copy(isFinished = true) }
            }
        }
    }
}
