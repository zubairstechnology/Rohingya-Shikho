package com.zubtech.rohingyashikho.data.audio

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataSourceFactory: DataSource.Factory
) {
    private val player: ExoPlayer by lazy {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()
            .apply {
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

    /**
     * Tries to play a resource. If it starts with http, it plays as a URL.
     * Otherwise, it tries to play from assets.
     */
    fun playAsset(fileName: String) {
        if (fileName.isBlank()) return
        
        if (fileName.startsWith("http")) {
            playUrl(fileName)
            return
        }

        try {
            val assetUri = Uri.parse("asset:///$fileName")
            playUri(assetUri)
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error loading asset: $fileName", e)
        }
    }

    fun playFile(file: java.io.File) {
        try {
            playUri(Uri.fromFile(file))
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error loading file: ${file.name}", e)
        }
    }

    fun playUrl(url: String) {
        if (url.isBlank()) return
        try {
            var finalUrl = url
            if (url.contains("drive.google.com")) {
                // Convert various Google Drive link formats to direct download links for streaming
                val fileId = "/d/([^/]+)".toRegex().find(url)?.groupValues?.get(1)
                    ?: "id=([^&]+)".toRegex().find(url)?.groupValues?.get(1)

                if (fileId != null) {
                    finalUrl = "https://docs.google.com/uc?export=download&id=$fileId"
                }
            }
            playUri(Uri.parse(finalUrl))
        } catch (e: Exception) {
            Log.e("AudioPlayer", "Error loading URL: $url", e)
        }
    }

    private fun playUri(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        player.stop()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun stop() {
        player.stop()
        _isPlaying.value = false
    }
    
    fun release() {
        player.release()
    }
}
