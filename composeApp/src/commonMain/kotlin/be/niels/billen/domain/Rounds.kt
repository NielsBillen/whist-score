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
        copy(rounds = rounds + round, scores = scores(rounds + round))

    fun removeAt(index: Int): Rounds {
        val remaining = rounds.filterIndexed { i, _ -> i != index }
        return copy(rounds = remaining, scores = scores(remaining))
    }

    fun score(playerId: PlayerId) = scores.getOrElse(playerId) { 0 }

    companion object {
        val EMPTY = Rounds()

        fun of(rounds: List<Round>): Rounds =
            Rounds(rounds = rounds, scores = scores(rounds))
    }
}

private fun scores(rounds: List<Round>) = PlayerId.entries.associateWith { player ->
    rounds.foldIndexed(0) { index, running, round ->
        running + round.points(player) * multiplier(rounds, index)
    }
}

private fun multiplier(rounds: List<Round>, index: Int) =
    if (index == 0) 1 else if (rounds[index - 1].passRound) 2 else 1
