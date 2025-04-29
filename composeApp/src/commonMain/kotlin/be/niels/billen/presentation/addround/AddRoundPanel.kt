package be.niels.billen.presentation.addround

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddRoundPanel(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onContinue: () -> Unit,
    canContinue: () -> Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier) {
        content()

        Spacer(Modifier.weight(1f))

        Row {
            Button(onClick = onCancel, colors = ButtonDefaults.outlinedButtonColors()) {
                Text("Cancel")
            }

            Spacer(Modifier.weight(1f))

            Button(
                enabled = canContinue(),
                colors = ButtonDefaults.buttonColors(),
                onClick = onContinue
            ) {
                Text("Next")
            }
        }
    }
}