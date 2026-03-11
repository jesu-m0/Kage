package com.kage.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kage.app.data.model.StreamItem
import com.kage.app.ui.theme.KageCardFocused
import com.kage.app.ui.theme.KagePrimary
import com.kage.app.ui.util.SoundFeedback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ContentCard(
    item: StreamItem,
    isFavorite: Boolean,
    isBroken: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    soundFeedback: SoundFeedback,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var longPressJob by remember { mutableStateOf<Job?>(null) }
    var longPressFired by remember { mutableStateOf(false) }
    var waitingForFinalRelease by remember { mutableStateOf(false) }

    val borderColor = when {
        isBroken && isFocused -> Color(0xFFFF6B6B)
        isFocused -> KagePrimary
        isBroken -> Color(0xFFFF6B6B).copy(alpha = 0.5f)
        else -> Color.Transparent
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isFocused) KageCardFocused else MaterialTheme.colorScheme.surface)
            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
            .onFocusChanged {
                if (it.isFocused && !isFocused) {
                    soundFeedback.playNavigationSound()
                }
                isFocused = it.isFocused
            }
            .focusable()
            .onKeyEvent { event ->
                val isOk = event.key == Key.Enter || event.key == Key.DirectionCenter
                if (!isOk) return@onKeyEvent false

                // After long press fired, eat EVERYTHING until final release
                if (waitingForFinalRelease) {
                    if (event.type == KeyEventType.KeyUp) {
                        waitingForFinalRelease = false
                        longPressFired = false
                    }
                    return@onKeyEvent true
                }

                when (event.type) {
                    KeyEventType.KeyDown -> {
                        if (longPressJob == null && !longPressFired) {
                            longPressJob = scope.launch {
                                delay(600)
                                longPressFired = true
                                waitingForFinalRelease = true
                                onLongClick()
                            }
                        }
                        true
                    }
                    KeyEventType.KeyUp -> {
                        longPressJob?.cancel()
                        longPressJob = null
                        if (!longPressFired) {
                            onClick()
                        }
                        longPressFired = false
                        true
                    }
                    else -> false
                }
            }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF2A2A2A)),
            contentAlignment = Alignment.Center
        ) {
            if (item.thumbnail.isNotBlank()) {
                AsyncImage(
                    model = item.thumbnail,
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = item.streamType.uppercase(),
                    style = MaterialTheme.typography.bodySmall,
                    color = KagePrimary
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isFavorite) {
                    Badge(text = "★", color = Color(0xFFFFD700))
                }
                if (isBroken) {
                    Badge(text = "⚠", color = Color(0xFFFF6B6B))
                }
            }
        }

        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = if (isBroken)
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.onSurface
        )

        if (item.description.isNotBlank()) {
            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = if (isBroken) 0.dp else 4.dp)
            )
        }

        if (isBroken) {
            Text(
                text = "ID: ${item.id}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFF6B6B).copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
private fun Badge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelSmall
        )
    }
}