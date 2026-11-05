package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// astryx y2k icon buttons: flat, no border, rounded, no hover state —
// they scale down 0.98 on press and fade to 50% when disabled.
@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: IconButtonSize = IconButtonSize.Sm,
    variant: ButtonVariant = ButtonVariant.Secondary,
    icon: ImageVector,
    contentDescription: String,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shape = Style.Shapes.smallRoundedCornerShape

    Surface(
        modifier = modifier
            .size(size.size)
            .alpha(if (enabled) 1f else 0.5f),
        color = variant.backgroundColor,
        contentColor = variant.contentColor,
        shape = shape,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .scale(if (enabled && isPressed) 0.98f else 1f)
                .clickable(
                    enabled = enabled,
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center,
        ) {
            IconButtonIcon(icon = icon, contentDescription = contentDescription, size = size.iconSize)
        }
    }
}

@Composable
private fun IconButtonIcon(icon: ImageVector, contentDescription: String, size: Dp) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = Modifier.size(size),
        tint = LocalContentColor.current,
    )
}
