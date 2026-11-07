package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun SelectableCard(
    state: SelectableCardState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = Style.Shapes.mediumRoundedCornerShape
    Box(
        modifier =
            modifier
                .background(
                    color =
                        if (state.selected) {
                            MaterialTheme.colorScheme.surfaceBright
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                    shape = shape,
                ).border(
                    width = 1.dp,
                    color =
                        if (state.selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                    shape = shape,
                ).semantics { this.role = state.role }
                .clickable(
                    enabled = state.enabled,
                    onClick = onClick,
                    indication = null,
                    interactionSource = null,
                ),
        content = content,
    )
}
