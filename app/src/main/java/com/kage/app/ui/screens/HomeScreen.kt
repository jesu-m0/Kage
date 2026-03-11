package com.kage.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kage.app.data.local.UserPreferencesManager
import com.kage.app.data.model.Category
import com.kage.app.data.model.StreamItem
import com.kage.app.ui.components.ChannelOptionsDialog
import com.kage.app.ui.components.ContentCard
import com.kage.app.ui.util.rememberSoundFeedback

@Composable
fun HomeScreen(
    category: Category,
    prefsManager: UserPreferencesManager,
    onItemClick: (StreamItem) -> Unit
) {
    var dialogItem by remember { mutableStateOf<StreamItem?>(null) }
    var prefsVersion by remember { mutableStateOf(0) }
    val soundFeedback = rememberSoundFeedback()

    // Read prefs once, keyed to version
    val favoriteIds = remember(prefsVersion) { prefsManager.getFavoriteIds() }
    val brokenIds = remember(prefsVersion) { prefsManager.getBrokenIds() }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(horizontal = 48.dp, vertical = 27.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(
                items = category.items,
                key = { it.id }
            ) { streamItem ->
                ContentCard(
                    item = streamItem,
                    isFavorite = streamItem.id in favoriteIds,
                    isBroken = streamItem.id in brokenIds,
                    onClick = { onItemClick(streamItem) },
                    onLongClick = { dialogItem = streamItem },
                    soundFeedback = soundFeedback
                )
            }
        }

        dialogItem?.let { item ->
            ChannelOptionsDialog(
                item = item,
                isFavorite = item.id in favoriteIds,
                isBroken = item.id in brokenIds,
                onToggleFavorite = {
                    prefsManager.toggleFavorite(item.id)
                    prefsVersion++
                },
                onToggleBroken = {
                    prefsManager.toggleBroken(item.id)
                    prefsVersion++
                },
                onDismiss = { dialogItem = null }
            )
        }
    }
}