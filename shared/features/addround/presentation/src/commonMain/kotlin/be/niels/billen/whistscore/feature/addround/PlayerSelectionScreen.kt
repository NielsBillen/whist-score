package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import be.niels.billen.whistscore.feature.core.*
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlayerSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
    onSelectionChange: (PlayerId, Boolean) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val game by viewModel.game.collectAsState()

    PlayerSelectionScreen(
        roundType = requireNotNull(state.roundType),
        players = game.players,
        selection = state.players,
        modifier = modifier,
        onSelectionChange = onSelectionChange,
    )
}

@Composable
internal fun PlayerSelectionScreen(
    roundType: RoundType,
    players: Players,
    selection: Set<PlayerId>,
    modifier: Modifier = Modifier,
    onSelectionChange: (PlayerId, Boolean) -> Unit,
) {
    AddRoundPanel(
        title = roundType.displayName,
        description = roundType.description,
        modifier = modifier
    ) {
        if (roundType.singlePlayer) {
            SinglePlayerChoice(
                players = players,
                selectedPlayers = selection,
                onSelection = { onSelectionChange(it, true) })
        } else {
            MultiPlayerChoice(
                players = players,
                selectedPlayers = selection,
                onSelectionChange = onSelectionChange
            )
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
            text = player.name,
            modifier = Modifier.padding(Style.Dimensions.paddingLarge),
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
