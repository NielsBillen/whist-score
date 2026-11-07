package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialog(
    title: String,
    body: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scrim(onDismiss = onDismiss) {
        DialogContent(
            title = title,
            body = body,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            modifier = modifier,
        )
    }
}

@Composable
private fun Scrim(
    onDismiss: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        content = content,
        modifier =
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.3f))
                .pointerInput(Unit) { detectTapGestures { onDismiss() } },
    )
}

@Composable
private fun DialogContent(
    title: String,
    body: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .widthIn(max = 320.dp)
                .wrapContentSize()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = Style.Shapes.mediumRoundedCornerShape,
                ).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = Style.Shapes.mediumRoundedCornerShape,
                ).clip(Style.Shapes.mediumRoundedCornerShape)
                .padding(Style.Dimensions.paddingLarge),
        verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingMedium),
    ) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)

        Text(text = body, style = MaterialTheme.typography.bodyMedium)

        ButtonRow(
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ButtonRow(
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
    ) {
        TextButton(
            variant = ButtonVariant.Ghost,
            onClick = onDismiss,
            text = dismissText,
        )

        TextButton(
            variant = ButtonVariant.Destructive,
            onClick = onConfirm,
            text = confirmText,
        )
    }
}
