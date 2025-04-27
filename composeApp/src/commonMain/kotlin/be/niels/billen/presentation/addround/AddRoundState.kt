package be.niels.billen.presentation.addround

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.RoundType

data class AddRoundState(val roundType: RoundType? = null, val players: Set<PlayerId> = emptySet()) {
    fun setRoundType(roundType: RoundType) = AddRoundState(roundType = roundType)
}
