package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.game.Game
import be.niels.billen.whistscore.feature.core.Player
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.Style
import be.niels.billen.whistscore.feature.core.Points

@Composable
fun SummaryScreen(
    modifier: Modifier = Modifier,
    game: Game,
    round: Round,
) {
    val shape = RoundedCornerShape(Style.Dimensions.radiusMedium)

    AddRoundPanel(
        title = "Summary",
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)
        ) {
            val nextGame = game + round
            val roundIndex = nextGame.rounds.size - 1

            Column(verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)) {
                for (playerId in PlayerId.entries) {
                    if (playerId !in game.players) continue
                    val points = nextGame.rounds.points(roundIndex, playerId)

                    Box(
                        Modifier.background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = shape
                        ).border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.secondary,
                            shape
                        ).clip(shape)
                    ) {
                        Row(Modifier.padding(Style.Dimensions.paddingLarge)) {
                            Text(
                                text = game.players.getValue(playerId).name,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold
                            )
                            Points(points)
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
