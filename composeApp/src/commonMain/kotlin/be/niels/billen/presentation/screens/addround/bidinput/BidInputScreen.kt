package be.niels.billen.presentation.screens.addround.bidinput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.components.Selectable
import be.niels.billen.presentation.screens.addround.AddRoundAction
import be.niels.billen.presentation.screens.addround.AddRoundPanel

@Composable
fun BidInputScreen(
    modifier: Modifier = Modifier,
    initialBid: Int?,
    onCancel: () -> Unit,
    onAction: (AddRoundAction) -> Unit
) {
    var selectedBid by remember { mutableStateOf(initialBid) }

    AddRoundPanel(
        title = "Select bid",
        description = "Choose the number of slams the player bids in this round.",
        onBack = onCancel,
        onNext = { selectedBid?.let { onAction(AddRoundAction.SetBid(it)) } },
        nextEnabled = { selectedBid != null },
        modifier = modifier,
    ) {
        FlowRow(
            maxItemsInEachRow = 7,
            horizontalArrangement = Arrangement.spacedBy(
                Style.Dimensions.paddingLarge,
                Alignment.CenterHorizontally
            ),
            verticalArrangement = Arrangement.spacedBy(
                Style.Dimensions.paddingLarge,
                Alignment.CenterVertically
            )
        ) {
            for (bid in 9..12) {
                val selected = selectedBid == bid

                Selectable(
                    selected,
                    onClick = { selectedBid = bid },
                    Modifier.requiredSize(56.dp)
                ) {
                    Text(
                        "$bid",
                        Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center)
                    )
                }
            }
        }
    }
}
