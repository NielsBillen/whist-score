package be.niels.billen.whistscore.feature.overview

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.game.GameRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
class OverviewViewModel(gameRepository: GameRepository) : ViewModel() {
    val canStartNewGame = gameRepository.game.map { it.rounds.isNotEmpty() }
        .stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(), initialValue = false)

}