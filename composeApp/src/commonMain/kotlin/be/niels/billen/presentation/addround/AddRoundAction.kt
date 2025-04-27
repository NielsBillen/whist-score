package be.niels.billen.presentation.addround

import be.niels.billen.domain.RoundType

sealed interface AddRoundAction {
    data class SetRoundType(val roundType: RoundType) : AddRoundAction
}