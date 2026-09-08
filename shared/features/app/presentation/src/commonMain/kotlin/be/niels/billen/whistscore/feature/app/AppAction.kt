package be.niels.billen.whistscore.feature.app

import be.niels.billen.whistscore.feature.core.Round

sealed interface AppAction {
    object ResetGame : AppAction

    data class AddRound(val round: Round) : AppAction
}
