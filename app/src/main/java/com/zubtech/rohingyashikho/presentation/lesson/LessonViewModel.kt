package com.zubtech.rohingyashikho.presentation.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.audio.AudioPlayer
import com.zubtech.rohingyashikho.data.audio.AudioRecorder
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.local.entity.ProgressEntity
import com.zubtech.rohingyashikho.domain.model.Lesson
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import com.zubtech.rohingyashikho.domain.usecase.UpdateReviewItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class LessonUiState(
    val lesson: Lesson? = null,
    val currentItemIndex: Int = 0,
    val isLoading: Boolean = true,
    val isCompleted: Boolean = false,
    val isRecording: Boolean = false,

    val recordedFile: File? = null
) {
    val currentItem: LessonItem? get() = lesson?.items?.getOrNull(currentItemIndex)
}

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val progressDao: ProgressDao,
    private val updateReviewItemUseCase: UpdateReviewItemUseCase,
    private val audioPlayer: AudioPlayer,
    private val audioRecorder: AudioRecorder,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val lessonId: String = checkNotNull(savedStateHandle["lessonId"])

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            contentRepository.getLesson(lessonId).collect { lesson ->
                _uiState.update { it.copy(lesson = lesson, isLoading = false) }
            }
        }
    }

    fun playAudio() {
        uiState.value.currentItem?.let { item ->
            audioPlayer.playAsset(item.audioFileRef)
        }
    }

    fun startRecording(cacheDir: File) {
        val file = File(cacheDir, "temp_recording.mp4")
        audioRecorder.startRecording(file)
        _uiState.update { it.copy(isRecording = true, recordedFile = file) }
    }

    fun stopRecording() {
        audioRecorder.stopRecording()
        _uiState.update { it.copy(isRecording = false) }
    }

    fun playRecording() {
        uiState.value.recordedFile?.let { file ->
            // In a real app, play the file URI
            // audioPlayer.playFile(file)
        }
    }

    fun moveToNextItem() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentItemIndex + 1
        val itemsCount = currentState.lesson?.items?.size ?: 0

        viewModelScope.launch {
            currentState.currentItem?.let { item ->
                // Update progress for the current item before moving on
                progressDao.updateProgress(
                    ProgressEntity(
                        lessonItemId = item.id,
                        masteryPercentage = 100,
                        lastAccessed = System.currentTimeMillis()
                    )
                )
                // Initialize SR with SM-2 (quality 5 assumed for lesson view)
                updateReviewItemUseCase(item.id, 5, null)
            }

            if (nextIndex < itemsCount) {
                _uiState.update { it.copy(currentItemIndex = nextIndex) }
            } else {
                _uiState.update { it.copy(isCompleted = true) }
            }
        }
    }
}
