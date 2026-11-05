package be.niels.billen.whistscore.feature.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import be.niels.billen.whistscore.feature.addround.AddRoundView
import be.niels.billen.whistscore.feature.addround.slideTransition
import be.niels.billen.whistscore.feature.core.icons.Clubs
import be.niels.billen.whistscore.feature.core.icons.Pencil
import be.niels.billen.whistscore.feature.editplayers.EditPlayersView
import be.niels.billen.whistscore.feature.overview.Overview
import be.niels.billen.whistscore.feature.ui.AppShell
import be.niels.billen.whistscore.feature.ui.UiTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    modifier: Modifier = Modifier,
    viewModel: AppViewModel = koinViewModel(),
) {
    UiTheme {
        val backStack = remember { mutableStateListOf<AppNavigation>(AppNavigation.OverviewRoute) }

        AppShell(
            title = "Whist score",
            appIcon = Clubs,
            modifier = modifier.fillMaxSize(),
            actions = {
                IconButton(onClick = { backStack.add(AppNavigation.EditPlayersRoute) }, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Pencil, contentDescription = "Edit players", modifier = Modifier.size(16.dp))
                }
            },
        ) {
            NavDisplay(
                modifier = Modifier.fillMaxSize(),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                transitionSpec = slideTransition(1),
                popTransitionSpec = slideTransition(-1),
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = { key ->
                    when (key) {
                        is AppNavigation.OverviewRoute -> NavEntry(key) {
                            Overview(
                                onResetGame = { viewModel.onAction(AppAction.ResetGame) },
                                onAddRound = { backStack.add(AppNavigation.AddRoundRoute) },
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
