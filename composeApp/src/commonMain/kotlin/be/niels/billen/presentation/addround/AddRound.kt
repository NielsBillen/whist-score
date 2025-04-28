package be.niels.billen.presentation.addround

import androidx.compose.animation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.app.AppAction
import be.niels.billen.presentation.app.AppScreen
import org.koin.compose.koinInject


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddRound(modifier: Modifier = Modifier, onAction: (AppAction) -> Unit) {
    val viewModel: AddRoundViewModel = koinInject()
    val state = viewModel.state.collectAsState().value
    val players = viewModel.players.collectAsState()

    AnimatedContent(targetState = state, modifier = modifier.padding(Style.Dimensions.paddingLarge), transitionSpec = {
        slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth }) + fadeIn() togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut()
    }, contentAlignment = Alignment.Center, content = { state ->
        if (state.roundType == null) {
            RoundTypeInputScreen(
                selectedRoundType = state.roundType, onAction = { viewModel.onAction(it) }, onCancel = {
                    onAction(AppAction.Navigate(AppScreen.OVERVIEW))
                })
        } else if (state.players.isEmpty()) {
            PlayerSelectionScreen(
                roundType = state.roundType,
                players = players.value,
                selectedPlayers = state.players,
                onAction = { viewModel.onAction(it) })
        } else if (state.slams == null) {
            SlamInputScreen(
                selectedSlams = state.slams, onAction = { viewModel.onAction(it) })
        }
    })
}

