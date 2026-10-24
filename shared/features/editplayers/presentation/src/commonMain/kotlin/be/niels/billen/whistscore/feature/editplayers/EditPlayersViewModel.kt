package be.niels.billen.whistscore.feature.editplayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.players.PlayerRepository
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class EditPlayersViewModel(private val playersRepository: PlayerRepository) : ViewModel() {
    val players = playersRepository.players.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = persistentHashMapOf()
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
                else -> it.putting(action.playerId, oldPlayer.copy(name = action.name))
            }
        }
    }
}