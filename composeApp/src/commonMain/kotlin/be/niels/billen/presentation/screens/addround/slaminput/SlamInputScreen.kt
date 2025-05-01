package be.niels.billen.presentation.screens.addround.slaminput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.screens.addround.AddRoundAction
import be.niels.billen.presentation.screens.addround.AddRoundPanel
import be.niels.billen.presentation.components.Selectable

@Composable
fun SlamInputScreen(
    modifier: Modifier = Modifier,
    initialSlams: Int?,
    onCancel: () -> Unit,
    onAction: (AddRoundAction) -> Unit
) {
    var selectedSlams by remember { mutableStateOf(initialSlams) }

    AddRoundPanel(
        title = "Select slams",
        description = "Choose the number of slams the player(s) achieved this round.",
        onBack = onCancel,
        onNext = { selectedSlams?.let { onAction(AddRoundAction.SetSlams(it)) } },
        nextEnabled = { selectedSlams != null },
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
                    onClick = { selectedSlams = slams },
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
