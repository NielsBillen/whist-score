package be.niels.billen.whistscore.feature.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.game.GameRepository
import kotlinx.coroutines.flow.*

class PlayersViewModel(
    gameRepository: GameRepository,
) : ViewModel() {
    val players = gameRepository.game.map { game ->
        PlayerId.entries.map { playerId ->
            val player = game.players.getValue(playerId)
            PlayerView(name = player.name, color = player.color, score = game.score(playerId = playerId))
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
}
