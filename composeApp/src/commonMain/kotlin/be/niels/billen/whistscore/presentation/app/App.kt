package be.niels.billen.whistscore.presentation.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import be.niels.billen.whistscore.presentation.AppTheme
import be.niels.billen.whistscore.presentation.background.Background
import be.niels.billen.whistscore.presentation.screens.addround.AddRoundView
import be.niels.billen.whistscore.presentation.screens.editplayers.EditPlayersView
import be.niels.billen.whistscore.presentation.screens.overview.Overview
import org.koin.compose.koinInject

@Composable
fun App(modifier: Modifier = Modifier) {
    AppTheme {
        Surface(modifier = modifier.fillMaxSize()) {
            Background(Modifier.fillMaxSize())

            val viewModel: AppViewModel = koinInject()
            val backStack = remember { mutableStateListOf<AppNavigation>(AppNavigation.OverviewRoute) }

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { key ->
                        when (key) {
                            is AppNavigation.OverviewRoute -> NavEntry(key) {
                                Overview(
                                    onAction = viewModel::onAction,
                                    onAppNavigation = { backStack.add(it) },
                                )
                            }

                            is AppNavigation.AddRoundRoute -> NavEntry(key) {
                                AddRoundView(
                                    onCancel = { backStack.removeLastOrNull() },
                                    onSave = { round ->
                                        viewModel.onAction(AppAction.AddRound(round))
                                        backStack.removeLastOrNull()
                                    },
                                )
                            }

                            is AppNavigation.EditPlayersRoute -> NavEntry(key) {
                                EditPlayersView(
                                    onSave = { backStack.removeLastOrNull() },
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
