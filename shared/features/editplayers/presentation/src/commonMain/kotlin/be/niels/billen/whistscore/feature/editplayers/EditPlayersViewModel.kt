package be.niels.billen.whistscore.feature.editplayers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.game.Game
import be.niels.billen.whistscore.feature.game.GameRepository
import be.niels.billen.whistscore.feature.players.PlayerRepository
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EditPlayersViewModel(
    private val playersRepository: PlayerRepository,
    private val gameRepository: GameRepository
) : ViewModel() {
    val order = gameRepository.game.map { it.order }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Game.DEFAULT_ORDER
    )

    val players = playersRepository.players.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = persistentHashMapOf()
    )

    fun onAction(action: EditPlayersAction) {
        when (action) {
            is EditPlayersAction.ChangeName -> changeName(action)
            is EditPlayersAction.MoveDown -> moveDown(action)
            is EditPlayersAction.MoveUp -> moveUp(action)
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

    private fun moveUp(action: EditPlayersAction.MoveUp) {
        gameRepository.update {
            val index = it.order.indexOf(action.playerId)
            if (index <= 0) return@update it
            it.copy(order = it.order.move(index, index - 1))
        }
    }

    private fun moveDown(action: EditPlayersAction.MoveDown) {
        gameRepository.update {
            val index = it.order.indexOf(action.playerId)
            if (index >= PlayerId.entries.size - 1) return@update it
            it.copy(order = it.order.move(index, index + 1))

        }
    }
}

private fun <T> PersistentList<T>.move(from: Int, to: Int): PersistentList<T> {
    if (from == to || from !in indices || to !in indices) return this
    return removingAt(index = from).addingAt(index = to, this[from])
}