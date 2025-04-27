package be.niels.billen.domain.repository

import be.niels.billen.domain.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val game : Flow<Game>

    fun update(transform: (Game) -> Game)
}