package be.niels.billen.whistscore.feature.core

import androidx.compose.runtime.Immutable

@Immutable
data class Players(val players: Map<PlayerId, Player>) : Map<PlayerId, Player> by players {
    constructor(vararg players: Pair<PlayerId, Player>) : this(mapOf(*players))

    operator fun plus(player: Pair<PlayerId, Player>) = copy(players = players + player)

    operator fun minus(playerId: PlayerId) = copy(players = players - playerId)

    companion object {
        val EMPTY = Players()

        operator fun invoke() = EMPTY
    }
}
