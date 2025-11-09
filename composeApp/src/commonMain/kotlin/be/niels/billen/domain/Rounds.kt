package be.niels.billen.domain

@ConsistentCopyVisibility
data class Rounds private constructor(
    val rounds: List<Round> = emptyList(),
    val scores: Map<PlayerId, Int> = emptyMap()
) : List<Round> by rounds {
    init {
        require(scores.values.sum() == 0) { "sum of scores should be zero" }
    }

    operator fun plus(round: Round) = copy(rounds = rounds + round, scores = scores + round)

    fun removeAt(index: Int): Rounds {
        val round = rounds[index]
        val newRounds = rounds.filterIndexed { i, _ -> i != index }
        val newScores = scores - round

        return copy(rounds = newRounds, scores = newScores)
    }

    fun score(playerId: PlayerId) = scores.getOrElse(playerId) { 0 }

    companion object {
        val EMPTY = Rounds()

        fun of(rounds: List<Round>): Rounds {
            val scores =
                PlayerId.entries.associateWith { player -> rounds.fold(0) { score, round -> score + round.points(player) } }


            return Rounds(rounds = rounds, scores = scores)
        }
    }
}

private operator fun Map<PlayerId, Int>.plus(round: Round) = PlayerId.entries.associateWith { player ->
    getOrElse(player) { 0 } + round.points(player)
}

private operator fun Map<PlayerId, Int>.minus(round: Round) = PlayerId.entries.associateWith { player ->
    getOrElse(player) { 0 } - round.points(player)
}