package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.game.Game
import kotlinx.coroutines.flow.SharedFlow
import org.koin.compose.koinInject

@Composable
fun AddRoundView(
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinInject(),
    onCancel: () -> Unit,
    onSave: (Round) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val game by viewModel.game.collectAsState()


    AddRoundView(
        modifier = modifier,
        onCancel = onCancel,
        onSave = onSave,
        state = state,
        game = game,
        onAction = viewModel::onAction,
        effects = viewModel.effects
    )
}

@Composable
internal fun AddRoundView(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onSave: (Round) -> Unit,
    onAction: (AddRoundAction) -> Unit,
    effects: SharedFlow<AddRoundEffect>,
    state: AddRoundState,
    game: Game
) {
    val backStack = remember { mutableStateListOf(AddRoundScreen.SELECT_ROUND_TYPE) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddRoundScreens(
            onAction = onAction,
            effects = effects,
            state = state,
            game = game,
            backStack = backStack,
            modifier = Modifier.weight(1f),
        )

        backStack.lastOrNull()?.let { screen ->
            ButtonRow(
                screen = screen,
                onBack = { backStack.removeLast() },
                onSave = { state.round?.let(onSave) },
                onCancel = onCancel
            )
        }
    }
}


@Composable
private fun AddRoundScreens(
    modifier: Modifier = Modifier,
    onAction: (AddRoundAction) -> Unit,
    effects: SharedFlow<AddRoundEffect>,
    state: AddRoundState,
    game: Game,
    backStack: SnapshotStateList<AddRoundScreen>
) {
    LaunchedEffect(effects) {
        effects.collect { effect ->
            when (effect) {
                is AddRoundEffect.NextScreen -> backStack.add(effect.screen)
                AddRoundEffect.PreviousScreen -> backStack.removeLastOrNull()
            }
        }
    }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        contentAlignment = Alignment.Center,
        entryProvider = { key ->
            when (key) {
                AddRoundScreen.SELECT_ROUND_TYPE -> NavEntry(key) {
                    RoundTypeInputScreen(
                        selectedRoundType = state.roundType,
                        onRoundTypeSelected = { onAction(AddRoundAction.SetRoundType(it)) },
                    )
                }

                AddRoundScreen.SELECT_PLAYERS -> NavEntry(key) {
                    val roundType = requireNotNull(state.roundType)
                    PlayerSelectionScreen(
                        roundType = roundType,
                        players = game.players,
                        selection = state.players,
                        onSelectionChange = { id, selected ->
                            onAction(
                                AddRoundAction.SetPlayerSelected(
                                    id,
                                    selected
                                )
                            )
                        },
                    )
                }

                AddRoundScreen.SELECT_SLAMS -> NavEntry(key) {
                    SlamInputScreen(
                        selectedSlams = state.slams,
                        onSlamsSelected = { onAction(AddRoundAction.SetSlams(it)) },
                    )
                }

                AddRoundScreen.SELECT_BID_ACHIEVED -> NavEntry(key) {
                    BidAchievedInput(
                        selectedBidAchieved = state.bidAchieved,
                        onBidAchievedSelected = { onAction(AddRoundAction.SetBidAchieved(it)) },
                    )
                }

                AddRoundScreen.SUMMARY -> NavEntry(key) {
                    val round = requireNotNull(state.round)

                    SummaryScreen(
                        game = game,
                        round = round,
                    )
                }
            }
        },
        transitionSpec = slideTransition(1),
        popTransitionSpec = slideTransition(-1)
    )
}

@Composable
private fun ButtonRow(screen: AddRoundScreen, onBack: () -> Unit, onCancel: () -> Unit, onSave: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Button(onClick = {
            when (screen) {
                AddRoundScreen.SELECT_ROUND_TYPE -> onCancel()
                AddRoundScreen.SELECT_PLAYERS,
                AddRoundScreen.SELECT_SLAMS,
                AddRoundScreen.SELECT_BID_ACHIEVED,
                AddRoundScreen.SUMMARY -> onBack()
            }
        }) {
            Text("Back")
        }

        Button(
            onClick = onSave,
            enabled = screen == AddRoundScreen.SUMMARY
        ) {
            Text("Save")
        }
    }
}

internal sealed interface AddRoundNextStep {
    data class Dispatch(val action: AddRoundAction) : AddRoundNextStep
    data class Save(val round: Round) : AddRoundNextStep
}

internal data class NextButton(val enabled: Boolean, val step: AddRoundNextStep)
