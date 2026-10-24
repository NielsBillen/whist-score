package be.niels.billen.whistscore.feature.addround

import androidx.compose.runtime.Immutable
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.Round.*
import be.niels.billen.whistscore.feature.core.RoundType
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf

@Immutable
data class AddRoundState(
    val roundType: RoundType? = null,
    val players: PersistentSet<PlayerId> = persistentSetOf(),
    val bidAchieved: Boolean? = null,
    val slams: Int? = null,
) {
    val round: Round? by lazy {
        if (roundType == null) return@lazy null
        if (roundType == RoundType.Pass) return@lazy PassRound
        if (players.isEmpty()) return@lazy null

        return@lazy when (roundType) {
            RoundType.Regular -> if (slams == null) null else Regular(players, slams)
            RoundType.Treble -> if (slams == null) null else Treble(players, slams)
            RoundType.Abandonce, RoundType.AbandonceInTrump -> if (bidAchieved == null) null else Abandonce(
                playerId = players.first(), playerWon = bidAchieved
            )

            RoundType.Misere -> if (bidAchieved == null) null else Misere(players.first(), bidAchieved)
            RoundType.OpenMisere -> if (bidAchieved == null) null else OpenMisere(players.first(), bidAchieved)
            RoundType.SoloSlim -> if (bidAchieved == null) null else SoloSlim(players.first(), bidAchieved)
        }
    }
}
