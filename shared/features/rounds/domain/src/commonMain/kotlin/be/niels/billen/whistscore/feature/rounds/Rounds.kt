package be.niels.billen.whistscore.feature.rounds

import androidx.compose.runtime.Immutable
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round
import kotlinx.collections.immutable.*

@Immutable
data class Rounds(
    val rounds: PersistentList<Round> = persistentListOf(),
) : List<Round> by rounds {
    private val scores by lazy { scores() }

    operator fun plus(round: Round) = Rounds(rounds + round)

    fun removeAt(index: Int): Rounds =
        if (index in rounds.indices) Rounds(rounds.filterIndexed { i, _ -> i != index }.toPersistentList()) else this

    fun points(index: Int, playerId: PlayerId): Int =
        rounds[index].points(playerId) * multiplierAt(index)

    fun score(playerId: PlayerId) = scores.getOrElse(playerId) { 0 }

    private fun multiplierAt(index: Int): Int {
        if (index == 0 || index !in rounds.indices) return 1
        return rounds[index - 1].multiplier
    }

    private fun scores(): ImmutableMap<PlayerId, Int> =
        PlayerId.entries.associateWith { playerId ->
            rounds.foldIndexed(0) { index, running, _ ->
                running + points(index, playerId)
            }
        }.toImmutableMap()

    companion object {
        val EMPTY = Rounds()
    }
}

private val Round.multiplier: Int
    get() = when (this) {
        is Round.PassRound -> 2
        is Round.Regular,
        is Round.Treble,
        is Round.Abandonce,
        is Round.Misere,
        is Round.OpenMisere,
        is Round.SoloSlim -> 1
    }