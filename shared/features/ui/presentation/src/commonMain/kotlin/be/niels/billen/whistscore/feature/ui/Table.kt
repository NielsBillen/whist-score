package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

// astryx "balanced" row density: --spacing-2 (8dp) block, --spacing-3 (12dp) inline.
private val TableRowVerticalPadding = Style.Dimensions.paddingSmall
private val TableRowHorizontalPadding = Style.Dimensions.paddingMedium
private val TableRowDividerThickness = 1.dp

/**
 * A data-driven table: a header row plus one body row per item, separated by
 * hairline dividers (none under the last row).
 *
 * The header and each row lay out their own cells inside the given [RowScope],
 * so cell content can be any composable — text, icons, buttons.
 */
@Composable
fun <T> Table(
    rows: ImmutableList<T>,
    header: @Composable RowScope.() -> Unit,
    rowContent: @Composable RowScope.(item: T, index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        item {
            TableHeader(content = header)
        }

        itemsIndexed(rows) { index, item ->
            TableRow(modifier = Modifier.fillMaxWidth()) { rowContent(item, index) }

        }
    }
}

@Composable
private fun TableHeader(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    Column(modifier = modifier) {
        TableRow(content = content, modifier = Modifier.fillMaxWidth())

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
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
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = TableRowVerticalPadding,
                horizontal = TableRowHorizontalPadding,
            ),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}
