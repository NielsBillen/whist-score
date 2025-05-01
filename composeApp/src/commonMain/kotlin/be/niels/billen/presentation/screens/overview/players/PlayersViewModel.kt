package be.niels.billen.presentation.screens.overview.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.domain.Game
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.*

//class PlayersViewModel(gameRepository: GameRepository) : ViewModel() {
//    val players = gameRepository.game.map { game ->
//
//        game.players.map { (id, player) -> PlayerView(name = player.name, color = player.color,game.score(id) ) }
//    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
//}

class PlayersViewModel(playersRepository: PlayerRepository, roundsRepository: RoundsRepository) : ViewModel() {

    private val game: Flow<Game> = combine(playersRepository.players, roundsRepository.rounds) {
        players, rounds -> Game(players, rounds)
    }


    val players = game.map { game ->

        game.players.map { (id, player) -> PlayerView(name = player.name, color = player.color,game.score(id) ) }
    }.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = emptyList())
}