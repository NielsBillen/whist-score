package be.niels.billen

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import be.niels.billen.di.appModule
import be.niels.billen.presentation.app.App
import kotlinx.browser.document
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        KoinApplication(configuration = koinConfiguration(declaration = {
            modules(
                appModule
            )
        }), content = ::App)
    }
}