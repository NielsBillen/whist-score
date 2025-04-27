package be.niels.billen.presentation.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.domain.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class PlayersViewModel(gameRepository: GameRepository) : ViewModel() {
    val players = gameRepository.game.map { game ->

        game.players.map { (id, player) -> PlayerView(name = player.name, color = player.color,game.score(id) ) }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
}