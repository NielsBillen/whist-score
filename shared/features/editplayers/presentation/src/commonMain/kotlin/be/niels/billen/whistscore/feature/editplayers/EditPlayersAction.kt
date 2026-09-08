package be.niels.billen.whistscore.feature.editplayers

import be.niels.billen.whistscore.feature.core.PlayerId

sealed interface EditPlayersAction {
    data class ChangeName(val playerId: PlayerId, val name: String) : EditPlayersAction
}