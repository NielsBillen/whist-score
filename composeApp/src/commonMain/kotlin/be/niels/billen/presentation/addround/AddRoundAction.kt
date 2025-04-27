package be.niels.billen.presentation.addround

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round
import be.niels.billen.domain.RoundType

sealed interface AddRoundAction {
    data class SetRoundType(val roundType: RoundType) : AddRoundAction
    data class SelectPlayer(val player: PlayerId, val selected: Boolean) : AddRoundAction
    data class SetPlayers(val players: Set<PlayerId>) : AddRoundAction
    data class SetSlams(val slams: UInt) : AddRoundAction {
        init {
            require(slams in 0u..13u) { "the number of slams must be between [0..13]"}
        }
    }
    data class AddRound(val round: Round) : AddRoundAction
}