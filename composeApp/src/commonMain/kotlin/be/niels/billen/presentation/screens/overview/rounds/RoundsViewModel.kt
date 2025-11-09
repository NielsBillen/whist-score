package be.niels.billen.presentation.screens.overview.rounds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.data.repository.DefaultPlayerRepository
import be.niels.billen.domain.Rounds
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class RoundsViewModel(playersRepository: PlayerRepository, private val roundsRepository: RoundsRepository) :
    ViewModel() {
    val players = playersRepository.players.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap()
    )

    val rounds = roundsRepository.rounds.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Rounds.EMPTY
    )

    fun onAction(action: RoundsViewAction) {
        when (action) {
            is RoundsViewAction.DeleteRound -> deleteRound(action)
        }
    }

    private fun deleteRound(action: RoundsViewAction.DeleteRound) {
        roundsRepository.update {
            if (action.index in it.indices) {
                it.removeAt(action.index)
            } else {
                it
            }
        }
    }
}