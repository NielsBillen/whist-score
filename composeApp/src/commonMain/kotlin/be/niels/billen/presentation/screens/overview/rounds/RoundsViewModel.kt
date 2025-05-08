package be.niels.billen.presentation.screens.overview.rounds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.data.repository.DefaultPlayerRepository
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class RoundsViewModel(playersRepository: PlayerRepository, roundsRepository: RoundsRepository) : ViewModel() {
    val players = playersRepository.players.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = DefaultPlayerRepository.defaultPlayers
    )

    val rounds = roundsRepository.rounds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

}