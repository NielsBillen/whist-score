package be.niels.billen.presentation.screens.addround

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.RoundType

sealed interface AddRoundAction {
    data class SetRoundType(val roundType: RoundType) : AddRoundAction
    data class SetPlayers(val players: Set<PlayerId>) : AddRoundAction
    data class SetSlams(val slams: Int) : AddRoundAction {
        init {
            require(slams in 0..13) { "the number of slams must be between [0..13]" }
        }
    }

    data class SetBid(val bid: Int) : AddRoundAction {
        init {
            require(bid in 9..13) { "the bid of slams must be between [9..13]" }
        }
    }

    data object PreviousScreen: AddRoundAction
}