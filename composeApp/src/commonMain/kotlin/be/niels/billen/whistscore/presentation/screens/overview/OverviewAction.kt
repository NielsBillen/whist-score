package be.niels.billen.whistscore.presentation.screens.overview

sealed interface OverviewAction {
    object ResetGame : OverviewAction
}