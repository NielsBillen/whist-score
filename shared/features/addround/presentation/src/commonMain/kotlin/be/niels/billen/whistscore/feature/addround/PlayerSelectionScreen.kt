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
import be.niels.billen.whistscore.feature.game.Game
import kotlinx.collections.immutable.PersistentSet
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PlayerSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
    onSelectionChange: (PlayerId, Boolean) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val game by viewModel.game.collectAsState()
    val players by viewModel.players.collectAsState()

    PlayerSelectionScreen(
        roundType = requireNotNull(state.roundType),
        game = game,
        players = players,
        selection = state.players,
        modifier = modifier,
        onSelectionChange = onSelectionChange,
    )
}

@Composable
internal fun PlayerSelectionScreen(
    roundType: RoundType,
    game: Game,
    players: Players,
    selection: PersistentSet<PlayerId>,
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
                game = game,
                players = players,
                selectedPlayers = selection,
                onSelection = { onSelectionChange(it, true) })
        } else {
            MultiPlayerChoice(
                game = game,
                players = players,
                selectedPlayers = selection,
                onSelectionChange = onSelectionChange
            )
        }
    }
}


@Composable
private fun MultiPlayerChoice(
    game: Game,
    players: Players,
    selectedPlayers: PersistentSet<PlayerId>,
    modifier: Modifier = Modifier,
    onSelectionChange: (PlayerId, Boolean) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingLarge)
    ) {
        game.order.forEach { playerId ->
            val player = players[playerId] ?: return@forEach
            val checked = playerId in selectedPlayers
            val enabled = checked || selectedPlayers.size < 2

            PlayerCheckBox(
                playerId = playerId,
                player = player,
                selected = checked,
                enabled = enabled,
                onSelectionChange = onSelectionChange
            )
        }
    }
}


@Composable
private fun PlayerCheckBox(
    playerId: PlayerId,
    player: Player,
    selected: Boolean,
    enabled: Boolean,
    onSelectionChange: (PlayerId, Boolean) -> Unit,
) {
    Selectable(
        selected = selected,
        enabled = enabled,
        onClick = { onSelectionChange(playerId, !selected) },
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
    game: Game,
    players: Players,
    selectedPlayers: PersistentSet<PlayerId>,
    modifier: Modifier = Modifier,
    onSelection: (PlayerId) -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingLarge)
    ) {
        game.order.forEach { playerId ->
            val player = players[playerId] ?: return@forEach
            PlayerRadioButton(
                playerId = playerId,
                player = player,
                selected = playerId in selectedPlayers,
                onSelection = onSelection
            )

        }
    }
}

@Composable
private fun PlayerRadioButton(
    playerId: PlayerId,
    player: Player,
    selected: Boolean,
    onSelection: (PlayerId) -> Unit
) {
    Selectable(
        selected = selected,
        onClick = { onSelection(playerId) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            player.name, Modifier.padding(Style.Dimensions.paddingLarge),
        )
    }
}
