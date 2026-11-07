package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.ui.SelectableCard
import be.niels.billen.whistscore.feature.ui.SelectableCardState
import be.niels.billen.whistscore.feature.ui.Style
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SlamInputScreen(
    onSlamsChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    SlamInputScreen(
        selectedSlams = state.slams,
        onSlamsChange = onSlamsChange,
        modifier = modifier,
    )
}

@Composable
internal fun SlamInputScreen(
    selectedSlams: Int?,
    onSlamsChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    AddRoundPanel(
        title = "Select slams",
        description = "Choose the number of slams the player(s) achieved this round.",
        modifier = modifier,
    ) {
        FlowRow(
            maxItemsInEachRow = 7,
            horizontalArrangement =
                Arrangement.spacedBy(
                    Style.Dimensions.paddingLarge,
                    Alignment.CenterHorizontally,
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    Style.Dimensions.paddingLarge,
                    Alignment.CenterVertically,
                ),
        ) {
            for (slams in 0..13) {
                val selected = selectedSlams == slams

                SelectableCard(
                    state = SelectableCardState(selected = selected),
                    onClick = { onSlamsChange(slams) },
                    modifier = Modifier.requiredSize(56.dp),
                ) {
                    Text(
                        text = "$slams",
                        modifier = Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center),
                    )
                }
            }
        }
    }
}
