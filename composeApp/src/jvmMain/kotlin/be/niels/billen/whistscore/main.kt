package be.niels.billen.whistscore

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import be.niels.billen.whistscore.di.appModule
import be.niels.billen.whistscore.presentation.app.App
import org.koin.core.context.GlobalContext.startKoin

fun main() = application {
    startKoin {
        modules(appModule)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Wiezen",
    ) {
        App()
    }
}