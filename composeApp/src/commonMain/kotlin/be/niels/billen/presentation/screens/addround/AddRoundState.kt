package be.niels.billen.presentation.screens.addround

import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Round
import be.niels.billen.domain.Round.*
import be.niels.billen.domain.RoundType

data class AddRoundState(
    val screen: AddRoundScreen = AddRoundScreen.SELECT_ROUND_TYPE,
    val roundType: RoundType? = null,
    val bid: Int? = null,
    val players: Set<PlayerId> = emptySet(),
    val playerWon: Boolean? = null,
    val slams: Int? = null,
    val passRound: Boolean = false,
) {
    fun setRoundType(roundType: RoundType) = AddRoundState(roundType = roundType)

    fun setScreen(screen: AddRoundScreen) = copy(screen = screen)

    fun setPlayers(players: Set<PlayerId>) = copy(players = players)

    fun setSlams(slams: Int) = copy(slams = slams)

    fun setBid(slams: Int) = copy(bid = slams)

    fun setBidAchieved(bidAchieved: Boolean) = copy(playerWon = bidAchieved)

    fun setPassRound(passRound: Boolean) = copy(passRound = passRound)

    val round: Round? by lazy {
        if (roundType == null || players.isEmpty()) return@lazy null

        return@lazy when (roundType) {
            RoundType.Regular -> if (slams == null) null else Regular(players, slams, passRound)
            RoundType.Treble -> if (slams == null) null else Treble(players, slams, passRound)
            RoundType.Abandonce, RoundType.AbandonceInTrump -> if (playerWon == null) null else Abandonce(
                players.first(), playerWon, passRound
            )

            RoundType.Misere -> if (playerWon == null) null else Misere(players.first(), playerWon, passRound)
            RoundType.OpenMisere -> if (playerWon == null) null else OpenMisere(players.first(), playerWon, passRound)
            RoundType.SoloSlim -> if (playerWon == null) null else SoloSlim(players.first(), playerWon, passRound)
        }
    }
}
