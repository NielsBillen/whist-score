package be.niels.billen.presentation.screens.addround.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.niels.billen.domain.Game
import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.components.Points
import be.niels.billen.presentation.screens.addround.AddRoundPanel

@Composable
fun SummaryScreen(
    game: Game,
    round: Round,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    val shape = RoundedCornerShape(Style.Dimensions.radiusMedium)

    AddRoundPanel(
        title = "Summary",
        onBack = onBack,
        onNext = onNext,
        nextEnabled = { true }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)) {
                for (playerId in PlayerId.entries) {
                    if (playerId !in game.players) continue
                    val isPassRounds = game.rounds.lastOrNull() is Round.PassRound
                    val points = round.points(playerId = playerId, passRound = isPassRounds)

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