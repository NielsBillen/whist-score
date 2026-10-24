package be.niels.billen.whistscore.feature.rounds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.*
import be.niels.billen.whistscore.feature.core.icons.ArrowUp
import be.niels.billen.whistscore.feature.core.icons.Trash
import be.niels.billen.whistscore.feature.game.Game
import org.koin.compose.viewmodel.koinViewModel

private val ROUNDS_COLUMN_WIDTH = 32.dp
private val ICONS_SIZE = 32.dp


@Composable
fun RoundsView(
    modifier: Modifier = Modifier,
    viewModel: RoundsViewModel = koinViewModel(),
) {
    val game by viewModel.game.collectAsState()

    RoundsView(game = game, modifier = modifier, onAction = viewModel::onAction)
}

@Composable
fun RoundsView(
    game: Game,
    modifier: Modifier = Modifier,
    onAction: (RoundsViewAction) -> Unit
) {
    Column(
        modifier = modifier.background(
            color = Color(0xFF1a1a1f),
            shape = Style.Shapes.mediumRoundedCornerShape
        ).padding(Style.Dimensions.paddingMedium),
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)
    ) {
        TableHeader(players = game.players)

        Box(Modifier.background(MaterialTheme.colorScheme.onSurface).fillMaxWidth().requiredHeight(1.dp))

        game.rounds.let { rounds ->
            when {
                rounds.isEmpty() -> NoRoundsMessage(modifier = Modifier.fillMaxWidth().weight(1f))
                else -> RoundsList(rounds = game.rounds, onAction = onAction)
            }
        }
    }
}

@Composable
private fun TableHeader(players: Players) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "#",
            modifier = Modifier.width(ROUNDS_COLUMN_WIDTH),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        for (playerId in PlayerId.entries) {
            Text(
                text = players[playerId]?.name ?: "-",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.requiredWidth(ICONS_SIZE))
    }
}

@Composable
private fun NoRoundsMessage(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("No rounds have been played")
    }
}

@Composable
private fun RoundsList(rounds: Rounds, modifier: Modifier = Modifier, onAction: (RoundsViewAction) -> Unit) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)
    ) {
        itemsIndexed(rounds) { index, _ ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${index + 1}", Modifier.width(ROUNDS_COLUMN_WIDTH), textAlign = TextAlign.Center)

                for (playerId in PlayerId.entries) {
                    Points(
                        rounds.points(index, playerId),
                        Modifier.weight(1f)
                    )
                }

                IconButton(
                    onClick = { onAction(RoundsViewAction.DeleteRound(index = index)) },
                    modifier = Modifier.requiredSize(ICONS_SIZE)
                ) {
                    Icon(
                        imageVector = Trash,
                        contentDescription = "Delete round",
                        modifier = Modifier.fillMaxSize().padding(Style.Dimensions.paddingSmall)
                    )
                }
            }
        }
    }
}