package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.audio.AudioPlayer
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlphabetViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val alphabetItems: StateFlow<List<LessonItem>> = contentRepository.getCourse()
        .map { course ->
            course?.levels?.find { it.id == "lvl_beginner" }
                ?.units?.find { it.id == "unit_hanifi_1" }
                ?.lessons?.flatMap { it.items } ?: emptyList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun playAudio(item: LessonItem) {
        if (item.audioFileRef.isNotEmpty()) {
            audioPlayer.playAsset(item.audioFileRef)
        }
    }
    
    fun playAlphabetSong() {
        // Placeholder for alphabet song if available
        // audioPlayer.playAsset("audio/alphabet_song.mp3")
    }
}
