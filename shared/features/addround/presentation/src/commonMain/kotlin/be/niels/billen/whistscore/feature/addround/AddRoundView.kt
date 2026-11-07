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
    onCancel: () -> Unit,
    onSave: (Round) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
) {
    AddRoundView(
        onCancel = onCancel,
        onSave = onSave,
        onAction = viewModel::onAction,
        viewModel = viewModel,
        modifier = modifier,
    )
}

@Composable
internal fun AddRoundView(
    onCancel: () -> Unit,
    onSave: (Round) -> Unit,
    onAction: (AddRoundAction) -> Unit,
    viewModel: AddRoundViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.state.collectAsState()
    val backStack = remember { mutableStateListOf(state.screen) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium),
    ) {
        AddRoundScreens(
            onAction = onAction,
            effects = viewModel.effects,
            backStack = backStack,
            modifier = Modifier.weight(1f),
        )

        backStack.lastOrNull()?.let { screen ->
            ButtonRow(
                screen = screen,
                onBack = {
                    when (screen) {
                        AddRoundScreen.SELECT_ROUND_TYPE -> onCancel()
                        else -> if (backStack.isNotEmpty()) backStack.removeLast()
                    }
                },
                onSave = { state.round?.let(onSave) },
                state = state,
                onAction = onAction,
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
    onAction: (AddRoundAction) -> Unit,
    effects: SharedFlow<AddRoundEffect>,
    backStack: SnapshotStateList<AddRoundScreen>,
    modifier: Modifier = Modifier,
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
        entryProvider = { key -> screenEntry(key, onAction) },
        transitionSpec = slideTransition(1),
        popTransitionSpec = slideTransition(-1),
    )
}

private fun screenEntry(
    key: AddRoundScreen,
    onAction: (AddRoundAction) -> Unit,
): NavEntry<AddRoundScreen> =
    NavEntry(key) {
        when (key) {
            AddRoundScreen.SELECT_ROUND_TYPE -> {
                RoundTypeInputScreen(
                    modifier = screenModifier,
                    onRoundTypeChange = { type -> onAction(AddRoundAction.SetRoundType(type)) },
                )
            }

            AddRoundScreen.SELECT_PLAYERS -> {
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

            AddRoundScreen.SELECT_SLAMS -> {
                SlamInputScreen(
                    modifier = screenModifier,
                    onSlamsChange = { slams -> onAction(AddRoundAction.SetSlams(slams)) },
                )
            }

            AddRoundScreen.SELECT_BID_ACHIEVED -> {
                BidAchievedInput(
                    modifier = screenModifier,
                    onBidAchievedChange = { achieved -> onAction(AddRoundAction.SetBidAchieved(achieved)) },
                )
            }

            AddRoundScreen.SUMMARY -> {
                SummaryScreen(modifier = screenModifier)
            }
        }
    }

@Composable
private fun ButtonRow(
    screen: AddRoundScreen,
    onBack: () -> Unit,
    onSave: () -> Unit,
    state: AddRoundState,
    onAction: (AddRoundAction) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = Style.Dimensions.paddingSmall,
                    end = Style.Dimensions.paddingSmall,
                    bottom = Style.Dimensions.paddingSmall,
                ).fillMaxWidth()
                .requiredWidthIn(max = 800.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TextButton(
            text = "Back",
            variant = ButtonVariant.Secondary,
            modifier = Modifier.widthIn(min = 96.dp),
            onClick = onBack,
        )

        when (screen) {
            AddRoundScreen.SUMMARY -> {
                SaveButton(onClick = onSave)
            }

            AddRoundScreen.SELECT_PLAYERS -> {
                if (state.roundType?.singlePlayer != true) {
                    NextButton(
                        enabled = state.players.isNotEmpty(),
                        onClick = { onAction(AddRoundAction.ConfirmPlayers) },
                    )
                }
            }

            AddRoundScreen.SELECT_ROUND_TYPE,
            AddRoundScreen.SELECT_SLAMS,
            AddRoundScreen.SELECT_BID_ACHIEVED,
            -> {}
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
