package com.kage.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.kage.app.data.model.Catalog
import com.kage.app.data.model.StreamItem
import com.kage.app.data.repository.CatalogRepository
import com.kage.app.ui.screens.ErrorScreen
import com.kage.app.ui.screens.HomeScreen
import com.kage.app.ui.screens.LoadingScreen
import com.kage.app.ui.screens.PlayerScreen
import com.kage.app.ui.theme.KageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KageTheme {
                KageAppContent()
            }
        }
    }
}

sealed class Screen {
    data object Home : Screen()
    data class Player(val item: StreamItem) : Screen()
}

sealed class CatalogState {
    data object Loading : CatalogState()
    data class Success(val catalog: Catalog) : CatalogState()
    data class Error(val message: String) : CatalogState()
}

@Composable
fun KageAppContent() {
    val repository = remember { CatalogRepository() }
    var catalogState by remember { mutableStateOf<CatalogState>(CatalogState.Loading) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var refreshTrigger by remember { mutableStateOf(0) }

    // Reload catalog on resume
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        refreshTrigger++
    }

    LaunchedEffect(refreshTrigger) {
        catalogState = CatalogState.Loading
        val result = repository.fetchCatalog()
        catalogState = result.fold(
            onSuccess = { CatalogState.Success(it) },
            onFailure = { CatalogState.Error(it.message ?: "Unknown error") }
        )
    }

    when (val screen = currentScreen) {
        is Screen.Home -> {
            when (val state = catalogState) {
                is CatalogState.Loading -> LoadingScreen()
                is CatalogState.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { refreshTrigger++ }
                )
                is CatalogState.Success -> HomeScreen(
                    catalog = state.catalog,
                    onItemClick = { item ->
                        currentScreen = Screen.Player(item)
                    }
                )
            }
        }
        is Screen.Player -> {
            PlayerScreen(
                item = screen.item,
                onBack = { currentScreen = Screen.Home }
            )
        }
    }
}
