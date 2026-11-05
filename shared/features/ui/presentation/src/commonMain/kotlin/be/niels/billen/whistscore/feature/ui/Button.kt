package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 32.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = variant.backgroundColor,
            contentColor = variant.contentColor
        ),
        contentPadding = buttonPadding,
        shape = Style.Shapes.mediumRoundedCornerShape,
        content = content
    )
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

private val buttonPadding = PaddingValues(
    vertical = Style.Dimensions.paddingSmall,
    horizontal = Style.Dimensions.paddingMedium
)

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