package be.niels.billen.whistscore.feature.addround

import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.RoundType

sealed interface AddRoundAction {
    data class SetRoundType(val roundType: RoundType) : AddRoundAction
    data class SetPlayers(val players: Set<PlayerId>) : AddRoundAction
    data class SetSlams(val slams: Int) : AddRoundAction {
        init {
            require(slams in 0..13) { "the number of slams must be between [0..13]" }
        }
    }

    data class SetBidAchieved(val bidAchieved: Boolean) : AddRoundAction
    data object PreviousScreen : AddRoundAction
}
