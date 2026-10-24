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
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BidAchievedInput(
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
    onBidAchievedSelected: (Boolean) -> Unit,
) {
    val state by viewModel.state.collectAsState()
    BidAchievedInput(
        modifier = modifier,
        bidAchieved = state.bidAchieved,
        onBidAchievedSelected = onBidAchievedSelected,
    )
}

@Composable
internal fun BidAchievedInput(
    bidAchieved: Boolean?,
    modifier: Modifier = Modifier,
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
                selected = bidAchieved == true,
                onClick = { onBidAchievedSelected(true) },
                Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Yes",
                    modifier = Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center)
                )
            }

            Selectable(
                selected = bidAchieved == false,
                onClick = { onBidAchievedSelected(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No",
                    modifier = Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center)
                )
            }
        }
    }
}
