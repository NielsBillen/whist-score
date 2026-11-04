package be.niels.billen.whistscore.feature.rounds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.Points
import be.niels.billen.whistscore.feature.core.Players
import be.niels.billen.whistscore.feature.core.icons.Trash
import be.niels.billen.whistscore.feature.game.Game
import be.niels.billen.whistscore.feature.ui.Style
import be.niels.billen.whistscore.feature.ui.Table
import org.koin.compose.viewmodel.koinViewModel

private val ROUNDS_COLUMN_WIDTH = 32.dp
private val ICONS_SIZE = 32.dp
private val TABLE_OUTLINE_WIDTH = 1.dp

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
    Column(
        modifier = modifier
            .background(color = Color(0xFF1a1a1f), shape = Style.Shapes.mediumRoundedCornerShape)
            .border(
                width = TABLE_OUTLINE_WIDTH,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = Style.Shapes.mediumRoundedCornerShape,
            )
            .clip(Style.Shapes.mediumRoundedCornerShape),
    ) {
        if (game.rounds.isEmpty()) {
            NoRoundsMessage(modifier = Modifier.fillMaxSize())
        } else {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
        }
    }
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

    Spacer(modifier = Modifier.requiredWidth(ICONS_SIZE))
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
        modifier = Modifier.requiredSize(ICONS_SIZE)
    ) {
        Icon(
            imageVector = Trash,
            contentDescription = "Delete round",
            modifier = Modifier.fillMaxSize().padding(Style.Dimensions.paddingSmall)
        )
    }
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
