package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class ButtonVariant { Primary, Secondary, Ghost, Destructive }

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = Style.Shapes.mediumRoundedCornerShape

    Row(
        modifier = modifier
            .heightIn(min = 32.dp)
            .clickable(
                onClick = onClick,
                enabled = enabled,
                indication = null,
                interactionSource = interactionSource
            )
            .background(color = variant.backgroundColor, shape = shape)
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
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    text: String
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
internal val ButtonVariant.backgroundColor
    get() = when (this) {
        ButtonVariant.Primary -> MaterialTheme.colorScheme.primary
        // astryx y2k --color-neutral: a 10% white overlay (neutral.dark[95] @ 0x1A).
        ButtonVariant.Secondary -> Style.Colors.textPrimary.copy(alpha = 0.10f)
        ButtonVariant.Ghost -> Color.Transparent
        ButtonVariant.Destructive -> MaterialTheme.colorScheme.error
    }

@get:Composable
internal val ButtonVariant.contentColor
    get() = when (this) {
        ButtonVariant.Primary -> MaterialTheme.colorScheme.onPrimary
        // astryx y2k secondary icon/text color is --color-text-primary.
        ButtonVariant.Secondary -> Style.Colors.textPrimary
        ButtonVariant.Ghost -> MaterialTheme.colorScheme.primary
        ButtonVariant.Destructive -> MaterialTheme.colorScheme.onError
    }