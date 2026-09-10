package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.whistscore.feature.core.Style
import be.niels.billen.whistscore.feature.core.Selectable

@Composable
fun BidAchievedInput(
    modifier: Modifier = Modifier,
    selectedBidAchieved: Boolean?,
    onBidAchievedSelected: (Boolean) -> Unit,
) {
    AddRoundPanel(
        title = "Bid Achieved",
        description = "Choose whether the player achieved the bid",
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingLarge)
        ) {
            Selectable(
                selectedBidAchieved == true,
                onClick = { onBidAchievedSelected(true) },
                Modifier.fillMaxWidth()
            ) {
                Text(
                    "Yes",
                    Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center)
                )
            }

            Selectable(
                selectedBidAchieved == false,
                onClick = { onBidAchievedSelected(false) },
                Modifier.fillMaxWidth()
            ) {
                Text(
                    "No",
                    Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center)
                )
            }
        }
    }
}
