package com.kage.app.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.datasource.DefaultDataSource
import com.kage.app.data.model.StreamItem

class KagePlayer(context: Context) {

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(context).build()

    private var onError: ((PlaybackException) -> Unit)? = null

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                onError?.invoke(error)
            }
        })
    }

    fun setOnErrorListener(listener: (PlaybackException) -> Unit) {
        onError = listener
    }

    @OptIn(UnstableApi::class)
    fun play(item: StreamItem, context: Context) {
        val url = if (item.isAcestream) {
            AcestreamResolver.resolve(item.streamUrl) ?: return
        } else {
            item.streamUrl
        }

        val mediaItem = MediaItem.fromUri(url)

        if (item.isHls) {
            val dataSourceFactory = DefaultDataSource.Factory(context)
            val hlsSource = HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mediaItem)
            exoPlayer.setMediaSource(hlsSource)
        } else {
            exoPlayer.setMediaItem(mediaItem)
        }

        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    fun stop() {
        exoPlayer.stop()
    }

    fun release() {
        exoPlayer.release()
    }
}
