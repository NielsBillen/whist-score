package be.niels.billen.presentation.addround

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.domain.RoundType


@Composable
fun RoundTypeInputScreen(
    modifier: Modifier = Modifier,
    selectedRoundType: RoundType?,
    onAction: (AddRoundAction) -> Unit,
    onCancel: () -> Unit
) {
    var roundType by remember { mutableStateOf(selectedRoundType) }

    Column(modifier) {
        Text("Select round type:", style = MaterialTheme.typography.titleLarge)

        Column {
            RoundType.entries.forEach { type ->
                RoundTypeButton(type, roundType, { roundType = type })
            }
        }

        Spacer(Modifier.weight(1f))

        Row {
            Button(onClick = onCancel) {
                Text("Cancel")
            }

            Spacer(Modifier.weight(1f))


            Button(enabled = roundType != null, onClick = {
                roundType?.let { onAction(AddRoundAction.SetRoundType(it)) }
            }) {
                Text("Next")
            }
        }


    }
}

@Composable
private fun RoundTypeButton(roundType: RoundType, selectedRoundType: RoundType?, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = roundType == selectedRoundType, onClick = onClick
        )
        Text(roundType.displayName)
    }
}

private val RoundType.displayName: String
    get() = when (this) {
        RoundType.Regular -> "Regular"
        RoundType.Abandonce -> "Abandonce"
    }