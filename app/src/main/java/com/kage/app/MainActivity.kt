package com.kage.app

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
    object Home : Screen()
    data class Player(val item: StreamItem) : Screen()
}

sealed class CatalogState {
    object Loading : CatalogState()
    data class Success(val catalog: Catalog) : CatalogState()
    data class Error(val message: String) : CatalogState()
}

@Composable
fun KageAppContent() {
    val repository = remember { CatalogRepository() }
    var catalogState by remember { mutableStateOf<CatalogState>(CatalogState.Loading) }
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
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
                is CatalogState.Success -> {
                    val context = LocalContext.current
                    HomeScreen(
                        catalog = state.catalog,
                        onItemClick = { item ->
                            if (item.isAcestream) {
                                val hash = item.streamUrl.removePrefix("acestream://")
                                val aceIntent = Intent("org.acestream.action.start_content")
                                aceIntent.data = Uri.parse("acestream:?content_id=$hash")
                                try {
                                    context.startActivity(aceIntent)
                                } catch (_: Exception) {
                                    Toast.makeText(context, "Acestream Media app not installed", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                currentScreen = Screen.Player(item)
                            }
                        }
                    )
                }
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
