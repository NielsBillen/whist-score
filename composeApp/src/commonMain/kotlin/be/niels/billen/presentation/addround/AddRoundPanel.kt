package be.niels.billen.presentation.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.niels.billen.presentation.Style

@Composable
fun AddRoundPanel(
    title: String,
    onBack: () -> Unit,
    onNext: () -> Unit,
    nextEnabled: () -> Boolean,
    modifier: Modifier = Modifier,
    description: String? = null,
    backText: String = "Back",
    nextText: String = "Next",
    content: @Composable BoxScope.() -> Unit
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Column(
            Modifier.widthIn(max = 800.dp).weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingExtraExtraLarge)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge)

            if (description != null) Text(description, textAlign = TextAlign.Center)

            Box(Modifier.weight(1f)) {
                content()
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Text(backText)
            }

            Button(
                onClick = onNext,
                enabled = nextEnabled(),
                colors = ButtonDefaults.buttonColors(),
            ) {
                Text(nextText)
            }
        }
    }
}