package be.niels.billen.presentation.screens.editplayers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Players
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.app.AppAction
import be.niels.billen.presentation.app.AppScreen
import org.koin.compose.koinInject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
fun EditPlayersView(
    viewModel: EditPlayersViewModel = koinInject(),
    onAppAction: (AppAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val players by viewModel.players.collectAsState()

    EditPlayersView(players = players, onAction = viewModel::onAction, onAppAction = onAppAction, modifier = modifier)
}

@Composable
fun EditPlayersView(
    players: Players,
    modifier: Modifier = Modifier,
    onAppAction: (AppAction) -> Unit,
    onAction: (EditPlayersAction) -> Unit
) {
    Column(
        modifier = modifier.padding(Style.Dimensions.paddingLarge).widthIn(max = 800.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingExtraExtraLarge)
    ) {
        Text(text = "Edit player names", style = MaterialTheme.typography.titleLarge)

        PlayerNameInputs(players = players, onAction = onAction, modifier = Modifier.weight(1f))

        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = { onAppAction(AppAction.Navigate(AppScreen.OVERVIEW)) }) {
            Text("Save")
        }
    }
}

@Composable
private fun PlayerNameInputs(
    players: Players,
    onAction: (EditPlayersAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = Style.Dimensions.paddingSmall),
    ) {
        players.entries.sortedBy { it.key }.forEach { (playerId, player) ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium),
                verticalAlignment = Alignment.CenterVertically
            )
            {
                Text(
                    text = "Player ${playerId.ordinal + 1}:",
                    modifier = Modifier.width(96.dp),
                    maxLines = 1,
                    softWrap = false
                )

                PlayerNameInput(
                    playerId = playerId,
                    name = player.name,
                    onAction = onAction,
                    modifier = Modifier.weight(1f),
                )
            }
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



