package be.niels.billen.whistscore.domain.repository

import be.niels.billen.whistscore.domain.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val game: Flow<Game>

    fun update(transform: (Game) -> Game)
}