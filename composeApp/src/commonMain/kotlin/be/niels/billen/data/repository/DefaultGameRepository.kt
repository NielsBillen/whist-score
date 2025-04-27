package be.niels.billen.data.repository

import be.niels.billen.domain.Game
import be.niels.billen.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultGameRepository : GameRepository {
    private val _game = MutableStateFlow(Game())

    override val game: Flow<Game> = _game.asStateFlow()

    override fun update(transform: (Game) -> Game) {
        _game.update(transform)
    }
}