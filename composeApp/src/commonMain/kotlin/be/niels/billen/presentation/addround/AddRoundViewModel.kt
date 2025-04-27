package be.niels.billen.presentation.addround

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.*

class AddRoundViewModel(playersRepository: PlayerRepository, private val roundsRepository: RoundsRepository) : ViewModel() {
    private val _state = MutableStateFlow(AddRoundState())
    val state: StateFlow<AddRoundState> = _state.asStateFlow()

    val players = playersRepository.players.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyMap(),
    )


    fun onAction(action: AddRoundAction) {
        when (action) {
            is AddRoundAction.SetRoundType -> onAction(action)
            is AddRoundAction.SelectPlayer -> onAction(action)
            is AddRoundAction.SetPlayers -> onAction(action)
            is AddRoundAction.SetSlams -> onAction(action)
            is AddRoundAction.AddRound -> onAction(action)
        }
    }

    fun onAction(action: AddRoundAction.SetRoundType) {
        _state.update { it.setRoundType(action.roundType) }
    }

    fun onAction(action: AddRoundAction.SelectPlayer) {
        _state.update { it.setPlayerSelected(action.player, action.selected) }
    }

    fun onAction(action: AddRoundAction.SetPlayers) {
        _state.update { it.setPlayers(action.players) }
    }

    fun onAction(action: AddRoundAction.SetSlams) {
        _state.update { it.setSlams(action.slams) }
    }

    fun onAction(action: AddRoundAction.AddRound) {
       roundsRepository.update { it + action.round }
    }
}