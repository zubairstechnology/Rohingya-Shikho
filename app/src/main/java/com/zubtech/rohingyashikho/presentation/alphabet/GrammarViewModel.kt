package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.audio.AudioPlayer
import com.zubtech.rohingyashikho.data.audio.TtsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GrammarViewModel @Inject constructor(
    private val audioPlayer: AudioPlayer,
    private val ttsManager: TtsManager
) : ViewModel() {

    private val _highlightedNativeIndex = MutableStateFlow(-1)
    val highlightedNativeIndex: StateFlow<Int> = _highlightedNativeIndex.asStateFlow()

    private val _highlightedEnglishIndex = MutableStateFlow(-1)
    val highlightedEnglishIndex: StateFlow<Int> = _highlightedEnglishIndex.asStateFlow()

    private val _playingAudioType = MutableStateFlow<String?>(null) // "native", "english", "tts", "native_word", "example_tts", "example_native"
    val playingAudioType: StateFlow<String?> = _playingAudioType.asStateFlow()

    private val _playingExampleIndex = MutableStateFlow(-1)
    val playingExampleIndex: StateFlow<Int> = _playingExampleIndex.asStateFlow()

    private var playbackJob: Job? = null

    fun speakEnglish(text: String) {
        stopAudio()
        viewModelScope.launch {
            _playingAudioType.value = "tts"
            ttsManager.speak(text)
        }
    }

    fun speakExampleEnglish(text: String, index: Int) {
        stopAudio()
        viewModelScope.launch {
            _playingExampleIndex.value = index
            _playingAudioType.value = "example_tts"
            ttsManager.speak(text)
        }
    }

    fun playNativeSong(audioFile: String, wordTimings: List<Long>) {
        if (audioFile.isEmpty()) return
        stopAudio()
        viewModelScope.launch {
            if (audioFile.startsWith("http")) {
                audioPlayer.playUrl(audioFile)
            } else {
                audioPlayer.playAsset("audio/grammar/$audioFile")
            }
            _playingAudioType.value = "native"
            if (wordTimings.isNotEmpty()) {
                startHighlighting(wordTimings, isNative = true)
            }
        }
    }

    fun playEnglishSong(audioFile: String, wordTimings: List<Long>) {
        if (audioFile.isEmpty()) return
        stopAudio()
        viewModelScope.launch {
            if (audioFile.startsWith("http")) {
                audioPlayer.playUrl(audioFile)
            } else {
                audioPlayer.playAsset("audio/grammar/$audioFile")
            }
            _playingAudioType.value = "english"
            if (wordTimings.isNotEmpty()) {
                startHighlighting(wordTimings, isNative = false)
            }
        }
    }

    fun playExampleNative(audioFile: String, index: Int) {
        if (audioFile.isEmpty()) return
        stopAudio()
        viewModelScope.launch {
            if (audioFile.startsWith("http")) {
                audioPlayer.playUrl(audioFile)
            } else {
                audioPlayer.playAsset("audio/grammar/examples/$audioFile")
            }
            _playingExampleIndex.value = index
            _playingAudioType.value = "example_native"
            
            // Wait for audio duration (simplified)
            delay(3000) 
            if (_playingExampleIndex.value == index && _playingAudioType.value == "example_native") {
                stopAudio()
            }
        }
    }

    fun playNativeWord(audioFile: String) {
        if (audioFile.isEmpty()) return
        stopAudio()
        viewModelScope.launch {
            if (audioFile.startsWith("http")) {
                audioPlayer.playUrl(audioFile)
            } else {
                audioPlayer.playAsset("audio/grammar/$audioFile")
            }
            _playingAudioType.value = "native_word"
        }
    }

    private fun startHighlighting(timings: List<Long>, isNative: Boolean) {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            val expectedType = if (isNative) "native" else "english"
            
            while (_playingAudioType.value == expectedType) {
                val elapsed = System.currentTimeMillis() - startTime
                
                var currentIdx = -1
                for (i in timings.indices) {
                    if (elapsed >= timings[i]) {
                        currentIdx = i
                    } else {
                        break
                    }
                }
                
                if (isNative) {
                    _highlightedNativeIndex.value = currentIdx
                } else {
                    _highlightedEnglishIndex.value = currentIdx
                }
                
                if (currentIdx == timings.size - 1 && elapsed > timings.last() + 2000) {
                    break
                }
                delay(16)
            }
            if (isNative) _highlightedNativeIndex.value = -1 else _highlightedEnglishIndex.value = -1
            if (_playingAudioType.value == expectedType) {
                _playingAudioType.value = null
            }
        }
    }

    fun stopAudio() {
        audioPlayer.stop()
        ttsManager.stop()
        playbackJob?.cancel()
        _highlightedNativeIndex.value = -1
        _highlightedEnglishIndex.value = -1
        _playingAudioType.value = null
        _playingExampleIndex.value = -1
    }

    override fun onCleared() {
        stopAudio()
        super.onCleared()
    }
}
