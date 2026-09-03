package be.niels.billen.presentation.app

import androidx.lifecycle.ViewModel
import be.niels.billen.domain.Rounds
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.update

class AppViewModel(private val roundsRepository: RoundsRepository) : ViewModel() {

    fun onAction(action: AppAction) {
        when (action) {
            is AppAction.AddRound -> addRound(action)
            AppAction.ResetGame -> resetGame()
        }
    }

    private fun addRound(action: AppAction.AddRound) {
        roundsRepository.update { it + action.round }
    }

    private fun resetGame() {
        roundsRepository.update { Rounds.EMPTY }
    }
}
