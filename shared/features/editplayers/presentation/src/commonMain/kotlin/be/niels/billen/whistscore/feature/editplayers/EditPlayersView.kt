package be.niels.billen.whistscore.feature.editplayers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.Player
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Players
import be.niels.billen.whistscore.feature.core.icons.ArrowDown
import be.niels.billen.whistscore.feature.core.icons.ArrowUp
import be.niels.billen.whistscore.feature.ui.Button
import be.niels.billen.whistscore.feature.ui.IconButton
import be.niels.billen.whistscore.feature.ui.Style
import be.niels.billen.whistscore.feature.ui.TextButton
import be.niels.billen.whistscore.feature.ui.TextField
import kotlinx.collections.immutable.ImmutableList
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EditPlayersView(
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditPlayersViewModel = koinViewModel(),
) {
    val order by viewModel.order.collectAsState()
    val players by viewModel.players.collectAsState()

    EditPlayersView(
        order = order,
        players = players,
        onAction = viewModel::onAction,
        onSave = onSave,
        modifier = modifier,
    )
}

@Composable
internal fun EditPlayersView(
    order: ImmutableList<PlayerId>,
    players: Players,
    onSave: () -> Unit,
    onAction: (EditPlayersAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    // The top and bottom padding are owned by the AppShell content area.
    Column(
        modifier =
            modifier
                .wrapContentWidth()
                .widthIn(max = 800.dp)
                .padding(Style.Dimensions.paddingSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingExtraExtraLarge),
    ) {
        Text(text = "Edit player names", style = MaterialTheme.typography.titleLarge)

        PlayerNameInputs(order = order, players = players, onAction = onAction, modifier = Modifier.weight(1f))

        TextButton(
            modifier = Modifier.align(Alignment.End),
            onClick = onSave,
            text = "Save",
        )
    }
}

@Composable
private fun PlayerNameInputs(
    order: ImmutableList<PlayerId>,
    players: Players,
    onAction: (EditPlayersAction) -> Unit,
    modifier: Modifier = Modifier,
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
                onAction = onAction,
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
        verticalAlignment = Alignment.CenterVertically,
    ) {
        key(playerId) {
            PlayerNameInput(
                playerId = playerId,
                name = player.name,
                onAction = onAction,
                modifier = Modifier.weight(1f),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                enabled = index > 0,
                onClick = { onAction(EditPlayersAction.MoveUp(playerId)) },
                icon = ArrowUp,
                contentDescription = "Move up",
            )

            IconButton(
                enabled = index < PlayerId.entries.size - 1,
                onClick = { onAction(EditPlayersAction.MoveDown(playerId)) },
                icon = ArrowDown,
                contentDescription = "Move down",
            )
        }
    }
}

@Composable
private fun PlayerNameInput(
    playerId: PlayerId,
    name: String,
    onAction: (EditPlayersAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember { mutableStateOf(TextFieldValue(text = name)) }

    TextField(
        value = value,
        onValueChange = {
            value = it
            onAction(EditPlayersAction.ChangeName(playerId, it.text))
        },
        modifier = modifier,
    )
}
