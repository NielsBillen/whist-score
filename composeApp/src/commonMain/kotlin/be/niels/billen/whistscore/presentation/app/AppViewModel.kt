package be.niels.billen.whistscore.presentation.app

import androidx.lifecycle.ViewModel
import be.niels.billen.whistscore.domain.Game
import be.niels.billen.whistscore.domain.repository.GameRepository

class AppViewModel(private val gameRepository: GameRepository) : ViewModel() {

    fun onAction(action: AppAction) {
        when (action) {
            is AppAction.AddRound -> addRound(action)
            AppAction.ResetGame -> clearRounds()
        }
    }

    private fun addRound(action: AppAction.AddRound) {
        gameRepository.update { it + action.round }
    }

    private fun clearRounds() {
        gameRepository.update(Game::clearRounds)
    }
}
