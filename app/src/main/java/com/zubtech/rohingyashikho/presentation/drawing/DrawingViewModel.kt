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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DrawingUiState(
    val item: LessonItem? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class DrawingViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val itemId: String = checkNotNull(savedStateHandle["itemId"])
    private val _uiState = MutableStateFlow(DrawingUiState())
    val uiState: StateFlow<DrawingUiState> = _uiState.asStateFlow()

    init {
        loadItem()
    }

    private fun loadItem() {
        viewModelScope.launch {
            contentRepository.getCourse().collect { course ->
                val item = course?.levels?.flatMap { it.units }
                    ?.flatMap { it.lessons }
                    ?.flatMap { it.items }
                    ?.find { it.id == itemId }
                _uiState.update { it.copy(item = item, isLoading = false) }
            }
        }
    }
}
