package be.niels.billen.whistscore.feature.rounds

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.Players
import be.niels.billen.whistscore.feature.core.Points
import be.niels.billen.whistscore.feature.core.icons.Trash
import be.niels.billen.whistscore.feature.game.Game
import be.niels.billen.whistscore.feature.ui.IconButton
import be.niels.billen.whistscore.feature.ui.IconButtonSize
import be.niels.billen.whistscore.feature.ui.Style
import be.niels.billen.whistscore.feature.ui.Table
import org.koin.compose.viewmodel.koinViewModel

private val ROUNDS_COLUMN_WIDTH = 32.dp

@Composable
fun RoundsView(
    modifier: Modifier = Modifier,
    viewModel: RoundsViewModel = koinViewModel(),
) {
    val game by viewModel.game.collectAsState()
    val players by viewModel.players.collectAsState()

    RoundsView(game = game, players = players, modifier = modifier, onAction = viewModel::onAction)
}

@Composable
fun RoundsView(
    game: Game,
    players: Players,
    modifier: Modifier = Modifier,
    onAction: (RoundsViewAction) -> Unit,
) {
    Table(
        rows = game.rounds.rounds,
        header = { RoundHeader(game = game, players = players) },
        rowContent = { _, index ->
            RoundRow(
                game = game,
                index = index,
                onDelete = { onAction(RoundsViewAction.DeleteRound(index = index)) },
            )
        },
        emptyContent = ::NoRoundsMessage,
        modifier = modifier
    )
}

@Composable
private fun RowScope.RoundHeader(game: Game, players: Players) {
    Text(
        text = "#",
        modifier = Modifier.width(ROUNDS_COLUMN_WIDTH),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold,
    )

    for (playerId in game.order) {
        Text(
            text = players[playerId]?.name ?: "-",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
        )
    }

    Spacer(modifier = Modifier.requiredWidth(IconButtonSize.Sm.size))
}

@Composable
private fun RowScope.RoundRow(
    game: Game,
    index: Int,
    onDelete: () -> Unit,
) {
    Text(
        text = "${index + 1}",
        modifier = Modifier.width(ROUNDS_COLUMN_WIDTH),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center,
    )

    for (playerId in game.order) {
        Points(
            points = game.rounds.points(index, playerId),
            modifier = Modifier.weight(1f)
        )
    }

    IconButton(
        onClick = onDelete,
        icon = Trash,
        contentDescription = "Delete round",
    )
}

@Composable
private fun NoRoundsMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.padding(Style.Dimensions.paddingLarge),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No rounds have been played",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
