package be.niels.billen.presentation.app

import androidx.lifecycle.ViewModel
import be.niels.billen.domain.Rounds
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel(private val roundsRepository: RoundsRepository) : ViewModel() {
    private val _screen = MutableStateFlow(AppScreen.OVERVIEW)
    val screen: StateFlow<AppScreen> = _screen.asStateFlow()

    fun navigate(action: AppAction) {
        when (action) {
            is AppAction.Navigate -> navigate(action)
            is AppAction.AddRound -> addRound(action)
            is AppAction.ResetGame -> resetGame()
        }
    }

    private fun navigate(action: AppAction.Navigate) {
        _screen.update { action.screen }
    }

    private fun addRound(action: AppAction.AddRound) {
        roundsRepository.update { it + action.round }
        _screen.update { AppScreen.OVERVIEW }
    }

    private fun resetGame() {
        roundsRepository.update { Rounds.EMPTY }
    }
}