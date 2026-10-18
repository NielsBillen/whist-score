package be.niels.billen.whistscore.feature.addround

import be.niels.billen.whistscore.feature.core.RoundType

object AddRoundNavigator {

    fun next(screen: AddRoundScreen, roundType: RoundType?): AddRoundScreen =
        roundType?.screens?.nextAfter(screen) ?: AddRoundScreen.SELECT_ROUND_TYPE

    fun previous(screen: AddRoundScreen, roundType: RoundType?): AddRoundScreen =
        roundType?.screens?.previousBefore(screen) ?: AddRoundScreen.SELECT_ROUND_TYPE

    private fun List<AddRoundScreen>.nextAfter(screen: AddRoundScreen): AddRoundScreen =
        this[(indexOf(screen) + 1).coerceAtMost(lastIndex)]

    private fun List<AddRoundScreen>.previousBefore(screen: AddRoundScreen): AddRoundScreen =
        this[(indexOf(screen) - 1).coerceAtLeast(0)]


}

private val RoundType.screens: List<AddRoundScreen>
    get() = when (this) {
        RoundType.Pass -> PASS_ROUND_SCREENS
        RoundType.Regular, RoundType.Treble -> SLAM_ROUND_SCREENS
        RoundType.Abandonce, RoundType.AbandonceInTrump, RoundType.Misere, RoundType.OpenMisere, RoundType.SoloSlim -> PASS_FAIL_ROUND_SCREENS
    }

private val PASS_ROUND_SCREENS = listOf(AddRoundScreen.SELECT_ROUND_TYPE, AddRoundScreen.SUMMARY)
private val SLAM_ROUND_SCREENS = listOf(
    AddRoundScreen.SELECT_ROUND_TYPE,
    AddRoundScreen.SELECT_PLAYERS,
    AddRoundScreen.SELECT_SLAMS,
    AddRoundScreen.SUMMARY
)

private val PASS_FAIL_ROUND_SCREENS = listOf(
    AddRoundScreen.SELECT_ROUND_TYPE,
    AddRoundScreen.SELECT_PLAYERS,
    AddRoundScreen.SELECT_BID_ACHIEVED,
    AddRoundScreen.SUMMARY
)