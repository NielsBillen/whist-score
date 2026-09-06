package be.niels.billen.whistscore.presentation.app

import be.niels.billen.whistscore.domain.Round

sealed interface AppAction {
    object ResetGame : AppAction

    data class AddRound(val round: Round) : AppAction
}
