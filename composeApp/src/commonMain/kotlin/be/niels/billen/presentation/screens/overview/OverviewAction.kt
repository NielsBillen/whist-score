package be.niels.billen.presentation.screens.overview

import be.niels.billen.domain.Round
import be.niels.billen.presentation.app.AppScreen

sealed interface OverviewAction {
    object ResetGame : OverviewAction
}