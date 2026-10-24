package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.Style
import kotlinx.coroutines.flow.SharedFlow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AddRoundView(
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
    onCancel: () -> Unit,
    onSave: (Round) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    AddRoundView(
        modifier = modifier,
        onCancel = onCancel,
        onSave = onSave,
        state = state,
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
) {
    val backStack = remember { mutableStateListOf(AddRoundScreen.SELECT_ROUND_TYPE) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)
    ) {
        AddRoundScreens(
            onAction = onAction,
            effects = effects,
            backStack = backStack,
            modifier = Modifier.weight(1f),
        )

        backStack.lastOrNull()?.let { screen ->
            ButtonRow(
                state = state,
                screen = screen,
                onBack = {
                    if (!backStack.isEmpty()) {
                        backStack.removeLast()
                    }
                },
                onSave = { state.round?.let(onSave) },
                onAction = onAction,
                onCancel = onCancel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private val screenModifier = Modifier.padding(
    start = Style.Dimensions.paddingLarge,
    end = Style.Dimensions.paddingLarge,
    top = Style.Dimensions.paddingLarge
)


@Composable
private fun AddRoundScreens(
    modifier: Modifier = Modifier,
    onAction: (AddRoundAction) -> Unit,
    effects: SharedFlow<AddRoundEffect>,
    backStack: SnapshotStateList<AddRoundScreen>
) {
    LaunchedEffect(effects) {
        effects.collect { effect ->
            when (effect) {
                is AddRoundEffect.NextScreen -> backStack.add(effect.screen)
                AddRoundEffect.PreviousScreen -> if (!backStack.isEmpty()) backStack.removeLastOrNull()
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
                        modifier = screenModifier,
                        onRoundTypeSelected = { onAction(AddRoundAction.SetRoundType(it)) }
                    )
                }

                AddRoundScreen.SELECT_PLAYERS -> NavEntry(key) {
                    PlayerSelectionScreen(
                        modifier = screenModifier,
                        onSelectionChange = { id, selected ->
                            onAction(
                                AddRoundAction.SetPlayerSelected(
                                    playerId = id,
                                    selected = selected
                                )
                            )
                        },
                    )
                }

                AddRoundScreen.SELECT_SLAMS -> NavEntry(key) {
                    SlamInputScreen(
                        modifier = screenModifier,
                        onSlamsSelected = { onAction(AddRoundAction.SetSlams(it)) },
                    )
                }

                AddRoundScreen.SELECT_BID_ACHIEVED -> NavEntry(key) {
                    BidAchievedInput(
                        modifier = screenModifier,
                        onBidAchievedSelected = { onAction(AddRoundAction.SetBidAchieved(it)) },
                    )
                }

                AddRoundScreen.SUMMARY -> NavEntry(key) {
                    SummaryScreen(modifier = screenModifier)
                }
            }
        },
        transitionSpec = slideTransition(1),
        popTransitionSpec = slideTransition(-1)
    )
}

@Composable
private fun ButtonRow(
    screen: AddRoundScreen,
    state: AddRoundState,
    onBack: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onAction: (AddRoundAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(
            start = Style.Dimensions.paddingLarge,
            bottom = Style.Dimensions.paddingLarge,
            end = Style.Dimensions.paddingLarge
        ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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


        when (screen) {
            AddRoundScreen.SUMMARY -> SaveButton(onClick = onSave)
            AddRoundScreen.SELECT_ROUND_TYPE -> Unit
            AddRoundScreen.SELECT_PLAYERS ->
                when (state.roundType?.singlePlayer == true) {
                    true -> Unit
                    false -> NextButton(
                        enabled = state.players.isNotEmpty(),
                        onClick = { onAction(AddRoundAction.ConfirmPlayers) }
                    )
                }


            AddRoundScreen.SELECT_SLAMS -> Unit
            AddRoundScreen.SELECT_BID_ACHIEVED -> Unit
        }
    }
}

@Composable
private fun NextButton(enabled: Boolean, onClick: () -> Unit) {
    Button(enabled = enabled, onClick = onClick) {
        Text("Next")
    }
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text("Save")
    }
}

internal sealed interface AddRoundNextStep {
    data class Dispatch(val action: AddRoundAction) : AddRoundNextStep
    data class Save(val round: Round) : AddRoundNextStep
}

internal data class NextButton(val enabled: Boolean, val step: AddRoundNextStep)
