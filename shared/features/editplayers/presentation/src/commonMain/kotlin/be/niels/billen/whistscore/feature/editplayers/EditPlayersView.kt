package be.niels.billen.whistscore.feature.editplayers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.Player
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Players
import be.niels.billen.whistscore.feature.core.Style
import be.niels.billen.whistscore.feature.core.icons.ArrowDown
import be.niels.billen.whistscore.feature.core.icons.ArrowUp
import kotlinx.collections.immutable.ImmutableList
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditPlayersView(
    modifier: Modifier = Modifier,
    viewModel: EditPlayersViewModel = koinViewModel(),
    onSave: () -> Unit,
) {
    val order by viewModel.order.collectAsState()
    val players by viewModel.players.collectAsState()

    EditPlayersView(
        order = order,
        players = players,
        onAction = viewModel::onAction,
        onSave = onSave,
        modifier = modifier
    )
}

@Composable
internal fun EditPlayersView(
    order: ImmutableList<PlayerId>,
    players: Players,
    modifier: Modifier = Modifier,
    onSave: () -> Unit,
    onAction: (EditPlayersAction) -> Unit
) {
    Column(
        modifier = modifier.padding(Style.Dimensions.paddingLarge).widthIn(max = 800.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingExtraExtraLarge)
    ) {
        Text(text = "Edit player names", style = MaterialTheme.typography.titleLarge)

        PlayerNameInputs(order = order, players = players, onAction = onAction, modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = onSave
        ) {
            Text("Save")
        }
    }
}

@Composable
private fun PlayerNameInputs(
    order: ImmutableList<PlayerId>,
    players: Players,
    onAction: (EditPlayersAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = Style.Dimensions.paddingSmall),
    ) {
        order.forEachIndexed { index, playerId ->
            val player = players[playerId] ?: return@forEachIndexed

            PlayerEntry(
                index = index,
                playerId = playerId,
                player = player,
                onAction = onAction
            )

        }
    }
}

@Composable
private fun PlayerEntry(
    index: Int,
    playerId: PlayerId,
    player: Player,
    onAction: (EditPlayersAction) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        key(playerId) {
            PlayerNameInput(
                playerId = playerId,
                name = player.name,
                onAction = onAction,
                modifier = Modifier.weight(1f),
            )
        }

        IconButton(
            enabled = index > 0,
            onClick = { onAction(EditPlayersAction.MoveUp(playerId)) }
        ) {
            Icon(imageVector = ArrowUp, contentDescription = "Move up")
        }

        IconButton(
            enabled = index < PlayerId.entries.size - 1,
            onClick = { onAction(EditPlayersAction.MoveDown(playerId)) }
        ) {
            Icon(imageVector = ArrowDown, contentDescription = "Move down")
        }
    }
}

@Composable
private fun PlayerNameInput(
    playerId: PlayerId,
    name: String,
    modifier: Modifier = Modifier,
    onAction: (EditPlayersAction) -> Unit
) {
    var value by remember { mutableStateOf(TextFieldValue(text = name)) }

    TextField(
        value = value,
        onValueChange = {
            value = it
            onAction(EditPlayersAction.ChangeName(playerId, it.text))
        },
        maxLines = 1,
        modifier = modifier,
    )
}



