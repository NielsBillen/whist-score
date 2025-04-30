package be.niels.billen.presentation.addround.playerselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.RoundType
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.addround.AddRoundAction
import be.niels.billen.presentation.addround.AddRoundPanel
import be.niels.billen.presentation.components.Selectable

@Composable
fun PlayerSelectionScreen(
    modifier: Modifier = Modifier,
    roundType: RoundType,
    players: Map<PlayerId, Player>,
    initialSelection: Set<PlayerId>,
    onAction: (AddRoundAction) -> Unit,
    onCancel: () -> Unit,
) {
    var selection by remember { mutableStateOf(initialSelection) }

    AddRoundPanel(
        title = roundType.title,
        description = roundType.description,
        onBack = onCancel,
        onNext = { onAction(AddRoundAction.SetPlayers(selection)) },
        nextEnabled = { selection.isNotEmpty() },
        modifier = modifier
    ) {
        when (roundType) {
            RoundType.Regular, RoundType.Treble -> MultiPlayerChoice(
                players,
                selection,
                onSelectionChange = { id, selected ->
                    selection = if (selected) selection + id
                    else selection - id
                })


            RoundType.Abandonce, RoundType.Misere -> SinglePlayerChoice(
                players, selection, onSelection = { selection = setOf(it) })

        }
    }
}

@Composable
private fun MultiPlayerChoice(
    players: Map<PlayerId, Player>,
    selectedPlayers: Set<PlayerId>,
    modifier: Modifier = Modifier,
    onSelectionChange: (PlayerId, Boolean) -> Unit,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingLarge)
    ) {
        players.forEach { (id, player) ->
            val checked = selectedPlayers.contains(id)
            val enabled = checked || selectedPlayers.size < 2
            PlayerCheckBox(id, player, selectedPlayers.contains(id), enabled, onSelectionChange)
        }
    }

}


@Composable
private fun PlayerCheckBox(
    id: PlayerId,
    player: Player,
    selected: Boolean,
    enabled: Boolean,
    onSelectionChange: (PlayerId, Boolean) -> Unit,
) {
    Selectable(
        selected = selected,
        enabled = enabled,
        onClick = { onSelectionChange(id, !selected) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            player.name, Modifier.padding(Style.Dimensions.paddingLarge),
        )
    }
}

@Composable
private fun SinglePlayerChoice(
    players: Map<PlayerId, Player>,
    selectedPlayers: Set<PlayerId>,
    modifier: Modifier = Modifier,
    onSelection: (PlayerId) -> Unit,
) {
    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingLarge)
    ) {
        players.forEach { (id, player) ->
            PlayerRadioButton(id, player, selectedPlayers.contains(id), onSelection)
        }
    }

}

@Composable
private fun PlayerRadioButton(
    id: PlayerId,
    player: Player,
    selected: Boolean,
    onSelection: (PlayerId) -> Unit
) {
    Selectable(
        selected = selected,
        onClick = { onSelection(id) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            player.name, Modifier.padding(Style.Dimensions.paddingLarge),
        )
    }
}

private val RoundType.title: String
    get() = if (singlePlayer) "Select player:" else "Select player(s)"


private val RoundType.description: String
    get() = if (singlePlayer) "Choose the player that played this round" else "Choose between the one player or two players that played this round"
