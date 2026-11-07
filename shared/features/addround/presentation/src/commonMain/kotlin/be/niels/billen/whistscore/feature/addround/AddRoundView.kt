package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.ui.ButtonVariant
import be.niels.billen.whistscore.feature.ui.Style
import be.niels.billen.whistscore.feature.ui.TextButton
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
        effects = viewModel.effects,
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
    initialState: AddRoundState = AddRoundState(),
) {
    val backStack = remember { mutableStateListOf(initialState.screen) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium),
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
                modifier =
                    Modifier.fillMaxWidth().padding(
                        start = Style.Dimensions.paddingSmall,
                        end = Style.Dimensions.paddingSmall,
                        bottom = Style.Dimensions.paddingSmall,
                    ),
            )
        }
    }
}

private val screenModifier =
    Modifier.padding(
        start = Style.Dimensions.paddingSmall,
        end = Style.Dimensions.paddingSmall,
        top = Style.Dimensions.paddingSmall,
    )

@Composable
private fun AddRoundScreens(
    modifier: Modifier = Modifier,
    onAction: (AddRoundAction) -> Unit,
    effects: SharedFlow<AddRoundEffect>,
    backStack: SnapshotStateList<AddRoundScreen>,
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
                AddRoundScreen.SELECT_ROUND_TYPE -> {
                    NavEntry(key) {
                        RoundTypeInputScreen(
                            modifier = screenModifier,
                            onRoundTypeSelected = { onAction(AddRoundAction.SetRoundType(it)) },
                        )
                    }
                }

                AddRoundScreen.SELECT_PLAYERS -> {
                    NavEntry(key) {
                        PlayerSelectionScreen(
                            modifier = screenModifier,
                            onSelectionChange = { id, selected ->
                                onAction(
                                    AddRoundAction.SetPlayerSelected(
                                        playerId = id,
                                        selected = selected,
                                    ),
                                )
                            },
                        )
                    }
                }

                AddRoundScreen.SELECT_SLAMS -> {
                    NavEntry(key) {
                        SlamInputScreen(
                            modifier = screenModifier,
                            onSlamsSelected = { onAction(AddRoundAction.SetSlams(it)) },
                        )
                    }
                }

                AddRoundScreen.SELECT_BID_ACHIEVED -> {
                    NavEntry(key) {
                        BidAchievedInput(
                            modifier = screenModifier,
                            onBidAchievedSelected = { onAction(AddRoundAction.SetBidAchieved(it)) },
                        )
                    }
                }

                AddRoundScreen.SUMMARY -> {
                    NavEntry(key) {
                        SummaryScreen(modifier = screenModifier)
                    }
                }
            }
        },
        transitionSpec = slideTransition(1),
        popTransitionSpec = slideTransition(-1),
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
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .requiredWidthIn(max = 800.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TextButton(
            text = "Back",
            variant = ButtonVariant.Secondary,
            modifier = Modifier.widthIn(min = 96.dp),
            onClick = {
                when (screen) {
                    AddRoundScreen.SELECT_ROUND_TYPE -> onCancel()

                    AddRoundScreen.SELECT_PLAYERS,
                    AddRoundScreen.SELECT_SLAMS,
                    AddRoundScreen.SELECT_BID_ACHIEVED,
                    AddRoundScreen.SUMMARY,
                    -> onBack()
                }
            },
        )

        when (screen) {
            AddRoundScreen.SUMMARY -> SaveButton(onClick = onSave)

            AddRoundScreen.SELECT_PLAYERS -> SelectPlayersNextButton(state = state, onAction = onAction)

            AddRoundScreen.SELECT_ROUND_TYPE,
            AddRoundScreen.SELECT_SLAMS,
            AddRoundScreen.SELECT_BID_ACHIEVED,
            -> Unit
        }
    }
}

@Composable
private fun SelectPlayersNextButton(
    state: AddRoundState,
    onAction: (AddRoundAction) -> Unit,
) {
    when (state.roundType?.singlePlayer == true) {
        true -> {}

        false -> {
            NextButton(
                enabled = state.players.isNotEmpty(),
                onClick = { onAction(AddRoundAction.ConfirmPlayers) },
            )
        }
    }
}

@Composable
private fun NextButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    TextButton(
        text = "Next",
        enabled = enabled,
        onClick = onClick,
        modifier = Modifier.widthIn(min = 96.dp),
        variant = ButtonVariant.Secondary,
    )
}

@Composable
private fun SaveButton(onClick: () -> Unit) {
    TextButton(
        text = "Save",
        onClick = onClick,
        modifier = Modifier.widthIn(min = 96.dp),
        variant = ButtonVariant.Primary,
    )
}
