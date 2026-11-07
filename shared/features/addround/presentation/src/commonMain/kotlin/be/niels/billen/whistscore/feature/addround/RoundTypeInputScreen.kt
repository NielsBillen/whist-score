package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import be.niels.billen.whistscore.feature.core.RoundType
import be.niels.billen.whistscore.feature.ui.ListItem
import be.niels.billen.whistscore.feature.ui.SelectableCard
import be.niels.billen.whistscore.feature.ui.SelectableCardState
import be.niels.billen.whistscore.feature.ui.Style
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RoundTypeInputScreen(
    onRoundTypeChange: (RoundType) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    RoundTypeInputScreen(
        selectedRoundType = state.roundType,
        onRoundTypeChange = onRoundTypeChange,
        modifier = modifier,
    )
}

@OptIn(ExperimentalGridApi::class)
@Composable
internal fun RoundTypeInputScreen(
    selectedRoundType: RoundType?,
    onRoundTypeChange: (RoundType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    AddRoundPanel(
        title = "Select round type",
        modifier = modifier,
    ) {
        Grid(
            config = {
                column(1.fr)
                gap(Style.Dimensions.paddingSmall)
                column(1.fr)
            },
            modifier = Modifier.verticalScroll(scrollState),
        ) {
            RoundType.entries.forEach { roundType ->
                RoundTypeButton(
                    roundType = roundType,
                    selected = selectedRoundType == roundType,
                    onClick = {
                        onRoundTypeChange(roundType)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun RoundTypeButton(
    roundType: RoundType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SelectableCard(
        state = SelectableCardState(selected = selected),
        onClick = onClick,
        modifier = modifier,
    ) {
        ListItem(
            label = roundType.displayName,
            description = roundType.description,
            modifier = Modifier.padding(Style.Dimensions.paddingLarge),
        )
    }
}
