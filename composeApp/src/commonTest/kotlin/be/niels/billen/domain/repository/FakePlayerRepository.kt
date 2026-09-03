package be.niels.billen.domain.repository

import androidx.compose.ui.graphics.Color
import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.Players
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakePlayerRepository(
    initial: Players = PlayerId.entries.associateWith {
        Player(name = "Player ${it.ordinal + 1}", color = Color.Black)
    },
) : PlayerRepository {
    private val _players = MutableStateFlow(initial)
    override val players = _players.asStateFlow()

    override fun update(transform: (Players) -> Players) = _players.update { transform(it) }
}
