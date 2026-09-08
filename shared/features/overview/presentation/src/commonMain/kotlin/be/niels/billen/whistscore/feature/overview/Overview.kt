package be.niels.billen.whistscore.feature.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.Style
import be.niels.billen.whistscore.feature.core.Icons
import be.niels.billen.whistscore.feature.core.icons.Pencil
import be.niels.billen.whistscore.feature.players.PlayersView
import be.niels.billen.whistscore.feature.rounds.RoundsView
import org.koin.compose.koinInject

@Composable
fun Overview(
    viewModel: OverviewViewModel = koinInject(),
    onResetGame: () -> Unit,
    onAddRound: () -> Unit,
    onEditPlayers: () -> Unit,
) {
    val canStartNewGame by viewModel.canStartNewGame.collectAsState()

    Overview(canStartNewGame = canStartNewGame, onResetGame = onResetGame, onAddRound = onAddRound, onEditPlayers = onEditPlayers)
}

@Composable
fun Overview(
    canStartNewGame: Boolean,
    onResetGame: () -> Unit,
    onAddRound: () -> Unit,
    onEditPlayers: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(Style.Dimensions.paddingLarge).widthIn(max = 800.dp),
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Whist score", style = MaterialTheme.typography.titleLarge)

            IconButton(onClick = onEditPlayers) {
                Icon(imageVector = Icons.Pencil, contentDescription = "Edit players")
            }
        }

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
private fun ButtonRow(
    canStartNewGame: Boolean,
    onResetGame: () -> Unit,
    onAddRound: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(Style.Dimensions.paddingSmall),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        OutlinedButton(
            enabled = canStartNewGame,
            onClick = onResetGame
        ) {
            Text(text = "New game")
        }

        Button(onClick = onAddRound) {
            Text(text = "Add Round")
        }
    }
}
