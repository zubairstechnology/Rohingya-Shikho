package com.zubtech.rohingyashikho.data.audio

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    _isPlaying.value = playing
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        _isPlaying.value = false
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    Log.e("AudioPlayer", "Error playing audio: ${error.message}", error)
                    _isPlaying.value = false
                }
            })
        }
    }
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun playAsset(fileName: String) {
        try {
            val assetUri = Uri.parse("asset:///$fileName")
            val mediaItem = MediaItem.fromUri(assetUri)
            
            player.stop()
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error loading asset: $fileName", e)
        }
    }

    fun playFile(file: java.io.File) {
        try {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
            
            player.stop()
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error loading file: ${file.name}", e)
        }
    }

    fun stop() {
        player.stop()
        _isPlaying.value = false
    }
    
    fun release() {
        player.release()
    }
}
