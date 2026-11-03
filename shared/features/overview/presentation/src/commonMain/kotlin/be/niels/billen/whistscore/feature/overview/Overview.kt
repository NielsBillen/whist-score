package be.niels.billen.whistscore.feature.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.ui.Style
import be.niels.billen.whistscore.feature.core.icons.Pencil
import be.niels.billen.whistscore.feature.players.PlayersView
import be.niels.billen.whistscore.feature.rounds.RoundsView
import be.niels.billen.whistscore.ui.Button
import be.niels.billen.whistscore.ui.ButtonVariant
import be.niels.billen.whistscore.ui.TextButton
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Overview(
    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = koinViewModel(),
    onResetGame: () -> Unit,
    onAddRound: () -> Unit,
    onEditPlayers: () -> Unit,
) {
    val canStartNewGame by viewModel.canStartNewGame.collectAsState()

    Overview(
        canStartNewGame = canStartNewGame,
        onResetGame = onResetGame,
        onAddRound = onAddRound,
        onEditPlayers = onEditPlayers,
        modifier = modifier,
    )
}

@Composable
internal fun Overview(
    canStartNewGame: Boolean,
    onResetGame: () -> Unit,
    onAddRound: () -> Unit,
    onEditPlayers: () -> Unit,
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
        TitleRow(onEditPlayers = onEditPlayers)

        PlayersView()

        RoundsView(Modifier.weight(1f))

        ButtonRow(
            canStartNewGame = canStartNewGame,
            onResetGame = onResetGame,
            onAddRound = onAddRound,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun TitleRow(onEditPlayers: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Whist score", style = MaterialTheme.typography.titleLarge)

        IconButton(onClick = onEditPlayers) {
            Icon(imageVector = Pencil, contentDescription = "Edit players")
        }
    }
}

@Composable
private fun ButtonRow(
    canStartNewGame: Boolean,
    onResetGame: () -> Unit,
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
            onClick = onResetGame,
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
