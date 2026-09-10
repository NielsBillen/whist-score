package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.Style
import org.koin.compose.koinInject

@Composable
fun AddRoundView(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onSave: (Round) -> Unit,
) {
    val viewModel: AddRoundViewModel = koinInject()
    val state by viewModel.state.collectAsState()
    val game by viewModel.game.collectAsState()
    val backStack = remember { mutableStateListOf<AddRoundNavigation>(AddRoundNavigation.SelectRoundTypeRoute) }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AddRoundEffect.NextScreen -> backStack.add(screenToRoute(state.screen))
                AddRoundEffect.PreviousScreen -> backStack.removeLastOrNull()
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) viewModel.onAction(AddRoundAction.PreviousScreen)
            else onCancel()
        },
        modifier = modifier.padding(Style.Dimensions.paddingLarge),
        contentAlignment = Alignment.Center,
        entryProvider = { key ->
            when (key) {
                is AddRoundNavigation.SelectRoundTypeRoute -> NavEntry(key) {
                    RoundTypeInputScreen(
                        initialRoundType = state.roundType,
                        onAction = { viewModel.onAction(it) },
                        onCancel = onCancel,
                    )
                }

                is AddRoundNavigation.SelectPlayersRoute -> NavEntry(key) {
                    val roundType = requireNotNull(state.roundType)
                    PlayerSelectionScreen(
                        roundType = roundType,
                        players = game.players,
                        initialSelection = state.players,
                        onCancel = { viewModel.onAction(AddRoundAction.PreviousScreen) },
                        onAction = { viewModel.onAction(it) },
                    )
                }

                is AddRoundNavigation.SelectSlamsRoute -> NavEntry(key) {
                    SlamInputScreen(
                        initialSlams = state.slams,
                        onCancel = { viewModel.onAction(AddRoundAction.PreviousScreen) },
                        onAction = { viewModel.onAction(it) },
                    )
                }

                is AddRoundNavigation.SelectBidAchievedRoute -> NavEntry(key) {
                    BidAchievedInput(
                        initialBidAchieved = state.playerWon,
                        onCancel = { viewModel.onAction(AddRoundAction.PreviousScreen) },
                        onAction = { viewModel.onAction(it) },
                    )
                }

                is AddRoundNavigation.SummaryRoute -> NavEntry(key) {
                    val round = requireNotNull(state.round)
                    SummaryScreen(
                        game = game,
                        round = round,
                        onBack = { viewModel.onAction(AddRoundAction.PreviousScreen) },
                        onNext = { onSave(round) },
                    )
                }
            }
        },
        transitionSpec = pushSlideTransition(),
        popTransitionSpec = popSlideTransition(),
    )
}

private fun screenToRoute(screen: AddRoundScreen): AddRoundNavigation = when (screen) {
    AddRoundScreen.SELECT_ROUND_TYPE -> AddRoundNavigation.SelectRoundTypeRoute
    AddRoundScreen.SELECT_PLAYERS -> AddRoundNavigation.SelectPlayersRoute
    AddRoundScreen.SELECT_SLAMS -> AddRoundNavigation.SelectSlamsRoute
    AddRoundScreen.SELECT_BID_ACHIEVED -> AddRoundNavigation.SelectBidAchievedRoute
    AddRoundScreen.SUMMARY -> AddRoundNavigation.SummaryRoute
}
