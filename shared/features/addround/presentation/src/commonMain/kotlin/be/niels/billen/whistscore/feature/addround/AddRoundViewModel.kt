package be.niels.billen.whistscore.feature.addround

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.game.Game
import be.niels.billen.whistscore.feature.game.GameRepository
import kotlinx.coroutines.flow.*

class AddRoundViewModel(gameRepository: GameRepository) : ViewModel() {

    private val _state = MutableStateFlow(AddRoundState())
    val state: StateFlow<AddRoundState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AddRoundEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<AddRoundEffect> = _effects.asSharedFlow()

    val game = gameRepository.game.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Game.DEFAULT,
    )

    fun setPlayers(action: AddRoundAction) {
        when (action) {
            is AddRoundAction.SetRoundType -> setRoundType(action)
            is AddRoundAction.SetPlayers -> setPlayers(action)
            is AddRoundAction.SetSlams -> setSlams(action)
            is AddRoundAction.SetBidAchieved -> setBidAchieved(action)
            is AddRoundAction.PreviousScreen -> navigateToPreviousScreen()
        }
    }

    private fun setRoundType(action: AddRoundAction.SetRoundType) {
        advance { it.setRoundType(action.roundType) }
    }

    private fun setPlayers(action: AddRoundAction.SetPlayers) {
        advance { it.setPlayers(action.players) }
    }

    private fun setSlams(action: AddRoundAction.SetSlams) {
        advance { it.setSlams(action.slams) }
    }

    private fun setBidAchieved(action: AddRoundAction.SetBidAchieved) {
        advance { it.setBidAchieved(action.bidAchieved) }
    }

    private fun navigateToPreviousScreen() {
        _state.update { state ->
            state.copy(screen = AddRoundNavigator.previous(screen = state.screen, roundType = state.roundType))
        }
        _effects.tryEmit(AddRoundEffect.PreviousScreen)
    }

    private fun advance(update: (AddRoundState) -> AddRoundState) {
        _state.update { state ->
            val updated = update(state)
            updated.copy(screen = AddRoundNavigator.next(screen = updated.screen, roundType = updated.roundType))
        }
        _effects.tryEmit(AddRoundEffect.NextScreen)
    }
}
