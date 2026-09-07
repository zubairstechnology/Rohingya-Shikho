package com.zubtech.rohingyashikho.data.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var player: ExoPlayer? = null

    fun playAsset(fileName: String) {
        stop()
        player = ExoPlayer.Builder(context).build().apply {
            // Media3 supports asset:/// scheme directly
            val assetUri = Uri.parse("asset:///audio/$fileName")
            val mediaItem = MediaItem.fromUri(assetUri)
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun playFile(file: java.io.File) {
        stop()
        player = ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(file))
            setMediaItem(mediaItem)
            prepare()
            play()
        }
    }

    fun stop() {
        player?.let {
            it.stop()
            it.release()
        }
        player = null
    }
}
