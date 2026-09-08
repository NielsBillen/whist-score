package be.niels.billen.whistscore.feature.players

import be.niels.billen.whistscore.feature.core.Players
import kotlinx.coroutines.flow.Flow


interface PlayerRepository {
    val players : Flow<Players>

    fun update(transform: (Players) -> Players)
}