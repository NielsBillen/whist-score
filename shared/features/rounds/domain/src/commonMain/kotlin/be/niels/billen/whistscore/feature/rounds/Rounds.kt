package be.niels.billen.whistscore.feature.rounds

import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round

data class Rounds(
    val rounds: List<Round> = emptyList(),
) : List<Round> by rounds {
    private val scores by lazy { scores() }

    operator fun plus(round: Round) = Rounds(rounds + round)

    fun removeAt(index: Int): Rounds =
        if (index in rounds.indices) Rounds(rounds.filterIndexed { i, _ -> i != index }) else this

    /** The points the round at [index] contributes to [playerId]. */
    fun points(index: Int, playerId: PlayerId): Int =
        rounds[index].points(playerId) * if (index > 0 && rounds[index - 1] is Round.PassRound) 2 else 1

    fun score(playerId: PlayerId) = scores.getOrElse(playerId) { 0 }

    fun scoreSnapshot(): Map<PlayerId, Int> = scores

    private fun scores(): Map<PlayerId, Int> =
        PlayerId.entries.associateWith { playerId ->
            rounds.foldIndexed(0) { index, running, _ ->
                running + points(index, playerId)
            }
        }

    companion object {
        val EMPTY = Rounds()
    }
}
