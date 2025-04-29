package be.niels.billen.presentation.addround.slaminput

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.addround.AddRoundAction

@Composable
fun SlamInputScreen(selectedSlams: UInt?, onAction: (AddRoundAction) -> Unit) {
    Column {
        Text("Select slams:")

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium)
        ) {
            for (slams in 0u..13u) {
                val selected = selectedSlams == slams

                IconToggleButton(
                    checked = selected,
                    onCheckedChange = { onAction(AddRoundAction.SetSlams(slams)) },
                    modifier = Modifier.border(1.dp, Color.Black, CircleShape)
                ) {
                    Text("$slams")
                }
            }
        }
    }
}
