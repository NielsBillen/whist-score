package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.RoundType
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
    var selectedRoundType by remember { mutableStateOf(state.roundType) }
    var selectedPlayers by remember { mutableStateOf(state.players) }
    var selectedSlams by remember { mutableStateOf(state.slams) }
    var selectedBidAchieved by remember { mutableStateOf(state.playerWon) }

    LaunchedEffect(viewModel.effects) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AddRoundEffect.NextScreen -> backStack.add(screenToRoute(state.screen))
                AddRoundEffect.PreviousScreen -> backStack.removeLastOrNull()
            }
        }
    }

    val nextButton = nextButtonFor(
        current = backStack.last(),
        state = state,
        selectedRoundType = selectedRoundType,
        selectedPlayers = selectedPlayers,
        selectedSlams = selectedSlams,
        selectedBidAchieved = selectedBidAchieved,
    )

    Column(modifier = modifier) {
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.weight(1f).padding(Style.Dimensions.paddingLarge),
            contentAlignment = Alignment.Center,
            entryProvider = { key ->
                when (key) {
                    is AddRoundNavigation.SelectRoundTypeRoute -> NavEntry(key) {
                        RoundTypeInputScreen(
                            selectedRoundType = selectedRoundType,
                            onRoundTypeSelected = { selectedRoundType = it },
                        )
                    }

                    is AddRoundNavigation.SelectPlayersRoute -> NavEntry(key) {
                        val roundType = requireNotNull(state.roundType)
                        PlayerSelectionScreen(
                            roundType = roundType,
                            players = game.players,
                            selection = selectedPlayers,
                            onSelectionChange = { id, selected ->
                                selectedPlayers = if (selected) {
                                    if (roundType.singlePlayer) setOf(id) else selectedPlayers + id
                                } else {
                                    selectedPlayers - id
                                }
                            },
                        )
                    }

                    is AddRoundNavigation.SelectSlamsRoute -> NavEntry(key) {
                        SlamInputScreen(
                            selectedSlams = selectedSlams,
                            onSlamsSelected = { selectedSlams = it },
                        )
                    }

                    is AddRoundNavigation.SelectBidAchievedRoute -> NavEntry(key) {
                        BidAchievedInput(
                            selectedBidAchieved = selectedBidAchieved,
                            onBidAchievedSelected = { selectedBidAchieved = it },
                        )
                    }

                    is AddRoundNavigation.SummaryRoute -> NavEntry(key) {
                        SummaryScreen(
                            game = game,
                            round = requireNotNull(state.round),
                        )
                    }
                }
            },
            transitionSpec = pushSlideTransition(),
            popTransitionSpec = popSlideTransition(),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Style.Dimensions.paddingLarge)
                .padding(bottom = Style.Dimensions.paddingLarge),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            OutlinedButton(onClick = {
                if (backStack.size > 1) viewModel.onAction(AddRoundAction.PreviousScreen)
                else onCancel()
            }) {
                Text("Back")
            }

            Button(
                onClick = {
                    when (val step = nextButton.step) {
                        is AddRoundNextStep.Dispatch -> viewModel.onAction(step.action)
                        is AddRoundNextStep.Save -> onSave(step.round)
                    }
                },
                enabled = nextButton.enabled,
            ) {
                Text("Next")
            }
        }
    }
}

internal sealed interface AddRoundNextStep {
    data class Dispatch(val action: AddRoundAction) : AddRoundNextStep
    data class Save(val round: Round) : AddRoundNextStep
}

internal data class NextButton(val enabled: Boolean, val step: AddRoundNextStep)

internal fun nextButtonFor(
    current: AddRoundNavigation,
    state: AddRoundState,
    selectedRoundType: RoundType?,
    selectedPlayers: Set<PlayerId>,
    selectedSlams: Int?,
    selectedBidAchieved: Boolean?,
): NextButton = when (current) {
    is AddRoundNavigation.SelectRoundTypeRoute -> NextButton(
        enabled = selectedRoundType != null,
        step = AddRoundNextStep.Dispatch(AddRoundAction.SetRoundType(selectedRoundType ?: RoundType.Pass)),
    )

    is AddRoundNavigation.SelectPlayersRoute -> {
        val playerCountRange = state.roundType?.playerCountRange
        NextButton(
            enabled = playerCountRange != null && selectedPlayers.size in playerCountRange,
            step = AddRoundNextStep.Dispatch(AddRoundAction.SetPlayers(selectedPlayers)),
        )
    }

    is AddRoundNavigation.SelectSlamsRoute -> NextButton(
        enabled = selectedSlams != null,
        step = AddRoundNextStep.Dispatch(AddRoundAction.SetSlams(selectedSlams ?: 0)),
    )

    is AddRoundNavigation.SelectBidAchievedRoute -> NextButton(
        enabled = selectedBidAchieved != null,
        step = AddRoundNextStep.Dispatch(AddRoundAction.SetBidAchieved(selectedBidAchieved ?: false)),
    )

    is AddRoundNavigation.SummaryRoute -> NextButton(
        enabled = true,
        step = AddRoundNextStep.Save(requireNotNull(state.round)),
    )
}

private fun screenToRoute(screen: AddRoundScreen): AddRoundNavigation = when (screen) {
    AddRoundScreen.SELECT_ROUND_TYPE -> AddRoundNavigation.SelectRoundTypeRoute
    AddRoundScreen.SELECT_PLAYERS -> AddRoundNavigation.SelectPlayersRoute
    AddRoundScreen.SELECT_SLAMS -> AddRoundNavigation.SelectSlamsRoute
    AddRoundScreen.SELECT_BID_ACHIEVED -> AddRoundNavigation.SelectBidAchievedRoute
    AddRoundScreen.SUMMARY -> AddRoundNavigation.SummaryRoute
}
