package be.niels.billen.presentation.app

import be.niels.billen.domain.Round

enum class AppScreen {
    OVERVIEW,
    ADD_ROUND
}

sealed interface AppAction {
    object ResetGame : AppAction
    data class Navigate(val screen: AppScreen) : AppAction
    data class AddRound(val round: Round) : AppAction
}