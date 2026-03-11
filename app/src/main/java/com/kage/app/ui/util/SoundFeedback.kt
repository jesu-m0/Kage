package com.kage.app.ui.util

import android.media.AudioManager
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView

class SoundFeedback(private val view: View) {
    fun playNavigationSound() {
        view.playSoundEffect(AudioManager.FX_FOCUS_NAVIGATION_UP)
    }
}

@Composable
fun rememberSoundFeedback(): SoundFeedback {
    val view = LocalView.current
    return remember { SoundFeedback(view) }
}