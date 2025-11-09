package be.niels.billen.presentation.screens.addround.playerselection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Players
import be.niels.billen.domain.RoundType
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.components.Selectable
import be.niels.billen.presentation.screens.addround.AddRoundAction
import be.niels.billen.presentation.screens.addround.AddRoundPanel

@Composable
fun PlayerSelectionScreen(
    modifier: Modifier = Modifier,
    roundType: RoundType,
    players: Players,
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
        nextEnabled = { selection.size in roundType.playerCountRange },
        modifier = modifier
    ) {
        if (roundType.singlePlayer) {
            SinglePlayerChoice(
                players = players,
                selectedPlayers = selection,
                onSelection = { selection = setOf(it) })
        } else {
            MultiPlayerChoice(
                players = players,
                selectedPlayers = selection,
                onSelectionChange = { id, selected ->
                    selection = if (selected) selection + id
                    else selection - id
                })
        }
    }
}

@Composable
private fun MultiPlayerChoice(
    players: Players,
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
    players: Players,
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
    get() = when (this) {
        RoundType.Regular -> "Select player(s)"
        RoundType.Abandonce, RoundType.SoloSlim, RoundType.AbandonceInTrump, RoundType.Misere, RoundType.OpenMisere -> "Select player"
        RoundType.Treble -> "Select players"
    }


private val RoundType.description: String
    get() = when (this) {
        RoundType.Regular -> "Choose between one and two players that played this round"
        RoundType.Abandonce, RoundType.SoloSlim, RoundType.AbandonceInTrump, RoundType.Misere, RoundType.OpenMisere -> "Choose the player that played this round"
        RoundType.Treble -> "Choose the two players that played this round"
    }