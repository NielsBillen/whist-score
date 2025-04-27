package be.niels.billen.presentation.addround

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.RoundType
import be.niels.billen.presentation.Style
import org.koin.compose.koinInject

@Composable
fun AddRound(modifier: Modifier = Modifier) {
    val viewModel: AddRoundViewModel = koinInject()
    val state = viewModel.state.collectAsState().value
    val players = viewModel.players.collectAsState()
    val selectedRoundType = state.roundType
    val selectedPlayers = state.players
    val selectedSlams = state.slams

    Column(modifier = modifier.padding(Style.Dimensions.paddingMedium)) {
        RoundTypeInput(selectedRoundType = state.roundType, onAction = { viewModel.onAction(it) })

        if (selectedRoundType != null) {
            PlayersInput(
                roundType = selectedRoundType,
                players = players.value,
                selectedPlayers = state.players,
                onAction = { viewModel.onAction(it) })
        }

        if (selectedRoundType != null && selectedPlayers.isNotEmpty()) {
            SlamInput(selectedSlams = selectedSlams, onAction = { viewModel.onAction(it) })
        }

        Spacer(Modifier.weight(1f))


        Button(onClick = {
            val round = state.round
            if (round != null) viewModel.onAction(AddRoundAction.AddRound(round))

        }, enabled = state.isValid, modifier = Modifier.align(Alignment.End)) {
            Text("Add")
        }
    }
}

@Composable
fun RoundTypeInput(modifier: Modifier = Modifier, selectedRoundType: RoundType?, onAction: (AddRoundAction) -> Unit) {
    Column {
        Text("Select round type:")

        Column(modifier) {
            RoundType.entries.forEach { type ->
                RoundTypeButton(type, selectedRoundType, onAction)
            }
        }
    }
}

@Composable
private fun RoundTypeButton(roundType: RoundType, selectedRoundType: RoundType?, onAction: (AddRoundAction) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = roundType == selectedRoundType,
            onClick = { onAction(AddRoundAction.SetRoundType(roundType)) })
        Text(roundType.displayName)
    }
}

private val RoundType.displayName: String
    get() = when (this) {
        RoundType.Regular -> "Regular"
        RoundType.Abandonce -> "Abandonce"
    }

@Composable
fun PlayersInput(
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
fun MultiPlayerChoice(
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
fun SinglePlayerChoice(
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


@Composable
private fun SlamInput(selectedSlams: UInt?, onAction: (AddRoundAction) -> Unit) {
    Column {
        Text("Select slams:")

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)
        ) {
            for (slams in 0u..13u) {
                val selected = selectedSlams == slams

                IconToggleButton(
                    checked = selected,
                    onCheckedChange = { onAction(AddRoundAction.SetSlams(slams)) },
                    modifier = Modifier.border(1.dp, Color.Black, CircleShape)
                ) {
                    Text("$slams")
                }
            }
        }
    }
}
