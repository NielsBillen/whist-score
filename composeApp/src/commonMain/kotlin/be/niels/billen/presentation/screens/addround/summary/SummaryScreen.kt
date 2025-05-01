package be.niels.billen.presentation.screens.addround.summary

import androidx.compose.runtime.Composable
import be.niels.billen.domain.Round
import be.niels.billen.presentation.screens.addround.AddRoundPanel

@Composable
fun SummaryScreen(
    round: Round,
    onBack: () -> Unit,
    onNext: () -> Unit,
) {
    AddRoundPanel(
        title = "Summary",
        onBack = onBack,
        onNext = onNext,
        nextEnabled = { true}
    ) {
    }
}