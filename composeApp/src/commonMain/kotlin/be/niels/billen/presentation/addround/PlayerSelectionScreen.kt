package be.niels.billen.presentation.addround

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.RoundType

@Composable
fun PlayerSelectionScreen(
    modifier: Modifier = Modifier,
    roundType: RoundType,
    players: Map<PlayerId, Player>,
    selectedPlayers: Set<PlayerId>,
    onAction: (AddRoundAction) -> Unit
) {
    when (roundType) {
        RoundType.Regular -> MultiPlayerChoice(modifier, players, selectedPlayers, onAction)
        RoundType.Abandonce -> SinglePlayerChoice(modifier, players, selectedPlayers, onAction)
    }
}

@Composable
private fun MultiPlayerChoice(
    modifier: Modifier = Modifier,
    players: Map<PlayerId, Player>,
    selectedPlayers: Set<PlayerId>,
    onAction: (AddRoundAction) -> Unit,
) {
    Column(modifier) {
        Text("Select player(s)")

        Column {
            players.forEach { (id, player) ->
                val checked = selectedPlayers.contains(id)
                val enabled = checked || selectedPlayers.size < 2
                PlayerCheckBox(id, player, selectedPlayers.contains(id), enabled, onAction)
            }
        }
    }
}


@Composable
private fun PlayerCheckBox(
    id: PlayerId,
    player: Player,
    selected: Boolean,
    enabled: Boolean,
    onAction: (AddRoundAction) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = selected,
            enabled = enabled,
            onCheckedChange = { onAction(AddRoundAction.SelectPlayer(id, !selected)) })
        Text(player.name)
    }
}

@Composable
private fun SinglePlayerChoice(
    modifier: Modifier = Modifier,
    players: Map<PlayerId, Player>,
    selectedPlayers: Set<PlayerId>,
    onAction: (AddRoundAction) -> Unit
) {
    Column {
        Text("Select player:")

        Column(modifier) {
            players.forEach { (id, player) ->
                PlayerRadioButton(id, player, selectedPlayers.contains(id), onAction)
            }
        }
    }
}

@Composable
private fun PlayerRadioButton(id: PlayerId, player: Player, selected: Boolean, onAction: (AddRoundAction) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = { onAction(AddRoundAction.SetPlayers(setOf(id))) })
        Text(player.name)
    }
}
