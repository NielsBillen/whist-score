package be.niels.billen.presentation.screens.overview.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.domain.Game
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.*
import kotlin.math.round

class PlayersViewModel(
    playersRepository: PlayerRepository,
    roundsRepository: RoundsRepository
) : ViewModel() {
    val players = combine(playersRepository.players, roundsRepository.rounds) { players, rounds ->
        players.map { (id, player) ->
            PlayerView(name = player.name, color = player.color, score = rounds.score(id))
        }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
}
