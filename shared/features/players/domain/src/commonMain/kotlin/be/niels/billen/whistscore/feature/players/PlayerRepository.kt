package be.niels.billen.whistscore.feature.players

import be.niels.billen.whistscore.feature.core.Player
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.Players
import kotlinx.collections.immutable.PersistentMap
import kotlinx.coroutines.flow.Flow


interface PlayerRepository {
    val players : Flow<Players>

    fun update(transform: (PersistentMap<PlayerId, Player>) -> Players)
}