package be.niels.billen.whistscore.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.Background

@Composable
fun AppShell(
    title: String,
    modifier: Modifier = Modifier,
    appIcon: ImageVector? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Style.Colors.body),
    ) {
        TopBar(
            title = title,
            appIcon = appIcon,
            actions = actions,
            modifier = Modifier.fillMaxWidth(),
        )

        AppContent(modifier = Modifier.padding(Style.Dimensions.paddingMedium).weight(1f), content = content)
    }
}

@Composable
private fun AppContent(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = modifier
    ) {
        Background(modifier = Modifier.fillMaxSize(), tint = Style.Colors.surface)

        Column(
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}

@Composable
private fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    appIcon: ImageVector? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .background(Style.Colors.surface),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(
                vertical = Style.Dimensions.paddingSmall,
                horizontal = Style.Dimensions.paddingLarge
            ).widthIn(max = 800.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppTitle(title = title, appIcon = appIcon, modifier = Modifier.weight(1f))

            Row(content = actions)
        }
    }
}

@Composable
private fun AppTitle(
    title: String,
    modifier: Modifier = Modifier,
    appIcon: ImageVector? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        appIcon?.let {
            Icon(
                imageVector = appIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .background(color = Color.White, shape = CircleShape)
                    .padding(4.dp),
                tint = Style.Colors.body
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}