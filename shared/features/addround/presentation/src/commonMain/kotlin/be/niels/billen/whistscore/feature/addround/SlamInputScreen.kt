package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.Style
import be.niels.billen.whistscore.feature.core.Selectable

@Composable
fun SlamInputScreen(
    modifier: Modifier = Modifier,
    selectedSlams: Int?,
    onSlamsSelected: (Int) -> Unit,
) {
    AddRoundPanel(
        title = "Select slams",
        description = "Choose the number of slams the player(s) achieved this round.",
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
            for (slams in 0..13) {
                val selected = selectedSlams == slams

                Selectable(
                    selected,
                    onClick = { onSlamsSelected(slams) },
                    Modifier.requiredSize(56.dp)
                ) {
                    Text(
                        "$slams",
                        Modifier.padding(Style.Dimensions.paddingLarge).align(Alignment.Center)
                    )
                }
            }
        }
    }
}
