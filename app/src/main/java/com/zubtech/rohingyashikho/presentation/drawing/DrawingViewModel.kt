package com.zubtech.rohingyashikho.presentation.drawing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DrawingUiState(
    val item: LessonItem? = null,
    val isLoading: Boolean = true,
    val hasNext: Boolean = false,
    val hasPrev: Boolean = false
)

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var currentItemId: String = checkNotNull(savedStateHandle["itemId"])
    private val _uiState = MutableStateFlow(DrawingUiState())
    val uiState: StateFlow<DrawingUiState> = _uiState.asStateFlow()

    private var currentList: List<LessonItem> = emptyList()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // Determine which list to use based on the ID prefix
            if (currentItemId.startsWith("c")) {
                currentList = contentRepository.getConsonantsForWriting().first()
            } else if (currentItemId.startsWith("v")) {
                currentList = contentRepository.getVowels().first()
            }
            
            updateItem()
        }
    }

    private fun updateItem() {
        val index = currentList.indexOfFirst { it.id == currentItemId }
        if (index != -1) {
            _uiState.update {
                it.copy(
                    item = currentList[index],
                    isLoading = false,
                    hasNext = index < currentList.size - 1,
                    hasPrev = index > 0
                )
            }
        } else {
            // Fallback for standalone lessons
            viewModelScope.launch {
                contentRepository.getCourse().collect { course ->
                    val item = course?.levels?.flatMap { it.units }
                        ?.flatMap { it.lessons }
                        ?.flatMap { it.items }
                        ?.find { it.id == currentItemId }
                    _uiState.update { it.copy(item = item, isLoading = false, hasNext = false, hasPrev = false) }
                }
            }
        }
    }

    fun navigateToNext() {
        val index = currentList.indexOfFirst { it.id == currentItemId }
        if (index != -1 && index < currentList.size - 1) {
            currentItemId = currentList[index + 1].id
            updateItem()
        }
    }

    fun navigateToPrev() {
        val index = currentList.indexOfFirst { it.id == currentItemId }
        if (index != -1 && index > 0) {
            currentItemId = currentList[index - 1].id
            updateItem()
        }
    }
}
