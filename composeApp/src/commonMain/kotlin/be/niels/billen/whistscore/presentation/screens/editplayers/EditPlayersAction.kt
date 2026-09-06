package be.niels.billen.whistscore.presentation.screens.editplayers

import be.niels.billen.whistscore.domain.PlayerId

sealed interface EditPlayersAction {
    data class ChangeName(val playerId: PlayerId, val name: String) : EditPlayersAction
}