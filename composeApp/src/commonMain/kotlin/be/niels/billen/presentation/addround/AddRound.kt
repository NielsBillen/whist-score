package be.niels.billen.presentation.addround

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.addround.playerselection.PlayerSelectionScreen
import be.niels.billen.presentation.addround.roundinput.RoundTypeInputScreen
import be.niels.billen.presentation.addround.slaminput.SlamInputScreen
import be.niels.billen.presentation.addround.summary.SummaryScreen
import be.niels.billen.presentation.app.AppAction
import be.niels.billen.presentation.app.AppScreen
import org.koin.compose.koinInject


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddRound(modifier: Modifier = Modifier, onAction: (AppAction) -> Unit) {
    val viewModel: AddRoundViewModel = koinInject()
    val state = viewModel.state.collectAsState().value
    val players = viewModel.players.collectAsState()

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
                    onCancel = {
                        onAction(AppAction.Navigate(AppScreen.OVERVIEW))
                    })

                AddRoundScreen.SELECT_PLAYERS -> state.roundType.let {
                    requireNotNull(it)
                    PlayerSelectionScreen(
                        roundType = it,
                        players = players.value,
                        initialSelection = state.players,
                        onCancel = { viewModel.onAction(AddRoundAction.Navigate(AddRoundScreen.SELECT_ROUND_TYPE)) },
                        onAction = { viewModel.onAction(it) })
                }

                AddRoundScreen.SELECT_SLAMS -> SlamInputScreen(
                    initialSlams = state.slams,
                    onCancel = { viewModel.onAction(AddRoundAction.Navigate(AddRoundScreen.SELECT_PLAYERS)) },
                    onAction = { viewModel.onAction(it) })

                AddRoundScreen.SUMMARY -> state.round.let {
                    requireNotNull(it)

                    SummaryScreen(
                        round = it,
                        onBack = { viewModel.onAction(AddRoundAction.Navigate(AddRoundScreen.SELECT_SLAMS)) },
                        onNext = { onAction(AppAction.AddRound(it)) }
                    )
                }
            }
        })
}

