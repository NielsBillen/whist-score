package be.niels.billen.whistscore.feature.players

import be.niels.billen.whistscore.feature.core.DEFAULT_PLAYERS
import be.niels.billen.whistscore.feature.core.Players
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakePlayerRepository(
    initial: Players = DEFAULT_PLAYERS,
) : PlayerRepository {
    private val _players = MutableStateFlow(initial)
    override val players = _players.asStateFlow()

    override fun update(transform: (Players) -> Players) = _players.update(transform)
}
