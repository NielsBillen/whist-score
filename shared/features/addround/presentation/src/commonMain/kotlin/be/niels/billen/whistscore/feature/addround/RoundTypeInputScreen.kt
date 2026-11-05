package be.niels.billen.whistscore.feature.addround

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridConfigurationScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import be.niels.billen.whistscore.feature.core.RoundType
import be.niels.billen.whistscore.feature.ui.ListItem
import be.niels.billen.whistscore.feature.ui.SelectableCard
import be.niels.billen.whistscore.feature.ui.Style
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RoundTypeInputScreen(
    modifier: Modifier = Modifier,
    viewModel: AddRoundViewModel = koinViewModel(),
    onRoundTypeSelected: (RoundType) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    RoundTypeInputScreen(
        selectedRoundType = state.roundType,
        modifier = modifier,
        onRoundTypeSelected = onRoundTypeSelected,
    )
}

@OptIn(ExperimentalGridApi::class)
@Composable
internal fun RoundTypeInputScreen(
    selectedRoundType: RoundType?,
    modifier: Modifier = Modifier,
    onRoundTypeSelected: (RoundType) -> Unit,
) {
    val scrollState = rememberScrollState()

    AddRoundPanel(
        title = "Select round type",
        modifier = modifier
    ) {
        Grid(
            config = {
                column(1.fr)
                gap(Style.Dimensions.paddingSmall)
                column(1.fr)
            },
            modifier =  Modifier.verticalScroll(scrollState),
        ) {
            RoundType.entries.forEachIndexed { index, roundType ->
                RoundTypeButton(
                    index = index,
                    roundType = roundType,
                    selected = selectedRoundType == roundType,
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        onRoundTypeSelected(roundType)
                    }
                )
            }
        }
    }
}

@Composable
private fun RoundTypeButton(
    index: Int,
    roundType: RoundType,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    SelectableCard(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
    ) {
        ListItem(
            label = "${index + 1}. ${roundType.displayName}",
            description = roundType.description,
            modifier = Modifier.padding(Style.Dimensions.paddingLarge)
        )
    }
}
