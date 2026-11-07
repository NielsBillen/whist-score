package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Card(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = Style.Shapes.mediumRoundedCornerShape

    Box(
        modifier =
            modifier
                .background(MaterialTheme.colorScheme.surface, shape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape),
        content = content,
    )
}
