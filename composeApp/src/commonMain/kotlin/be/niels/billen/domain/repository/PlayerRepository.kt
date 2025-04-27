package be.niels.billen.domain.repository

import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
import kotlinx.coroutines.flow.Flow

typealias Players = Map<PlayerId, Player>

interface PlayerRepository {
    val players : Flow<Players>

    fun update(transform: (Players) -> Players)
}