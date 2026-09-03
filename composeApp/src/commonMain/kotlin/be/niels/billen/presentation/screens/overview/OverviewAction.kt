package be.niels.billen.presentation.screens.overview

sealed interface OverviewAction {
    object ResetGame : OverviewAction
}