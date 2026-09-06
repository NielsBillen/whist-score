package be.niels.billen.presentation.app

import be.niels.billen.domain.Round

sealed interface AppAction {
    object ResetGame : AppAction

    data class AddRound(val round: Round) : AppAction
}
