package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        modifier =
            modifier
                .background(color = Style.Colors.surface, shape = shape)
                .border(
                    width = TableOutlineWidth,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = shape,
                ).clip(shape = shape),
    ) {
        TableRow(content = header)

        if (rows.isNotEmpty()) {
            TableRows(rows = rows, rowContent = rowContent, modifier = modifier.weight(1f))
        } else if (emptyContent != null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.weight(1f)) {
                emptyContent()
            }
        }
    }
}

@Composable
private fun <T> TableRows(
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
private fun TableDivider() =
    HorizontalDivider(
        thickness = TableRowDividerThickness,
        color = MaterialTheme.colorScheme.outlineVariant,
    )

@Composable
private fun TableRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier =
                modifier.padding(
                    vertical = TableRowVerticalPadding,
                    horizontal = TableRowHorizontalPadding,
                ),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
        TableDivider()
    }
}
