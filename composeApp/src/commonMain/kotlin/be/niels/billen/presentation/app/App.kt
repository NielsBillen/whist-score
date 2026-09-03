package be.niels.billen.presentation.app

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
import be.niels.billen.presentation.AppTheme
import be.niels.billen.presentation.background.Background
import be.niels.billen.presentation.screens.addround.AddRoundView
import be.niels.billen.presentation.screens.editplayers.EditPlayersView
import be.niels.billen.presentation.screens.overview.Overview
import org.koin.compose.koinInject

private data object OverviewRoute : NavKey

private data object AddRoundRoute : NavKey

private data object EditPlayersRoute : NavKey

@Composable
fun App(modifier: Modifier = Modifier) {
    AppTheme {
        Surface(Modifier.fillMaxSize()) {
            Background(Modifier.fillMaxSize())

            val viewModel: AppViewModel = koinInject()
            val backStack = remember { mutableStateListOf<Any>(OverviewRoute) }

            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = { key ->
                        when (key) {
                            is OverviewRoute -> NavEntry(key) {
                                Overview(
                                    onAction = viewModel::onAction,
                                    onAddRound = { backStack.add(AddRoundRoute) },
                                    onEditPlayers = { backStack.add(EditPlayersRoute) },
                                )
                            }

                            is AddRoundRoute -> NavEntry(key) {
                                AddRoundView(
                                    onCancel = { backStack.removeLastOrNull() },
                                    onSave = { round ->
                                        viewModel.onAction(AppAction.AddRound(round))
                                        backStack.removeLastOrNull()
                                    },
                                )
                            }

                            is EditPlayersRoute -> NavEntry(key) {
                                EditPlayersView(
                                    onSave = { backStack.removeLastOrNull() },
                                )
                            }

                            else -> error("Unknown route: $key")
                        }
                    }
                )
            }
        }
    }
}
