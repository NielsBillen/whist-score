package be.niels.billen.domain

sealed interface Round {

    fun points(player: PlayerId): Int

    data class Regular(val players: Set<PlayerId>, val slams: UInt = 0u) : Round {
        private val requiredSlams = if (players.size == 1) 5u else 8u
        private val score = 2u + if (slams > requiredSlams) slams - requiredSlams else requiredSlams - slams

        init {
            require(players.isNotEmpty()) { "there must be at least one winner" }
            require(players.size < 3) { "there can be at most two winners" }
            require(slams in 0u..13u) { "the number of slams must be between 0 and 13 " }
        }

        override fun points(player: PlayerId) =
            if (winner(player))
                score.toInt() * (if (players.size == 1) 3 else 1)
            else
                -score.toInt()


        private fun winner(playerId: PlayerId) = players.contains(playerId) && slams >= requiredSlams
    }

    data class Abandonce(val player: PlayerId, val slams: UInt = 0u) : Round {
        override fun points(player: PlayerId) =
            if (this.player == player)
                if (slams >= REQUIRED_SLAMS) 9 else -9
            else
                if (slams >= REQUIRED_SLAMS) -3 else 3

        companion object {
            const val REQUIRED_SLAMS = 9u
        }
    }
}

