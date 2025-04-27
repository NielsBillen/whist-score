package be.niels.billen.presentation.addround

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round
import be.niels.billen.domain.RoundType

data class AddRoundState(
    val roundType: RoundType? = null,
    val players: Set<PlayerId> = emptySet(),
    val slams: UInt? = null
) {
    fun setRoundType(roundType: RoundType) = AddRoundState(roundType = roundType)
    fun setPlayerSelected(player: PlayerId, selected: Boolean): AddRoundState =
        setPlayers(players = if (selected) players + player else players - player)

    fun setPlayers(players: Set<PlayerId>): AddRoundState =
        copy(players = players)

    fun setSlams(slams: UInt): AddRoundState =
        copy(slams = slams)

    val isValid: Boolean
        get() = roundType != null && players.isNotEmpty() && slams in 0u..13u

    val round: Round? by lazy {
        if (roundType == null || slams == null || players.isEmpty()) return@lazy null

        return@lazy when (roundType) {
            RoundType.Regular -> Round.Regular(players, slams)
            RoundType.Abandonce -> Round.Abandonce(players.first(), slams)
        }
    }
}
