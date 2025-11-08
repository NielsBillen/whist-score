package be.niels.billen.presentation.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.presentation.AppTheme
import be.niels.billen.presentation.background.Background
import be.niels.billen.presentation.screens.addround.AddRound
import be.niels.billen.presentation.screens.overview.Overview
import org.koin.compose.koinInject

@Composable
fun App(modifier: Modifier = Modifier) {
    AppTheme {
        Surface(Modifier.fillMaxSize()) {
            Background(Modifier.fillMaxSize())

            val viewModel: AppViewModel = koinInject()
            val screen = viewModel.screen.collectAsState()

            Box(modifier, contentAlignment = Alignment.TopCenter) {
                when (screen.value) {
                    AppScreen.OVERVIEW -> Overview { viewModel.onAction(it) }
                    AppScreen.ADD_ROUND -> AddRound { viewModel.onAction(it) }
                }
            }
        }
    }
}
