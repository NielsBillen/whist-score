package be.niels.billen.whistscore.feature.game

import kotlinx.coroutines.flow.Flow

interface GameRepository {
    val game: Flow<Game>

    fun update(transform: (Game) -> Game)
}