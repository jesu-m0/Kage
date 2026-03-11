package com.kage.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import androidx.activity.compose.BackHandler
import com.kage.app.data.model.StreamItem
import com.kage.app.ui.theme.KageCardFocused
import com.kage.app.ui.theme.KagePrimary

@Composable
fun ChannelOptionsDialog(
    item: StreamItem,
    isFavorite: Boolean,
    isBroken: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleBroken: () -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val firstOptionFocus = remember { FocusRequester() }

    // Shared gate: the ENTIRE dialog ignores input until user fully releases
    var dialogReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        firstOptionFocus.requestFocus()
    }

    // Intercept at dialog level: consume all OK events until first KeyUp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 27.dp)
            .onPreviewKeyEvent { event ->
                val isOk = event.key == Key.Enter || event.key == Key.DirectionCenter
                if (!isOk) return@onPreviewKeyEvent false
                if (!dialogReady) {
                    // Consume everything; flip ready on first release
                    if (event.type == KeyEventType.KeyUp) {
                        dialogReady = true
                    }
                    return@onPreviewKeyEvent true
                }
                false // dialog ready, let children handle
            },
        contentAlignment = Alignment.TopEnd
    ) {
        Column(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 360.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            DialogOption(
                text = if (isFavorite) "★ Quitar de favoritos" else "☆ Añadir a favoritos",
                focusRequester = firstOptionFocus,
                onClick = { onToggleFavorite(); onDismiss() }
            )

            DialogOption(
                text = if (isBroken) "✓ Marcar como funcional" else "⚠ Reportar como roto",
                onClick = { onToggleBroken(); onDismiss() }
            )
        }
    }
}

@Composable
private fun DialogOption(
    text: String,
    focusRequester: FocusRequester? = null,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isFocused) KageCardFocused else Color.Transparent)
            .onFocusChanged { isFocused = it.isFocused }
            .then(
                if (focusRequester != null) Modifier.focusRequester(focusRequester)
                else Modifier
            )
            .focusable()
            .onKeyEvent { event ->
                val isOk = event.key == Key.Enter || event.key == Key.DirectionCenter
                if (!isOk) return@onKeyEvent false
                if (event.type == KeyEventType.KeyUp) {
                    onClick()
                    true
                } else true // consume KeyDown
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isFocused) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
    }
}