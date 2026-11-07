package be.niels.billen.whistscore.feature.addround

import androidx.compose.runtime.Immutable
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import be.niels.billen.whistscore.feature.core.Round.Abandonce
import be.niels.billen.whistscore.feature.core.Round.Misere
import be.niels.billen.whistscore.feature.core.Round.OpenMisere
import be.niels.billen.whistscore.feature.core.Round.PassRound
import be.niels.billen.whistscore.feature.core.Round.Regular
import be.niels.billen.whistscore.feature.core.Round.SoloSlim
import be.niels.billen.whistscore.feature.core.Round.Treble
import be.niels.billen.whistscore.feature.core.RoundType
import be.niels.billen.whistscore.feature.core.WinCondition
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
            RoundType.Regular -> {
                if (slams == null) null else Regular(players, slams)
            }

            RoundType.Treble -> {
                if (slams == null) null else Treble(players, slams)
            }

            RoundType.Abandonce, RoundType.AbandonceInTrump -> {
                if (bidAchieved == null) {
                    null
                } else {
                    Abandonce(
                        playerId = players.first(),
                        playerWon = bidAchieved,
                    )
                }
            }

            RoundType.Misere -> {
                if (bidAchieved == null) null else Misere(players.first(), bidAchieved)
            }

            RoundType.OpenMisere -> {
                if (bidAchieved == null) null else OpenMisere(players.first(), bidAchieved)
            }

            RoundType.SoloSlim -> {
                if (bidAchieved == null) null else SoloSlim(players.first(), bidAchieved)
            }
        }
    }

    // The screen the flow is waiting on: each incomplete input blocks on its own screen,
    // and a complete round lands on the summary.
    val screen: AddRoundScreen
        get() =
            when {
                roundType == null -> {
                    AddRoundScreen.SELECT_ROUND_TYPE
                }

                roundType == RoundType.Pass -> {
                    AddRoundScreen.SUMMARY
                }

                players.isEmpty() -> {
                    AddRoundScreen.SELECT_PLAYERS
                }

                else -> {
                    when (roundType.winCondition) {
                        WinCondition.SLAMS -> {
                            if (slams == null) AddRoundScreen.SELECT_SLAMS else AddRoundScreen.SUMMARY
                        }

                        WinCondition.BID_ACHIEVED -> {
                            if (bidAchieved == null) AddRoundScreen.SELECT_BID_ACHIEVED else AddRoundScreen.SUMMARY
                        }

                        WinCondition.NONE -> {
                            AddRoundScreen.SUMMARY
                        }
                    }
                }
            }
}
