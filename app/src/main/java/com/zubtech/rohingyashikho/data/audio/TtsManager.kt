package com.zubtech.rohingyashikho.data.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Set to Indian English for a male-like Indian voice (depends on system voices)
            val result = tts?.setLanguage(Locale("en", "IN"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsManager", "Language not supported")
                tts?.setLanguage(Locale.US)
            }
            isInitialized = true
            
            // Try to find a male voice if possible
            tts?.voices?.find { it.name.contains("male", ignoreCase = true) && it.locale.country == "IN" }?.let {
                tts?.voice = it
            }
        } else {
            Log.e("TtsManager", "Initialization failed")
        }
    }

    fun speak(text: String) {
        if (isInitialized && text.isNotBlank()) {
            _isPlaying.value = true
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_id")
            // We don't have a perfect "done" callback here without a listener, 
            // but this is enough for simple usage.
        }
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = false
    }

    fun setApiKey(key: String) {
        // Deprecated: No API key needed for native TTS
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
    }
}
