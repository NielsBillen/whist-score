package be.niels.billen.presentation.screens.addround.roundinput

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import be.niels.billen.domain.RoundType
import be.niels.billen.presentation.Style
import be.niels.billen.presentation.components.Selectable
import be.niels.billen.presentation.screens.addround.AddRoundAction
import be.niels.billen.presentation.screens.addround.AddRoundPanel

@Composable
fun RoundTypeInputScreen(
    modifier: Modifier = Modifier,
    initialRoundType: RoundType?,
    onAction: (AddRoundAction) -> Unit,
    onCancel: () -> Unit
) {
    var selectedRoundType by remember { mutableStateOf(initialRoundType) }

    AddRoundPanel(
        title = "Select round type",
        modifier = modifier,
        onBack = onCancel,
        onNext = { selectedRoundType?.let { onAction(AddRoundAction.SetRoundType(it)) } },
        nextEnabled = { selectedRoundType != null }) {


        LazyColumn(verticalArrangement = Arrangement.spacedBy(Style.Dimensions.paddingSmall)) {
            items(items = RoundType.entries) { type ->
                RoundTypeButton(type, selectedRoundType == type) {
                    selectedRoundType = type
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

private val RoundType.displayName: String
    get() = when (this) {
        RoundType.Regular -> "Regular"
        RoundType.Abandonce -> "Abandonce"
        RoundType.Misere -> "Misere"
        RoundType.Treble -> "Treble"
        RoundType.AbandonceInTrump -> "Abandonce in Trump"
        RoundType.OpenMisere -> "Open Misere"
        RoundType.SoloSlim -> "Solo Slim"
    }

private val RoundType.description: String
    get() = when (this) {
        RoundType.Regular -> "Two players try to achieve 8 slams together or one player tries to achieve 5 slams if no partner is found"
        RoundType.Abandonce -> "Players bids to achieve between 9 and 12 slams on their own and in the trump of their choice"
        RoundType.AbandonceInTrump -> "Players bid to achieve between 9 and 12 slams on their own in the dealt trump suit"
        RoundType.Misere -> "One player bids to achieve no slams"
        RoundType.Treble -> "The player with three aces teams up with the player with the last ace to achieve 8 slams"
        RoundType.OpenMisere -> "One player bids to achieve no slams with cards on the table"
        RoundType.SoloSlim -> "One player bids to achieve 13 slams"
    }