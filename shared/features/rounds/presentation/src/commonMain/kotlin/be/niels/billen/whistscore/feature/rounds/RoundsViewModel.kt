package be.niels.billen.whistscore.feature.rounds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.core.DEFAULT_PLAYERS
import be.niels.billen.whistscore.feature.game.Game
import be.niels.billen.whistscore.feature.game.GameRepository
import be.niels.billen.whistscore.feature.players.PlayerRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class RoundsViewModel(
    private val gameRepository: GameRepository,
    playerRepository: PlayerRepository
) :
    ViewModel() {
    val game = gameRepository.game.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Game.DEFAULT
    )


    val players = playerRepository.players.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = DEFAULT_PLAYERS
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