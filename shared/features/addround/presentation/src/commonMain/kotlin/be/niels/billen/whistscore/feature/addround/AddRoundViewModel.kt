package be.niels.billen.whistscore.feature.addround

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.game.Game
import be.niels.billen.whistscore.feature.game.GameRepository
import kotlinx.coroutines.flow.*

class AddRoundViewModel(gameRepository: GameRepository) : ViewModel() {
    private val navigator = AddRoundNavigator()

    private val _state = MutableStateFlow(AddRoundState())
    val state: StateFlow<AddRoundState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AddRoundEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<AddRoundEffect> = _effects.asSharedFlow()

    val game = gameRepository.game.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Game.DEFAULT,
    )

    fun onAction(action: AddRoundAction) {
        when (action) {
            is AddRoundAction.SetRoundType -> onAction(action)
            is AddRoundAction.SetPlayers -> onAction(action)
            is AddRoundAction.SetSlams -> onAction(action)
            is AddRoundAction.SetBidAchieved -> onAction(action)
            is AddRoundAction.PreviousScreen -> onAction(action)
        }
    }

    fun onAction(action: AddRoundAction.SetRoundType) {
        advance { it.setRoundType(action.roundType) }
    }

    fun onAction(action: AddRoundAction.SetPlayers) {
        advance { it.setPlayers(action.players) }
    }

    fun onAction(action: AddRoundAction.SetSlams) {
        advance { it.setSlams(action.slams) }
    }

    fun onAction(action: AddRoundAction.SetBidAchieved) {
        advance { it.setBidAchieved(action.bidAchieved) }
    }

    fun onAction(action: AddRoundAction.PreviousScreen) {
        _state.update { state ->
            state.copy(screen = navigator.previous(screen = state.screen, roundType = state.roundType))
        }
        _effects.tryEmit(AddRoundEffect.PreviousScreen)
    }

    private fun advance(update: (AddRoundState) -> AddRoundState) {
        _state.update { state ->
            val updated = update(state)
            updated.copy(screen = navigator.next(screen = updated.screen, roundType = updated.roundType))
        }
        _effects.tryEmit(AddRoundEffect.NextScreen)
    }
}
