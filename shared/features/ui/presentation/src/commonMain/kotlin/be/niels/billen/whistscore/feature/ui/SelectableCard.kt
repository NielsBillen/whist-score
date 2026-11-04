package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Composable
fun SelectableCard(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    role: Role = Role.RadioButton,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = Style.Shapes.mediumRoundedCornerShape
    Card(
        modifier = modifier
            .background(
                color = if (selected) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surface,
                shape = shape,
            )
            .border(
                width = 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                shape = shape,
            )
            .semantics { this.role = role }
            .clickable(enabled = enabled, onClick = onClick),
    ) {
        content()
    }
}
