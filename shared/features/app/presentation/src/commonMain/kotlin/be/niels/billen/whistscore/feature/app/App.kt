package be.niels.billen.whistscore.feature.app

import androidx.compose.foundation.layout.BoxWithConstraints
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
import be.niels.billen.whistscore.feature.core.AppTheme
import be.niels.billen.whistscore.feature.core.Background
import be.niels.billen.whistscore.feature.addround.AddRoundView
import be.niels.billen.whistscore.feature.addround.popSlideTransition
import be.niels.billen.whistscore.feature.addround.pushSlideTransition
import be.niels.billen.whistscore.feature.addround.slideSizeTransform
import be.niels.billen.whistscore.feature.editplayers.EditPlayersView
import be.niels.billen.whistscore.feature.overview.Overview
import org.koin.compose.koinInject

@Composable
fun App(modifier: Modifier = Modifier) {
    AppTheme {
        Surface(modifier = modifier.fillMaxSize()) {
            Background(Modifier.fillMaxSize())

            val viewModel: AppViewModel = koinInject()
            val backStack = remember { mutableStateListOf<AppNavigation>(AppNavigation.OverviewRoute) }

            BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                val windowWidth = constraints.maxWidth.toInt()
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    sizeTransform = slideSizeTransform,
                    transitionSpec = pushSlideTransition(windowWidth),
                    popTransitionSpec = popSlideTransition(windowWidth),
                    entryProvider = { key ->
                        when (key) {
                            is AppNavigation.OverviewRoute -> NavEntry(key) {
                                Overview(
                                    onResetGame = { viewModel.onAction(AppAction.ResetGame) },
                                    onAddRound = { backStack.add(AppNavigation.AddRoundRoute) },
                                    onEditPlayers = { backStack.add(AppNavigation.EditPlayersRoute) },
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
                    },
                )
            }
        }
    }
}
