package be.niels.billen.presentation.addround

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import be.niels.billen.domain.RoundType
import be.niels.billen.presentation.Style
import org.koin.compose.koinInject

@Composable
fun AddRound(modifier: Modifier = Modifier) {
    val viewModel: AddRoundViewModel = koinInject()

    Column(modifier = modifier.padding(Style.Dimensions.paddingMedium)) {
        Text("Select round type:")

        FlowRow {
            RoundType.entries.forEach { type ->
                RoundTypeButton(type, { viewModel.onAction(it) })
            }
        }
    }
}

@Composable
private fun RoundTypeButton(roundType: RoundType, onAction: (AddRoundAction) -> Unit) {
    Button(onClick = { onAction(AddRoundAction.SetRoundType(roundType)) }) {
        Text(roundType.displayName)
    }
}

private val RoundType.displayName: String
    get() = when (this) {
        RoundType.Regular -> "Regular"
    }