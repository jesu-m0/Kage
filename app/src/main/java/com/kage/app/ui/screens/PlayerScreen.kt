package com.kage.app.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import com.kage.app.data.model.StreamItem
import com.kage.app.player.KagePlayer

@OptIn(UnstableApi::class)
@Composable
fun PlayerScreen(
    item: StreamItem,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val player = remember { KagePlayer(context) }

    BackHandler { onBack() }

    DisposableEffect(item) {
        player.play(item, context)
        onDispose {
            player.stop()
            player.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player.exoPlayer
                useController = true
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    )
}
