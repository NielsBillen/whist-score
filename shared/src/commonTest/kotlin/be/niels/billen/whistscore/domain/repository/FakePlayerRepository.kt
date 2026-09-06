package be.niels.billen.whistscore.domain.repository

import androidx.compose.ui.graphics.Color
import be.niels.billen.whistscore.domain.Game
import be.niels.billen.whistscore.domain.Player
import be.niels.billen.whistscore.domain.PlayerId
import be.niels.billen.whistscore.domain.Players
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakePlayerRepository(
    initial: Players = Game.DEFAULT_PLAYERS,
) : PlayerRepository {
    private val _players = MutableStateFlow(initial)
    override val players = _players.asStateFlow()

    override fun update(transform: (Players) -> Players) = _players.update(transform)
}
