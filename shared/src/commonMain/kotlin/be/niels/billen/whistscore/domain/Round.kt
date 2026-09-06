package be.niels.billen.whistscore.domain

import kotlin.math.abs

sealed interface Round {
    fun points(playerId: PlayerId, passRound: Boolean): Int = points(playerId) * passRound.passRoundMultiplier

    fun points(playerId: PlayerId): Int = 0

    fun won(playerId: PlayerId): Boolean

    data object PassRound : Round {

        override fun points(playerId: PlayerId) = 0

        override fun won(playerId: PlayerId) = false
    }

    sealed interface MultiPlayerRound : Round {
        val playerIds: Set<PlayerId>
        val playersWon: Boolean

        override fun won(playerId: PlayerId) = when (playersWon) {
            true -> playerId in playerIds
            else -> playerId !in playerIds
        }

        override fun points(playerId: PlayerId) =
            when (playerId in playerIds) {
                true -> (if (playersWon) basePoints else -basePoints) * (nonPlayerCount / playerIds.size)
                false -> if (playersWon) -basePoints else basePoints
            }

        private val nonPlayerCount: Int
            get() = PlayerId.entries.size - playerIds.size

        val basePoints: Int
    }

    sealed interface SinglePlayerRound : Round {
        val playerId: PlayerId
        val playerWon: Boolean
        val penaltyPoints: Int

        override fun won(playerId: PlayerId) =
            if (playerWon) this.playerId == playerId else this.playerId != playerId

        override fun points(playerId: PlayerId) = when (this.playerId == playerId) {
            true -> if (playerWon) 3 * penaltyPoints else -penaltyPoints * 3
            false -> if (playerWon) -penaltyPoints else penaltyPoints
        }
    }

    data class Regular(
        override val playerIds: Set<PlayerId>,
        val slams: Int = 0,
    ) : MultiPlayerRound {

        val requiredSlams = if (playerIds.size == 1) 5 else 8
        override val playersWon = slams >= requiredSlams

        init {
            require(playerIds.size in 1..2) { "the number of players must be between 1 and 2" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }


        override val basePoints: Int
            get() = 2 + abs(slams - requiredSlams)
    }

    data class Abandonce(
        override val playerId: PlayerId,
        override val playerWon: Boolean = true,
    ) : SinglePlayerRound {
        override val penaltyPoints = 3
    }

    data class Misere(
        override val playerId: PlayerId,
        override val playerWon: Boolean = true,
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 5
    }

    data class OpenMisere(
        override val playerId: PlayerId,
        override val playerWon: Boolean = true,
    ) :
        SinglePlayerRound {
        override val penaltyPoints = 10
    }

    data class SoloSlim(
        override val playerId: PlayerId,
        override val playerWon: Boolean = true,
    ) : SinglePlayerRound {
        override val penaltyPoints = 15
    }


    data class Treble(
        override val playerIds: Set<PlayerId>,
        val slams: Int = 0,
    ) : MultiPlayerRound {
        val requiredSlams = if (playerIds.size == 1) 5 else 8
        override val playersWon = slams >= requiredSlams

        init {
            require(playerIds.size == 2) { "Treble is played by two players" }
            require(slams in 0..13) { "the number of slams must be between 0 and 13 " }
        }

        override val basePoints: Int
            get() = 4 + abs(slams - requiredSlams) * 2
    }
}

private val Boolean.passRoundMultiplier: Int get() = if (this) 2 else 1
