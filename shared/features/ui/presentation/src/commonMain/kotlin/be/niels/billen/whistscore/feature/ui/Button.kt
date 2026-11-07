package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val focusRingWidth = 2.dp
private val focusRingGap = 3.dp

internal fun Modifier.drawFocusRing(
    cornerRadius: Dp,
    color: Color,
): Modifier =
    drawBehind {
        val gap = focusRingGap.toPx()
        val width = focusRingWidth.toPx()
        val offset = gap + width / 2

        drawRoundRect(
            color = color,
            topLeft = Offset(-offset, -offset),
            size = Size(size.width + 2 * offset, size.height + 2 * offset),
            cornerRadius = CornerRadius((cornerRadius + focusRingGap).toPx()),
            style = Stroke(width = width),
        )
    }

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = Style.Shapes.mediumRoundedCornerShape
    val ringColor = if (enabled && isFocused) variant.focusRingColor else Color.Transparent

    Row(
        modifier =
            modifier
                .heightIn(min = 32.dp)
                .background(color = variant.backgroundColor, shape = shape)
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    indication = null,
                    interactionSource = interactionSource,
                ).drawFocusRing(Style.Dimensions.radiusMedium, ringColor)
                .padding(horizontal = Style.Dimensions.paddingMedium, vertical = Style.Dimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        CompositionLocalProvider(
            LocalContentColor provides variant.contentColor,
            LocalTextStyle provides MaterialTheme.typography.labelLarge,
            content = content,
        )
    }
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        variant = variant,
    ) {
        Text(text = text)
    }
}

@get:Composable
@get:ReadOnlyComposable
internal val ButtonVariant.backgroundColor
    get() =
        when (this) {
            ButtonVariant.Primary -> MaterialTheme.colorScheme.primary

            // astryx y2k --color-neutral: a 10% white overlay (neutral.dark[95] @ 0x1A).
            ButtonVariant.Secondary -> Color(0xFF242531)

            // textPrimary @10% over body, flattened opaque
            ButtonVariant.Ghost -> Color.Transparent

            ButtonVariant.Destructive -> MaterialTheme.colorScheme.error
        }

@get:Composable
@get:ReadOnlyComposable
internal val ButtonVariant.contentColor
    get() =
        when (this) {
            ButtonVariant.Primary -> MaterialTheme.colorScheme.onPrimary

            // astryx y2k secondary icon/text color is --color-text-primary.
            ButtonVariant.Secondary -> Style.Colors.textPrimary

            ButtonVariant.Ghost -> MaterialTheme.colorScheme.primary

            ButtonVariant.Destructive -> MaterialTheme.colorScheme.onError
        }

// astryx y2k: the focus ring matches the variant it rings — accent for
// every variant, error for destructive.
@get:Composable
@get:ReadOnlyComposable
internal val ButtonVariant.focusRingColor
    get() = if (this == ButtonVariant.Destructive) MaterialTheme.colorScheme.error else Style.Colors.accent
