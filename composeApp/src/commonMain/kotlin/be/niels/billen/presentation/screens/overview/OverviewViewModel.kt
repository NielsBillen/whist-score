package be.niels.billen.presentation.screens.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.domain.repository.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class OverviewViewModel(private val gameRepository: GameRepository) : ViewModel() {
    val canStartNewGame = gameRepository.game.map { it.rounds.isNotEmpty() }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = false)

}