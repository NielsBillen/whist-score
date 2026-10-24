package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.Style

@Composable
fun AddRoundPanel(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize().wrapContentSize().widthIn(max = 800.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingExtraExtraLarge)
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)

        if (description != null) Text(text = description, textAlign = TextAlign.Center)

        Box(modifier = Modifier.weight(1f), content = content)
    }
}
