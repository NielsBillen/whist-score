package be.niels.billen.whistscore.domain

//data class Rounds(
//    val rounds: List<Round> = emptyList(),
//) : List<Round> by rounds {
//    private val scores by lazy { rounds.scores() }
//
//    operator fun plus(round: Round) = copy(rounds = rounds + round)
//
//    fun removeAt(index: Int): Rounds {
//        return copy(rounds = rounds.filterIndexed { i, _ -> i != index })
//    }
//
//    fun score(playerId: PlayerId) = scores.getOrElse(playerId) { 0 }
//
//    companion object {
//        val EMPTY = Rounds()
//    }
//}
//
//private fun multiplier(passedPreviousRound: Boolean) = if (passedPreviousRound) 2 else 1
//
//private operator fun Map<PlayerId, Int>.plus(delta: Map<PlayerId, Int>) =
//    PlayerId.entries.associateWith { player -> getOrElse(player) { 0 } + delta.getValue(player) }
//
//private operator fun Map<PlayerId, Int>.minus(delta: Map<PlayerId, Int>) =
//    PlayerId.entries.associateWith { player -> getOrElse(player) { 0 } - delta.getValue(player) }
//
//private fun List<Round>.scores(): Map<PlayerId, Int> =
//    PlayerId.entries.associateWith { player ->
//        foldIndexed(0) { index, running, round ->
//            running + multiplier(passedPreviousRound = index > 0 && this[index - 1] is Round.PassRound) * round.points(
//                player
//            )
//        }
//    }
