package be.niels.billen.domain

import kotlin.math.abs

sealed interface Round {
    val players: Set<PlayerId>
    val requiredSlams: Int
    val slams: Int
    val baseScore: Int
    val overScoreMultiplier: Int

    fun points(player: PlayerId) =
        if (isWinner(player))
            basePoints * loserCount / winnerCount
        else
            -basePoints


    val winnerCount: Int
        get() = if (slams >= requiredSlams) players.size else PlayerId.entries.size - players.size

    val loserCount: Int
        get() = if (slams < requiredSlams) players.size else PlayerId.entries.size - players.size

    fun isWinner(playerId: PlayerId) =
        if (requiredSlams >= slams) players.contains(playerId) else !players.contains(playerId)

    private val basePoints: Int
        get() = baseScore + abs(slams - requiredSlams) * overScoreMultiplier


    data class Regular(override val players: Set<PlayerId>, override val slams: Int = 0) : Round {
        override val requiredSlams = if (players.size == 1) 5 else 8
        override val baseScore = 2
        override val overScoreMultiplier = 1

        init {
            require(players.isNotEmpty()) { "there must be at least one winner" }
            require(players.size < 3) { "there can be at most two winners" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }
    }

    data class Abandonce(override val players: Set<PlayerId>, override val slams: Int = 0) :
        Round {
        init {
            require(players.size == 1) { "Abandonce is a single player round" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }

        override val requiredSlams = 9
        override val baseScore = 3
        override val overScoreMultiplier = 0
    }

    data class Misere(override val players: Set<PlayerId>, override val slams: Int = 0) :
        Round {
        init {
            require(players.size == 1) { "Misere is a single player round" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }

        override val requiredSlams = 0
        override val baseScore = 5
        override val overScoreMultiplier = 0
    }

    data class Treble(override val players: Set<PlayerId>, override val slams: Int = 0) :
        Round {
        init {
            require(players.size == 2) { "Treble is a two player round" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }

        override val requiredSlams = 8
        override val baseScore = 4
        override val overScoreMultiplier = 2
    }
}

