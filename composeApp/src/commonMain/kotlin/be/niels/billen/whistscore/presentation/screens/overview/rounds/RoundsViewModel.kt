package be.niels.billen.whistscore.presentation.screens.overview.rounds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.domain.Game
import be.niels.billen.whistscore.domain.repository.GameRepository
import be.niels.billen.whistscore.domain.repository.PlayerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RoundsViewModel(private val gameRepository: GameRepository) :
    ViewModel() {
    val game = gameRepository.game.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Game.DEFAULT
    )

    fun onAction(action: RoundsViewAction) {
        when (action) {
            is RoundsViewAction.DeleteRound -> deleteRound(action)
        }
    }

    private fun deleteRound(action: RoundsViewAction.DeleteRound) {
        gameRepository.update { it.removeAt(action.index) }
    }
}