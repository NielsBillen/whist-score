package be.niels.billen.whistscore.feature.addround

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.RoundType.*
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

    fun onAction(action: AddRoundAction) {
        when (action) {
            is AddRoundAction.SetRoundType -> setRoundType(action)
            is AddRoundAction.SetSlams -> setSlams(action)
            is AddRoundAction.SetBidAchieved -> setBidAchieved(action)
            is AddRoundAction.PreviousScreen -> navigateToPreviousScreen()
            is AddRoundAction.SetPlayerSelected -> setPlayerSelected(
                playerId = action.playerId,
                selected = action.selected
            )
        }
    }

    private fun setPlayerSelected(playerId: PlayerId, selected: Boolean) {
        _state.update { oldState ->
            requireNotNull(oldState.roundType)

            val newPlayers = when (selected) {
                true -> if (oldState.roundType.singlePlayer) setOf(playerId) else oldState.players + playerId
                false -> oldState.players - playerId
            }

            oldState.copy(players = newPlayers)
        }
    }

    private fun setRoundType(action: AddRoundAction.SetRoundType) {
        _state.update { it.copy(roundType = action.roundType) }

        when (action.roundType) {
            Pass -> _effects.tryEmit(AddRoundEffect.NextScreen(AddRoundScreen.SUMMARY))

            Regular,
            Abandonce,
            AbandonceInTrump,
            Misere,
            Treble,
            OpenMisere,
            SoloSlim -> _effects.tryEmit(AddRoundEffect.NextScreen(AddRoundScreen.SELECT_PLAYERS))
        }
    }


    private fun setSlams(action: AddRoundAction.SetSlams) {
        _state.update { it.copy(slams = action.slams) }
    }

    private fun setBidAchieved(action: AddRoundAction.SetBidAchieved) {
        _state.update { it.copy(bidAchieved = action.bidAchieved) }
    }

    private fun navigateToPreviousScreen() {
        _effects.tryEmit(AddRoundEffect.PreviousScreen)
    }
}
