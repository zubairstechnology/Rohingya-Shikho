package com.zubtech.rohingyashikho.presentation.level

import androidx.lifecycle.ViewModel
import com.zubtech.rohingyashikho.data.audio.AudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LevelDetailViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    fun playAudio(url: String) {
        audioPlayer.playUrl(url)
    }

    fun stopAudio() {
        audioPlayer.stop()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
    }
}
