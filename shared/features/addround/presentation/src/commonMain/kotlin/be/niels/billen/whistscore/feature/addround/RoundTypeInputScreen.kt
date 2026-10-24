package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import be.niels.billen.whistscore.feature.core.RoundType
import be.niels.billen.whistscore.feature.core.Selectable
import be.niels.billen.whistscore.feature.core.Style

@Composable
fun RoundTypeInputScreen(
    modifier: Modifier = Modifier,
    selectedRoundType: RoundType?,
    onRoundTypeSelected: (RoundType) -> Unit,
) {
    AddRoundPanel(
        title = "Select round type",
        modifier = modifier,
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)) {
            items(items = RoundType.entries) { type ->
                RoundTypeButton(type, selectedRoundType == type) {
                    onRoundTypeSelected(type)
                }
            }
        }
    }
}

@Composable
private fun RoundTypeButton(roundType: RoundType, selected: Boolean, onClick: () -> Unit) {
    Selectable(selected, onClick, Modifier.fillMaxWidth()) {
        Column(
            Modifier.padding(Style.Dimensions.paddingLarge),
            verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)
        ) {
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(roundType.displayName)
                }
                append(" - ")
                append(roundType.description)
            })
        }
    }
}
