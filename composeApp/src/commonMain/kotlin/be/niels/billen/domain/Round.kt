package be.niels.billen.domain

import kotlin.math.abs

sealed interface Round {
    val passRound: Boolean

    fun points(player: PlayerId): Int

    fun won(playerId: PlayerId): Boolean

    val passRoundMultiplier: Int
        get() = if (passRound) 2 else 1

    sealed interface MultiPlayerRound : Round {
        val players: Set<PlayerId>
        val playersWon: Boolean

        override fun won(playerId: PlayerId) =
            if (playersWon) players.contains(playerId) else !players.contains(playerId)

        override fun points(player: PlayerId) = passRoundMultiplier * (if (players.contains(player)) {
            (if (playersWon) basePoints else -basePoints) * nonPlayerCount / players.size
        } else {
            if (playersWon) -basePoints else basePoints
        })

        private val nonPlayerCount: Int
            get() = PlayerId.entries.size - players.size

        val basePoints: Int
    }

    sealed interface SinglePlayerRound : Round {
        val player: PlayerId
        val playerWon: Boolean
        val penaltyPoints: Int

        override fun won(playerId: PlayerId) =
            if (playerWon) this.player == player else this.player != player

        override fun points(player: PlayerId): Int =
            passRoundMultiplier * (if (this.player == player) {
                if (playerWon) 3 * penaltyPoints else -penaltyPoints * 3
            } else {
                if (playerWon) -penaltyPoints else penaltyPoints
            })
    }

    data class Regular(
        override val players: Set<PlayerId>,
        val slams: Int = 0,
        override val passRound: Boolean = false
    ) : MultiPlayerRound {
        val requiredSlams = if (players.size == 1) 5 else 8
        override val playersWon = slams >= requiredSlams

        init {
            require(players.size in 1..2) { "the number of players must be between 1 and 2" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }


        override val basePoints: Int
            get() = 2 + abs(slams - requiredSlams)
    }

    data class Abandonce(
        override val player: PlayerId,
        override val playerWon: Boolean = true,
        override val passRound: Boolean = false
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 3
    }

    data class Misere(
        override val player: PlayerId,
        override val playerWon: Boolean = true,
        override val passRound: Boolean = false
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 5
    }

    data class OpenMisere(
        override val player: PlayerId,
        override val playerWon: Boolean = true,
        override val passRound: Boolean = false
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 10
    }

    data class SoloSlim(
        override val player: PlayerId,
        override val playerWon: Boolean = true,
        override val passRound: Boolean = false
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 15
    }


    data class Treble(
        override val players: Set<PlayerId>, val slams: Int = 0,
        override val passRound: Boolean = false
    ) : MultiPlayerRound {
        val requiredSlams = if (players.size == 1) 5 else 8
        override val playersWon = slams >= requiredSlams

        init {
            require(players.size == 2) { "Treble is played by two players" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }

        override val basePoints: Int
            get() = 4 + abs(slams - requiredSlams) * 2
    }
}

