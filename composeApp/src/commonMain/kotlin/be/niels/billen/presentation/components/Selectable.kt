package be.niels.billen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import be.niels.billen.presentation.Style

@Composable
fun Selectable(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    radius: Dp = Style.Dimensions.radiusMedium,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier = modifier.background(
            color = if (selected) MaterialTheme.colorScheme.surfaceBright else MaterialTheme.colorScheme.surface,
            shape = shape
        ).border(
            width = 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            shape
        ).clip(shape).clickable(enabled = enabled, onClick = onClick)
    ) {
        content()
    }
}