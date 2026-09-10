package be.niels.billen.whistscore.feature.addround

import be.niels.billen.whistscore.feature.core.RoundType

class AddRoundNavigator {

    fun next(screen: AddRoundScreen, roundType: RoundType?): AddRoundScreen =
        roundType?.screens?.nextAfter(screen) ?: AddRoundScreen.SELECT_ROUND_TYPE

    fun previous(screen: AddRoundScreen, roundType: RoundType?): AddRoundScreen =
        roundType?.screens?.previousBefore(screen) ?: AddRoundScreen.SELECT_ROUND_TYPE

    private fun List<AddRoundScreen>.nextAfter(screen: AddRoundScreen): AddRoundScreen =
        this[(indexOf(screen) + 1).coerceAtMost(lastIndex)]

    private fun List<AddRoundScreen>.previousBefore(screen: AddRoundScreen): AddRoundScreen =
        this[(indexOf(screen) - 1).coerceAtLeast(0)]

    private val RoundType.screens: List<AddRoundScreen>
        get() = when (this) {
            RoundType.Pass -> listOf(AddRoundScreen.SELECT_ROUND_TYPE, AddRoundScreen.SUMMARY)

            RoundType.Regular, RoundType.Treble -> listOf(
                AddRoundScreen.SELECT_ROUND_TYPE,
                AddRoundScreen.SELECT_PLAYERS,
                AddRoundScreen.SELECT_SLAMS,
                AddRoundScreen.SUMMARY
            )

            RoundType.Abandonce, RoundType.AbandonceInTrump, RoundType.Misere, RoundType.OpenMisere, RoundType.SoloSlim -> listOf(
                AddRoundScreen.SELECT_ROUND_TYPE,
                AddRoundScreen.SELECT_PLAYERS,
                AddRoundScreen.SELECT_BID_ACHIEVED,
                AddRoundScreen.SUMMARY
            )
        }
}
