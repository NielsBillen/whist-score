package be.niels.billen.whistscore.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.Style

enum class ButtonVariant { Primary, Secondary, Ghost, Destructive }
enum class ButtonSize { Small, Medium, Large }

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Secondary,
    size: ButtonSize = ButtonSize.Medium,
    isLoading: Boolean = false,
    isIconOnly: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    endContent: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val backgroundColor = when (variant) {
        ButtonVariant.Primary -> MaterialTheme.colorScheme.primary
        ButtonVariant.Secondary -> MaterialTheme.colorScheme.secondary
        ButtonVariant.Ghost -> Color.Transparent
        ButtonVariant.Destructive -> MaterialTheme.colorScheme.error
    }
    val contentColor = when (variant) {
        ButtonVariant.Primary -> MaterialTheme.colorScheme.onPrimary
        ButtonVariant.Secondary -> MaterialTheme.colorScheme.onSecondary
        ButtonVariant.Ghost -> MaterialTheme.colorScheme.primary
        ButtonVariant.Destructive -> MaterialTheme.colorScheme.onError
    }

    val padding = if (isIconOnly) {
        PaddingValues(0.dp)
    } else {
        PaddingValues(
            vertical = Style.Dimensions.paddingSmall,
            horizontal = Style.Dimensions.paddingMedium
        )
    }

    val borderRadius = Style.Dimensions.radiusMedium

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        contentPadding = padding,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(borderRadius),
        content = {
            if (isLoading) {
                Spacer(modifier = Modifier.padding(4.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = contentColor
                )
                if (icon != null) {
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            } else {
                if (icon != null) {
                    icon()
                }
                content()
                if (endContent != null) {
                    Spacer(modifier = Modifier.padding(4.dp))
                    endContent()
                }
            }
        }
    )
}