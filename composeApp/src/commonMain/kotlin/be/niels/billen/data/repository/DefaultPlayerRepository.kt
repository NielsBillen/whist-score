package be.niels.billen.data.repository

import androidx.compose.ui.graphics.Color
import be.niels.billen.domain.Player
import be.niels.billen.domain.PlayerId
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.Players
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultPlayerRepository : PlayerRepository {
    private val _players = MutableStateFlow(defaultPlayers)
    override val players = _players.asStateFlow()

    override fun update(transform: (Players) -> Players) {
        _players.update(transform)
    }

    companion object {
        val defaultPlayers = mapOf(
            PlayerId.Player1 to Player("Player 1", Color(0xFFFFB703)),
            PlayerId.Player2 to Player("Player 2", Color(0xFF219EBC)),
            PlayerId.Player3 to Player("Player 3", Color(0xFF8ECAE6)),
            PlayerId.Player4 to Player("Player 4", Color(0xFFFB8500))
        )
    }
}