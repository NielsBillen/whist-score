package be.niels.billen.presentation.screens.editplayers

import be.niels.billen.domain.PlayerId

sealed interface EditPlayersAction {
    data class ChangeName(val playerId: PlayerId, val name: String) : EditPlayersAction
}