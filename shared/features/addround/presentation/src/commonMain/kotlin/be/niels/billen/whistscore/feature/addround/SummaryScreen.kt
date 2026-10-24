package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.*
import be.niels.billen.whistscore.feature.game.Game
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SummaryScreen(
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val game by viewModel.game.collectAsState()
    val players by viewModel.players.collectAsState()

    SummaryScreen(modifier = modifier, game = game, players = players, round = state.round)
}

@Composable
internal fun SummaryScreen(
    modifier: Modifier = Modifier,
    game: Game,
    players: Players,
    round: Round?,

    ) {
    AddRoundPanel(
        title = "Summary",
        modifier = modifier,
    ) {
        if (round == null) {
            Text("Invalid round")
        } else {
            RoundSummary(round = round, game = game, players = players)
        }
    }
}

@Composable
private fun RoundSummary(game: Game, round: Round, players: Players) {
    val shape = RoundedCornerShape(Style.Dimensions.radiusMedium)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)
    ) {
        val nextGame = game + round
        val roundIndex = nextGame.rounds.lastIndex

        for (playerId in game.order) {
            if (playerId !in players) continue
            val points = nextGame.rounds.points(roundIndex, playerId)

            Row(
                Modifier.background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = shape
                ).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.secondary,
                    shape = shape
                ).padding(Style.Dimensions.paddingLarge)
            ) {
                Text(
                    text = players.getValue(playerId).name,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold
                )
                Points(points = points)
            }
        }
    }
}