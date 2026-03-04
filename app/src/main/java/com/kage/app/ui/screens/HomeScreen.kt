package com.kage.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kage.app.data.model.Catalog
import com.kage.app.data.model.StreamItem
import com.kage.app.ui.components.ContentCard

@Composable
fun HomeScreen(
    catalog: Catalog,
    onItemClick: (StreamItem) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(horizontal = 48.dp, vertical = 27.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        catalog.categories.forEach { category ->
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
                    onClick = { onItemClick(streamItem) }
                )
            }
        }
    }
}
