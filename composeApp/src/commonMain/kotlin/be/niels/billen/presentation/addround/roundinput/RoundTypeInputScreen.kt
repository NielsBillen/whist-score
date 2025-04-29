package be.niels.billen.presentation.addround.roundinput

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import be.niels.billen.domain.RoundType
import be.niels.billen.presentation.addround.AddRoundAction
import be.niels.billen.presentation.addround.AddRoundPanel


@Composable
fun RoundTypeInputScreen(
    modifier: Modifier = Modifier,
    initialRoundType: RoundType?,
    onAction: (AddRoundAction) -> Unit,
    onCancel: () -> Unit
) {
    var selectedRoundType by remember { mutableStateOf(initialRoundType) }

    AddRoundPanel(
        onCancel = onCancel,
        onContinue = { selectedRoundType?.let { onAction(AddRoundAction.SetRoundType(it)) } },
        canContinue = { selectedRoundType != null }) {
        Text("Select round type:", style = MaterialTheme.typography.titleLarge)

        Column {
            RoundType.entries.forEach { type ->
                RoundTypeButton(type, selectedRoundType, { selectedRoundType = type })
            }
        }
    }
//    Column(modifier) {
//        Text("Select round type:", style = MaterialTheme.typography.titleLarge)
//
//        Column {
//            RoundType.entries.forEach { type ->
//                RoundTypeButton(type, selectedRoundType, { selectedRoundType = type })
//            }
//        }
//
//        Spacer(Modifier.weight(1f))
//
//        Row {
//            Button(onClick = onCancel, colors = ButtonDefaults.outlinedButtonColors()) {
//                Text("Cancel")
//            }
//
//            Spacer(Modifier.weight(1f))
//
//
//            Button(enabled = selectedRoundType != null,
//                colors = ButtonDefaults.buttonColors(),
//                onClick = {
//                selectedRoundType?.let { onAction(AddRoundAction.SetRoundType(it)) }
//            }) {
//                Text("Next")
//            }
//        }
//    }
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