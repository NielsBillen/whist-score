package be.niels.billen.domain

@ConsistentCopyVisibility
data class Rounds private constructor(
    val rounds: List<Round> = emptyList(),
    val scores: Map<PlayerId, Int> = emptyMap()
) : List<Round> by rounds {
    init {
        require(scores.values.sum() == 0) { "sum of scores should be zero" }
    }

    operator fun plus(round: Round) =
        copy(rounds = rounds + round, scores = scores + contribution(round, rounds.lastOrNull()?.passRound == true))

    fun removeAt(index: Int): Rounds {
        val removed = rounds[index]
        val previousPassed = index > 0 && rounds[index - 1].passRound
        val nextIndex = index + 1
        val nextExists = nextIndex < rounds.size

        val delta = PlayerId.entries.associateWith { player ->
            val removedDelta = -multiplier(previousPassed) * removed.points(player)
            val nextDelta =
                if (nextExists) {
                    (multiplier(previousPassed) - multiplier(removed.passRound)) * rounds[nextIndex].points(player)
                } else {
                    0
                }
            removedDelta + nextDelta
        }

        return copy(rounds = rounds.filterIndexed { i, _ -> i != index }, scores = scores - delta)
    }

    fun score(playerId: PlayerId) = scores.getOrElse(playerId) { 0 }

    companion object {
        val EMPTY = Rounds()

        fun of(rounds: List<Round>): Rounds =
            Rounds(rounds = rounds, scores = rounds.scores())
    }
}

private fun multiplier(passedPreviousRound: Boolean) = if (passedPreviousRound) 2 else 1

private fun contribution(round: Round, passedPreviousRound: Boolean): Map<PlayerId, Int> =
    PlayerId.entries.associateWith { multiplier(passedPreviousRound) * round.points(it) }

private operator fun Map<PlayerId, Int>.plus(delta: Map<PlayerId, Int>) =
    PlayerId.entries.associateWith { player -> getOrElse(player) { 0 } + delta.getValue(player) }

private operator fun Map<PlayerId, Int>.minus(delta: Map<PlayerId, Int>) =
    PlayerId.entries.associateWith { player -> getOrElse(player) { 0 } - delta.getValue(player) }

private fun List<Round>.scores(): Map<PlayerId, Int> =
    PlayerId.entries.associateWith { player ->
        foldIndexed(0) { index, running, round ->
            running + multiplier(index > 0 && this[index - 1].passRound) * round.points(player)
        }
    }
