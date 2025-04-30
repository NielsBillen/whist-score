package be.niels.billen.presentation.addround

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round
import be.niels.billen.domain.RoundType

data class AddRoundState(
    val screen: AddRoundScreen = AddRoundScreen.SELECT_ROUND_TYPE,
    val roundType: RoundType? = null,
    val players: Set<PlayerId> = emptySet(),
    val slams: Int? = null
) {
    fun setRoundType(roundType: RoundType) = AddRoundState(roundType = roundType)

    fun setScreen(screen: AddRoundScreen) = copy(screen = screen)

    fun setPlayers(players: Set<PlayerId>): AddRoundState =
        copy(players = players)

    fun setSlams(slams: Int): AddRoundState =
        copy(slams = slams)

    val isValid: Boolean
        get() = roundType != null && players.isNotEmpty() && slams in 0..13

    val round: Round? by lazy {
        if (roundType == null || slams == null || players.isEmpty()) return@lazy null

        return@lazy when (roundType) {
            RoundType.Regular -> Round.Regular(players, slams)
            RoundType.Abandonce -> Round.Abandonce(players, slams)
            RoundType.Misere -> Round.Misere(players, slams)
            RoundType.Treble -> Round.Treble(players, slams)
        }
    }
}
