package com.kage.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kage.app.data.local.UserPreferencesManager
import com.kage.app.data.model.Catalog
import com.kage.app.data.model.Category
import com.kage.app.ui.theme.KageCardFocused
import com.kage.app.ui.theme.KagePrimary
import com.kage.app.ui.util.SoundFeedback
import com.kage.app.ui.util.rememberSoundFeedback

@Composable
fun CategorySelectionScreen(
    catalog: Catalog,
    prefsManager: UserPreferencesManager,
    onCategorySelected: (Category) -> Unit
) {
    val soundFeedback = rememberSoundFeedback()

    val favoriteIds = prefsManager.getFavoriteIds()
    val brokenIds = prefsManager.getBrokenIds()

    val allItems = catalog.categories.flatMap { it.items }
    val favoriteItems = allItems.filter { it.id in favoriteIds }
    val brokenItems = allItems.filter { it.id in brokenIds }

    val displayCategories = buildList {
        if (favoriteItems.isNotEmpty()) {
            add(Category(name = "★ Favoritos", items = favoriteItems))
        }
        addAll(catalog.categories)
        if (brokenItems.isNotEmpty()) {
            add(Category(name = "⚠ Rotos", items = brokenItems))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 48.dp, vertical = 27.dp)
    ) {
        Text(
            text = "Selecciona una sección",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 27.dp)
        ) {
            items(
                items = displayCategories,
                key = { it.name }
            ) { category ->
                CategoryCard(
                    category = category,
                    soundFeedback = soundFeedback,
                    onClick = { onCategorySelected(category) }
                )
            }
        }
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    soundFeedback: SoundFeedback,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val isFavoritesCategory = category.name.startsWith("★")
    val isBrokenCategory = category.name.startsWith("⚠")

    val bgColor = when {
        isFavoritesCategory && isFocused -> Color(0xFF3D3520)
        isBrokenCategory && isFocused -> Color(0xFF3D2020)
        isFocused -> KageCardFocused
        isFavoritesCategory -> Color(0xFF2A2518)
        isBrokenCategory -> Color(0xFF2A1818)
        else -> MaterialTheme.colorScheme.surface
    }

    val focusBorderColor = when {
        isFavoritesCategory -> Color(0xFFFFD700)
        isBrokenCategory -> Color(0xFFFF6B6B)
        else -> KagePrimary
    }

    val titleColor = when {
        isFavoritesCategory -> Color(0xFFFFD700)
        isBrokenCategory -> Color(0xFFFF6B6B)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                2.dp,
                if (isFocused) focusBorderColor else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .onFocusChanged {
                if (it.isFocused && !isFocused) {
                    soundFeedback.playNavigationSound()
                }
                isFocused = it.isFocused
            }
            .focusable()
            .onKeyEvent { event ->
                if (event.type == KeyEventType.KeyUp &&
                    (event.key == Key.Enter || event.key == Key.DirectionCenter)
                ) {
                    onClick(); true
                } else false
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = titleColor
            )
            Text(
                text = "${category.items.size} canales",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}