package be.niels.billen.whistscore.feature.addround

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.Style
import org.koin.compose.koinInject
import kotlin.math.round


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddRoundView(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onSave: (Round) -> Unit
) {
    val viewModel: AddRoundViewModel = koinInject()
    val state by viewModel.state.collectAsState()

    AnimatedContent(
        targetState = state.screen,
        modifier = modifier.padding(Style.Dimensions.paddingLarge),
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }) + fadeIn() togetherWith slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut()
        },
        contentAlignment = Alignment.Center,
        content = { screen ->
            when (screen) {
                AddRoundScreen.SELECT_ROUND_TYPE -> RoundTypeInputScreen(
                    initialRoundType = state.roundType,
                    onAction = { viewModel.onAction(it) },
                    onCancel = onCancel
                )

                AddRoundScreen.SELECT_PLAYERS -> state.roundType.let { roundType ->
                    requireNotNull(roundType)
                    val game by viewModel.game.collectAsState()


                    PlayerSelectionScreen(
                        roundType = roundType,
                        players = game.players,
                        initialSelection = state.players,
                        onCancel = { viewModel.onAction(AddRoundAction.PreviousScreen) },
                        onAction = { viewModel.onAction(it) })
                }

                AddRoundScreen.SELECT_SLAMS -> SlamInputScreen(
                    initialSlams = state.slams,
                    onCancel = { viewModel.onAction(AddRoundAction.PreviousScreen) },
                    onAction = { viewModel.onAction(it) })

                AddRoundScreen.SELECT_BID -> BidInputScreen(
                    initialBid = state.bid,
                    onCancel = { viewModel.onAction(AddRoundAction.PreviousScreen) },
                    onAction = { viewModel.onAction(it) })

                AddRoundScreen.SELECT_BID_ACHIEVED -> BidAchievedInput(
                    initialBidAchieved = state.playerWon,
                    onCancel = { viewModel.onAction(AddRoundAction.PreviousScreen) },
                    onAction = { viewModel.onAction(it) })

                AddRoundScreen.SUMMARY -> state.round.let { round ->
                    requireNotNull(round)
                    val game by viewModel.game.collectAsState()

                    SummaryScreen(
                        game = game,
                        round = round,
                        onBack = { viewModel.onAction(AddRoundAction.PreviousScreen) },
                        onNext = { onSave(round) },
                    )
                }
            }
        })
}

