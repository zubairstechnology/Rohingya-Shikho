package com.zubtech.rohingyashikho.presentation.alphabet

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.audio.AudioPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GrammarViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioPlayer: AudioPlayer
) : ViewModel(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private val _isTtsReady = MutableStateFlow(false)

    private val _highlightedNativeIndex = MutableStateFlow(-1)
    val highlightedNativeIndex: StateFlow<Int> = _highlightedNativeIndex.asStateFlow()

    private val _highlightedEnglishIndex = MutableStateFlow(-1)
    val highlightedEnglishIndex: StateFlow<Int> = _highlightedEnglishIndex.asStateFlow()

    private val _playingAudioType = MutableStateFlow<String?>(null) // "native", "english", "tts", "native_word"
    val playingAudioType: StateFlow<String?> = _playingAudioType.asStateFlow()

    private var playbackJob: Job? = null
    private var currentTextToSpeak: String = ""

    init {
        tts = TextToSpeech(context, this)
        setupUtteranceListener()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            setupBritishMaleVoice()
            _isTtsReady.value = true
        }
    }

    private fun setupUtteranceListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _playingAudioType.value = "tts"
            }

            override fun onDone(utteranceId: String?) {
                _playingAudioType.value = null
                _highlightedEnglishIndex.value = -1
            }

            override fun onError(utteranceId: String?) {
                _playingAudioType.value = null
                _highlightedEnglishIndex.value = -1
            }

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                if (currentTextToSpeak.isNotEmpty()) {
                    val words = currentTextToSpeak.split(Regex("\\s+"))
                    var charCount = 0
                    var targetWordIdx = -1
                    for (i in words.indices) {
                        if (start >= charCount && start <= charCount + words[i].length) {
                            targetWordIdx = i
                            break
                        }
                        charCount += words[i].length + 1
                    }
                    _highlightedEnglishIndex.value = targetWordIdx
                }
            }
        })
    }

    private fun setupBritishMaleVoice() {
        tts?.language = Locale.UK
        try {
            val voices = tts?.voices
            val britishMaleVoice = voices?.find { voice ->
                val name = voice.name.lowercase(Locale.ROOT)
                val isUK = voice.locale.language == "en" && voice.locale.country == "GB"
                val isMale = name.contains("male") || name.contains("low") || name.contains("fis") || name.contains("rjs")
                isUK && isMale
            } ?: voices?.find { it.locale.country == "GB" && it.name.lowercase().contains("male") }
              ?: voices?.find { it.locale.country == "GB" }
            
            britishMaleVoice?.let { tts?.voice = it }
            // 龜 Speed: 0.9–1.0×
            tts?.setSpeechRate(0.95f)
            // 🎵 Pitch: Natural / Default
            tts?.setPitch(1.0f) 
        } catch (e: Exception) {}
    }

    fun speakEnglish(text: String) {
        stopAudio()
        if (_isTtsReady.value) {
            setupBritishMaleVoice()
            currentTextToSpeak = text
            _playingAudioType.value = "tts"
            val params = Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "grammar_tts")
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "grammar_tts")
        }
    }

    fun playNativeSong(audioFile: String, wordTimings: List<Long>) {
        if (audioFile.isEmpty()) return
        stopAudio()
        viewModelScope.launch {
            audioPlayer.playAsset("audio/grammar/$audioFile")
            _playingAudioType.value = "native"
            startHighlighting(wordTimings, isNative = true)
        }
    }

    fun playEnglishSong(audioFile: String, wordTimings: List<Long>) {
        if (audioFile.isEmpty()) return
        stopAudio()
        viewModelScope.launch {
            audioPlayer.playAsset("audio/grammar/$audioFile")
            _playingAudioType.value = "english"
            startHighlighting(wordTimings, isNative = false)
        }
    }

    fun playNativeWord(audioFile: String) {
        if (audioFile.isEmpty()) return
        stopAudio()
        viewModelScope.launch {
            audioPlayer.playAsset("audio/grammar/$audioFile")
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
                    if (_highlightedNativeIndex.value != currentIdx) {
                        _highlightedNativeIndex.value = currentIdx
                    }
                } else {
                    if (_highlightedEnglishIndex.value != currentIdx) {
                        _highlightedEnglishIndex.value = currentIdx
                    }
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
        tts?.stop()
        playbackJob?.cancel()
        _highlightedNativeIndex.value = -1
        _highlightedEnglishIndex.value = -1
        _playingAudioType.value = null
        currentTextToSpeak = ""
    }

    override fun onCleared() {
        tts?.stop()
        tts?.shutdown()
        playbackJob?.cancel()
        super.onCleared()
    }
}
