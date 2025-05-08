package be.niels.billen.presentation.screens.overview.rounds

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.niels.billen.domain.PlayerId
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.components.Points
import org.koin.compose.koinInject

@Composable
fun RoundsView(modifier: Modifier = Modifier, viewModel: RoundsViewModel = koinInject()) {
    val rounds = viewModel.rounds.collectAsState().value
    val players = viewModel.players.collectAsState().value
    val roundColumnWidth = 64.dp
    val shape = RoundedCornerShape(Style.Dimensions.radiusMedium)


    Box(modifier.background(Color(0xFF1a1a1f), shape).padding(Style.Dimensions.paddingMedium)) {
        Column(verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)) {
            Row {
                Text(
                    "Round",
                    modifier = Modifier.width(roundColumnWidth),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
                for (playerId in PlayerId.entries) {
                    Text(
                        players.getValue(playerId).name,
                        Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(Modifier.background(MaterialTheme.colorScheme.onSurface).fillMaxWidth().requiredHeight(1.dp))

            if (rounds.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No rounds have been played")
                }

            }
            else {

                LazyColumn(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)
                ) {
                    itemsIndexed(rounds) { index, round ->
                        Row {
                            Text("#${index + 1}", Modifier.width(roundColumnWidth), textAlign = TextAlign.Center)

                            for (playerId in PlayerId.entries) {
                                Points(round.points(playerId), Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}