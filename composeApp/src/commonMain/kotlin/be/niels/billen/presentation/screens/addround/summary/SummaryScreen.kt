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
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Players
import be.niels.billen.domain.Round
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.components.Points
import be.niels.billen.presentation.components.Selectable
import be.niels.billen.presentation.screens.addround.AddRoundAction
import be.niels.billen.presentation.screens.addround.AddRoundPanel

@Composable
fun SummaryScreen(
    round: Round,
    players: Players,
    onBack: () -> Unit,
    onNext: () -> Unit,
    onAction: (AddRoundAction) -> Unit,
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
                    val points = round.points(playerId)

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
                            Text(players.getValue(playerId).name, Modifier.weight(1f), fontWeight = FontWeight.Bold)
                            Points(points)
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)
            ) {
                Text("Pass round?")


                Selectable(selected = round.passRound, onClick = { onAction(AddRoundAction.SetPassRound(true)) }) {
                    Text("Yes", Modifier.padding(Style.Dimensions.paddingLarge))
                }
                Selectable(selected = !round.passRound, onClick = { onAction(AddRoundAction.SetPassRound(false)) }) {
                    Text("No", Modifier.padding(Style.Dimensions.paddingLarge))
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}