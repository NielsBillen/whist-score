package be.niels.billen.domain

import kotlin.math.abs

sealed interface Round {

    fun points(player: PlayerId): Int


    fun isWinner(playerId: PlayerId): Boolean


    sealed interface MultiPlayerRound : Round {
        val players: Set<PlayerId>
        val playersWon: Boolean

        val winnerCount: Int
            get() = if (playersWon) players.size else PlayerId.entries.size - players.size

        val loserCount: Int
            get() = if (playersWon) PlayerId.entries.size - players.size else players.size

        override fun isWinner(playerId: PlayerId) =
            if (playersWon) players.contains(playerId) else !players.contains(playerId)
    }

    sealed interface SinglePlayerRound : Round {
        val player: PlayerId
        val playerWon: Boolean

        val penaltyPoints: Int

        override fun isWinner(playerId: PlayerId) =
            if (playerWon) this.player == player else this.player != player

        override fun points(player: PlayerId): Int =
            if (this.player == player) {
                if (playerWon) 3 * penaltyPoints else -penaltyPoints * 3
            } else {
                if (playerWon) -penaltyPoints else penaltyPoints
            }
    }

    data class Regular(override val players: Set<PlayerId>, val slams: Int = 0) : MultiPlayerRound {
        val requiredSlams = if (players.size == 1) 5 else 8
        override val playersWon = slams >= requiredSlams

        init {
            require(players.isNotEmpty()) { "there must be at least one winner" }
            require(players.size < 3) { "there can be at most two winners" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }

        override fun points(player: PlayerId) =
            if (isWinner(player))
                basePoints * loserCount / winnerCount
            else
                -basePoints

        private val basePoints: Int
            get() = 2 + abs(slams - requiredSlams)
    }

    data class Abandonce(
        override val player: PlayerId,
        override val playerWon: Boolean = true,
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 3
    }

    data class Misere(
        override val player: PlayerId,
        override val playerWon: Boolean = true,
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 5
    }

    data class OpenMisere(
        override val player: PlayerId,
        override val playerWon: Boolean = true,
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 10
    }

    data class SoloSlim(
        override val player: PlayerId,
        override val playerWon: Boolean = true,
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 15
    }


    data class Treble(override val players: Set<PlayerId>, val slams: Int = 0) : MultiPlayerRound {
        val requiredSlams = if (players.size == 1) 5 else 8
        override val playersWon = slams >= requiredSlams

        init {
            require(players.size == 2) { "Treble is played by two players" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }

        override fun points(player: PlayerId) =
            if (isWinner(player))
                basePoints * loserCount / winnerCount
            else
                -basePoints

        private val basePoints: Int
            get() = 4 + abs(slams - requiredSlams) * 2
    }
}

