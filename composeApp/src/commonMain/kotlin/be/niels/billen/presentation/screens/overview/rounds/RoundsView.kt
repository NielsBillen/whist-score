package be.niels.billen.presentation.screens.overview.rounds

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
import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Rounds
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.components.Points
import be.niels.billen.presentation.theme.Icons
import be.niels.billen.presentation.theme.icons.Trash
import org.koin.compose.koinInject

private val ROUNDS_COLUMN_WIDTH = 64.dp
private val ICONS_SIZE = 32.dp


@Composable
fun RoundsView(
    modifier: Modifier = Modifier,
    viewModel: RoundsViewModel = koinInject(),
) {
    val rounds by viewModel.rounds.collectAsState()
    val players by viewModel.players.collectAsState()

    RoundsView(rounds = rounds, players = players, modifier = modifier, onAction = viewModel::onAction)
}

@Composable
fun RoundsView(
    rounds: Rounds,
    players: Map<PlayerId, Player>,
    modifier: Modifier = Modifier,
    onAction: (RoundsViewAction) -> Unit
) {
    Box(
        modifier
            .background(
                color = Color(0xFF1a1a1f),
                shape = Style.Shapes.mediumRoundedCornerShape
            )
            .padding(Style.Dimensions.paddingMedium)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Round",
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

            Box(Modifier.background(MaterialTheme.colorScheme.onSurface).fillMaxWidth().requiredHeight(1.dp))

            if (rounds.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No rounds have been played")
                }

            } else {
                LazyColumn(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)
                ) {
                    itemsIndexed(rounds) { index, round ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("#${index + 1}", Modifier.width(ROUNDS_COLUMN_WIDTH), textAlign = TextAlign.Center)

                            for (playerId in PlayerId.entries) {
                                Points(round.points(playerId), Modifier.weight(1f))
                            }

                            IconButton(
                                onClick = { onAction(RoundsViewAction.DeleteRound(index = index)) },
                                modifier = Modifier.requiredSize(ICONS_SIZE)
                            ) {
                                Icon(
                                    imageVector = Icons.Trash,
                                    contentDescription = "Delete round",
                                    modifier = Modifier.fillMaxSize().padding(Style.Dimensions.paddingSmall)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}