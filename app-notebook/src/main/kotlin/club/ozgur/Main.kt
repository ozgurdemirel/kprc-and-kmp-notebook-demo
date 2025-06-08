package club.ozgur

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import cafe.adriel.voyager.navigator.Navigator
import club.ozgur.screens.HomeScreen

fun main() = application {
    var windowVisible by remember { mutableStateOf(true) }

    val windowState = rememberWindowState()

    val trayState = rememberTrayState()

    val trayIcon = try {
        useResource("icon.png") { stream ->
            BitmapPainter(loadImageBitmap(stream))
        }
    } catch (e: Exception) {
        println("Tray icon could not be loaded: ${e.message}")
        null
    }

    val showWindow = {
        windowVisible = true
        windowState.isMinimized = false
        windowState.placement = windowState.placement
    }


    Window(
        onCloseRequest = { windowVisible = false },
        title = "My Notebook",
        visible = windowVisible,
        state = windowState

    ) {
        MaterialTheme {
            Navigator(HomeScreen)
        }
    }

    if (trayIcon != null) {
        Tray(
            icon = trayIcon,
            state = trayState,
            tooltip = "My Notebook",
            onAction = {
                showWindow()
            },
            menu = {
                Item(
                    "Show Notebook",
                    onClick = {
                        showWindow()
                    }
                )
                Separator()
                Item(
                    "Exit",
                    onClick = {
                        exitApplication()
                    }
                )
            }
        )
    } else {
        if (!windowVisible) {
            println("Tray icon failed to load and window is not visible. Exiting application to prevent inaccessibility.")
            exitApplication()
        }
    }
}