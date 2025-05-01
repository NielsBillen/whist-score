package be.niels.billen.presentation.screens.addround.bidachievedinput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.components.Selectable
import be.niels.billen.presentation.screens.addround.AddRoundAction
import be.niels.billen.presentation.screens.addround.AddRoundPanel

@Composable
fun BidAchievedInput(
    modifier: Modifier = Modifier,
    initialBidAchieved: Boolean?,
    onCancel: () -> Unit,
    onAction: (AddRoundAction) -> Unit
) {
    var selectedBidAchieved by remember { mutableStateOf(initialBidAchieved) }

    AddRoundPanel(
        title = "Bid Achieved",
        description = "Choose whether the player achieved the bid",
        onBack = onCancel,
        onNext = { selectedBidAchieved?.let { onAction(AddRoundAction.SetBidAchieved(it)) } },
        nextEnabled = { selectedBidAchieved != null },
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingLarge)
        ) {
            Selectable(
                selectedBidAchieved == true,
                onClick = { selectedBidAchieved = true },
                Modifier.fillMaxWidth()
            ) {
                Text(
                    "Yes",
                    Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center)
                )
            }

            Selectable(
                selectedBidAchieved == false,
                onClick = { selectedBidAchieved = false },
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
