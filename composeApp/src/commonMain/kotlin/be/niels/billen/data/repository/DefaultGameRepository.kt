package be.niels.billen.data.repository

import be.niels.billen.domain.Game
import be.niels.billen.domain.repository.GameRepository
import be.niels.billen.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultGameRepository(val playerRepository: PlayerRepository, val roundsRepository: DefaultRoundsRepository) : GameRepository {
    private val _game = MutableStateFlow(Game())
    override val game = _game.asStateFlow()

    override fun update(transform: (Game) -> Game) {
        _game.update(transform)
    }
}