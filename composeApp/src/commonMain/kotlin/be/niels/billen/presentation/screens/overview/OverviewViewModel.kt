package be.niels.billen.presentation.screens.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.domain.Rounds
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class OverviewViewModel(private val roundsRepository: RoundsRepository) : ViewModel() {
    val canStartNewGame = roundsRepository.rounds.map { it.isNotEmpty() }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = false)

}