package be.niels.billen.whistscore.domain.repository

import be.niels.billen.whistscore.domain.Players
import kotlinx.coroutines.flow.Flow


interface PlayerRepository {
    val players : Flow<Players>

    fun update(transform: (Players) -> Players)
}