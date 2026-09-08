package be.niels.billen.whistscore.feature.rounds

import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Round

data class Rounds(
    val rounds: List<Round> = emptyList(),
) {
    private val scores by lazy { scores() }

    fun isEmpty() = rounds.isEmpty()

    fun isPassRound(index: Int) = index in rounds.indices && rounds[index] is Round.PassRound

    operator fun plus(round: Round) = copy(rounds = rounds + round)

    fun removeAt(index: Int): Rounds =
        if (index in rounds.indices) copy(rounds = rounds.filterIndexed { i, _ -> i != index }) else this

    fun score(playerId: PlayerId) = scores.getOrElse(playerId) { 0 }

    fun scoreSnapshot(): Map<PlayerId, Int> = scores

    private fun scores(): Map<PlayerId, Int> =
        PlayerId.entries.associateWith { playerId ->
            rounds.foldIndexed(0) { index, running, round ->
                running + round.points(playerId = playerId, passRound = isPassRound(index))
            }
        }

    companion object {
        val EMPTY = Rounds()
    }
}
