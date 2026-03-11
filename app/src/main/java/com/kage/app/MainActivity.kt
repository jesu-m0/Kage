package com.kage.app

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.kage.app.data.model.Catalog
import com.kage.app.data.model.StreamItem
import com.kage.app.data.repository.CatalogRepository
import com.kage.app.ui.screens.ErrorScreen
import com.kage.app.ui.screens.HomeScreen
import com.kage.app.ui.screens.LoadingScreen
import com.kage.app.ui.screens.PlayerScreen
import com.kage.app.ui.theme.KageTheme
import com.kage.app.data.model.Category
import com.kage.app.ui.screens.CategorySelectionScreen
import com.kage.app.data.local.UserPreferencesManager
import androidx.activity.compose.BackHandler

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
    object CategorySelection : Screen()
    data class ChannelList(val category: Category) : Screen()
    data class Player(val item: StreamItem, val fromCategory: Category) : Screen()
}

sealed class CatalogState {
    object Loading : CatalogState()
    data class Success(val catalog: Catalog) : CatalogState()
    data class Error(val message: String) : CatalogState()
}

@Composable
fun KageAppContent() {
    val context = LocalContext.current
    val prefsManager = remember { UserPreferencesManager(context) }
    val repository = remember { CatalogRepository() }
    var catalogState by remember { mutableStateOf<CatalogState>(CatalogState.Loading) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.CategorySelection) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    // Reload catalog on resume
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshTrigger++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(refreshTrigger) {
        catalogState = CatalogState.Loading
        val result = repository.fetchCatalog()
        catalogState = result.fold(
            onSuccess = { CatalogState.Success(it) },
            onFailure = { CatalogState.Error(it.message ?: "Error desconocido") }
        )
    }

    when (val screen = currentScreen) {
        is Screen.CategorySelection -> {
            when (val state = catalogState) {
                is CatalogState.Loading -> LoadingScreen()
                is CatalogState.Error -> ErrorScreen(
                    message = state.message,
                    onRetry = { refreshTrigger++ }
                )
                is CatalogState.Success -> {
                    CategorySelectionScreen(
                        catalog = state.catalog,
                        prefsManager = prefsManager,
                        onCategorySelected = { currentScreen = Screen.ChannelList(it) }
                    )
                }
            }
        }
        is Screen.ChannelList -> {
            BackHandler { currentScreen = Screen.CategorySelection }
            HomeScreen(
                category = screen.category,
                prefsManager = prefsManager,
                onItemClick = { item ->
                    if (item.isAcestream) {
                        val hash = item.streamUrl.removePrefix("acestream://")
                        val started = tryStartAcestream(context, hash)
                        if (!started) {
                            Toast.makeText(context, "Acestream no está instalado", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        currentScreen = Screen.Player(item, screen.category)
                    }
                }
            )
        }
        is Screen.Player -> {
            PlayerScreen(
                item = screen.item,
                onBack = { currentScreen = Screen.ChannelList(screen.fromCategory) }
            )
        }
    }
}

private fun tryStartAcestream(context: Context, contentId: String): Boolean {
    // Option A: start_content intent with new package (3.2.x+)
    try {
        val intent = Intent("org.acestream.action.start_content")
        intent.setPackage("org.acestream.node")
        intent.data = Uri.parse("acestream:?content_id=$contentId")
        context.startActivity(intent)
        return true
    } catch (_: Exception) {}

    // Option B: start_content intent without specific package
    try {
        val intent = Intent("org.acestream.action.start_content")
        intent.data = Uri.parse("acestream:?content_id=$contentId")
        context.startActivity(intent)
        return true
    } catch (_: Exception) {}

    // Option C: ACTION_VIEW with acestream:// URI
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("acestream://$contentId")
        context.startActivity(intent)
        return true
    } catch (_: Exception) {}

    return false
}
