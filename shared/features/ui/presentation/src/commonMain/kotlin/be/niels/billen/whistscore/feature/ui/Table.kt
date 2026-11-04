package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

// astryx "balanced" row density: --spacing-2 (8dp) block, --spacing-3 (12dp) inline.
private val TableRowVerticalPadding = Style.Dimensions.paddingSmall
private val TableRowHorizontalPadding = Style.Dimensions.paddingMedium
private val TableRowDividerThickness = 1.dp
private val TableOutlineWidth = 1.dp

@Composable
fun <T> Table(
    rows: ImmutableList<T>,
    header: @Composable RowScope.() -> Unit,
    rowContent: @Composable RowScope.(item: T, index: Int) -> Unit,
    modifier: Modifier = Modifier,
    emptyContent: (@Composable () -> Unit)? = null,
) {
    val shape = Style.Shapes.mediumRoundedCornerShape

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(color = Color(0xFF1a1a1f), shape = shape)
            .border(
                width = TableOutlineWidth,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = shape,
            )
    ) {
        TableHeader(content = header)

        if (rows.isNotEmpty()) {
            TableContent(rows = rows, rowContent = rowContent, modifier = modifier.weight(1f))
        } else {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                emptyContent?.invoke()
            }
        }
    }
}

@Composable
private fun <T> TableContent(
    rows: ImmutableList<T>,
    rowContent: @Composable RowScope.(item: T, index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(rows) { index, item ->
            TableRow { rowContent(item, index) }
        }
    }
}

@Composable
private fun TableHeader(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Column(modifier = modifier) {
        TableRow(content = content)

        HorizontalDivider(
            thickness = TableRowDividerThickness,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    }
}

@Composable
private fun TableRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.padding(
            vertical = TableRowVerticalPadding,
            horizontal = TableRowHorizontalPadding,
        ),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
