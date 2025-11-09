package be.niels.billen.domain.repository

import be.niels.billen.domain.Players
import kotlinx.coroutines.flow.Flow


interface PlayerRepository {
    val players : Flow<Players>

    fun update(transform: (Players) -> Players)
}