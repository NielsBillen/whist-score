package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.whistscore.feature.ui.SelectableCard
import be.niels.billen.whistscore.feature.ui.SelectableCardState
import be.niels.billen.whistscore.feature.ui.Style
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BidAchievedInput(
    onBidAchievedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    BidAchievedInput(
        bidAchieved = state.bidAchieved,
        onBidAchievedChange = onBidAchievedChange,
        modifier = modifier,
    )
}

@Composable
internal fun BidAchievedInput(
    bidAchieved: Boolean?,
    onBidAchievedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    AddRoundPanel(
        title = "Bid Achieved",
        description = "Choose whether the player achieved the bid",
        modifier = modifier,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingLarge),
        ) {
            SelectableCard(
                state = SelectableCardState(selected = bidAchieved == true),
                onClick = { onBidAchievedChange(true) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Yes",
                    modifier = Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center),
                )
            }

            SelectableCard(
                state = SelectableCardState(selected = bidAchieved == false),
                onClick = { onBidAchievedChange(false) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "No",
                    modifier = Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center),
                )
            }
        }
    }
}
