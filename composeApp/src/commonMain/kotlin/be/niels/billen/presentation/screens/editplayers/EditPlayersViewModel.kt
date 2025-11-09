package be.niels.billen.presentation.screens.editplayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class EditPlayersViewModel(private val playersRepository: PlayerRepository) : ViewModel() {
    val players = playersRepository.players.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap()
    )

    fun onAction(action: EditPlayersAction) {
        when (action) {
            is EditPlayersAction.ChangeName -> changeName(action)
        }
    }

    private fun changeName(action: EditPlayersAction.ChangeName) {
        playersRepository.update {
            when (val oldPlayer = it[action.playerId]) {
                null -> it
                else -> it + (action.playerId to oldPlayer.copy(name = action.name))
            }
        }
    }
}