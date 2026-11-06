package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

// astryx y2k single-line input, md size: --size-element-md 40px,
// --spacing-2 12px inline padding, --spacing-1 6px vertical padding.
private val TextFieldHeight = 40.dp
private val TextFieldBorderWidth = 1.dp
private val TextFieldFocusRingWidth = 2.dp

@Composable
fun TextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    placeholder: String? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isHovered by interactionSource.collectIsHoveredAsState()
    val shape = Style.Shapes.smallRoundedCornerShape
    val textStyle = MaterialTheme.typography.bodyLarge.copy(color = Style.Colors.textPrimary)

    val borderColor = if (isFocused) Style.Colors.textPrimary else Style.Colors.border
    // astryx paints a 2px inset ring: accent-muted on focus, a 30% border on hover.
    val ringColor = when {
        isFocused -> Style.Colors.accentMuted
        enabled && isHovered -> Style.Colors.border.copy(alpha = 0.3f)
        else -> Color.Transparent
    }

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .alpha(if (enabled) 1f else 0.5f)
            .background(color = Style.Colors.surface, shape = shape)
            .then(
                if (ringColor == Color.Transparent) {
                    Modifier
                } else {
                    Modifier.border(TextFieldBorderWidth + TextFieldFocusRingWidth, ringColor, shape)
                }
            )
            .border(TextFieldBorderWidth, borderColor, shape)
            .padding(horizontal = Style.Dimensions.paddingMedium),
        enabled = enabled,
        singleLine = true,
        textStyle = textStyle,
        cursorBrush = SolidColor(Style.Colors.textPrimary),
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth().heightIn(min = TextFieldHeight),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (placeholder != null && value.text.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle.copy(color = Style.Colors.textSecondary),
                    )
                }
                innerTextField()
            }
        },
    )
}
