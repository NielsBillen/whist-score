package be.niels.billen.whistscore.feature.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.ui.ConfirmDialog
import be.niels.billen.whistscore.feature.ui.Style
import be.niels.billen.whistscore.feature.players.PlayersView
import be.niels.billen.whistscore.feature.rounds.RoundsView
import be.niels.billen.whistscore.feature.ui.ButtonVariant
import be.niels.billen.whistscore.feature.ui.TextButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Overview(
    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = koinViewModel(),
    onResetGame: () -> Unit,
    onAddRound: () -> Unit,
) {
    val canStartNewGame by viewModel.canStartNewGame.collectAsState()
    var showNewGameDialog by remember { mutableStateOf(false) }

    Overview(
        canStartNewGame = canStartNewGame,
        onNewGameRequested = { showNewGameDialog = true },
        onAddRound = onAddRound,
        modifier = modifier,
    )

    if (showNewGameDialog) {
        NewGameDialog(
            onConfirm = {
                showNewGameDialog = false
                onResetGame()
            },
            onDismiss = { showNewGameDialog = false }
        )
    }
}

@Composable
internal fun Overview(
    canStartNewGame: Boolean,
    onNewGameRequested: () -> Unit,
    onAddRound: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(Style.Dimensions.paddingLarge)
            .wrapContentSize()
            .widthIn(max = 800.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)
    ) {
        PlayersView()

        RoundsView(Modifier.weight(1f))

        ButtonRow(
            canStartNewGame = canStartNewGame,
            onNewGameRequested = onNewGameRequested,
            onAddRound = onAddRound,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NewGameDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ConfirmDialog(
        title = "New game",
        body = "Starting a new game will discard all recorded rounds and reset the scoreboard.",
        confirmText = "Start new game",
        dismissText = "Cancel",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        modifier = modifier
    )
}

@Composable
private fun ButtonRow(
    canStartNewGame: Boolean,
    onNewGameRequested: () -> Unit,
    onAddRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonModifier = Modifier.widthIn(min = 96.dp)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(
            variant = ButtonVariant.Destructive,
            enabled = canStartNewGame,
            onClick = onNewGameRequested,
            modifier = buttonModifier,
            text = "New game"
        )

        TextButton(
            onClick = onAddRound,
            modifier = buttonModifier,
            text = "Add round"
        )
    }
}
