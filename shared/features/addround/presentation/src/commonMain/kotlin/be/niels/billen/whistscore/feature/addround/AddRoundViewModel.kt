package be.niels.billen.whistscore.feature.addround

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.RoundType
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

            is AddRoundAction.ConfirmPlayers -> confirmPlayers()
        }
    }

    private fun setPlayerSelected(playerId: PlayerId, selected: Boolean) {
        _state.update { oldState ->
            requireNotNull(oldState.roundType)

            val singlePlayer = oldState.roundType.singlePlayer


            val newPlayers = when (selected) {
                true -> if (singlePlayer) setOf(playerId) else oldState.players + playerId
                false -> oldState.players - playerId
            }

            if (singlePlayer) {
                advanceFromPlayersScreen(oldState.roundType)
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

    private fun confirmPlayers() {
        val roundType = _state.value.roundType
        requireNotNull(roundType)
        advanceFromPlayersScreen(roundType = roundType)
    }

    private fun advanceFromPlayersScreen(roundType: RoundType) {
        when (roundType.winCondition) {
            WinCondition.SLAMS -> _effects.tryEmit(AddRoundEffect.NextScreen(AddRoundScreen.SELECT_SLAMS))
            WinCondition.BID_ACHIEVED -> _effects.tryEmit(AddRoundEffect.NextScreen(AddRoundScreen.SELECT_SLAMS))
            WinCondition.NONE -> _effects.tryEmit(AddRoundEffect.NextScreen(AddRoundScreen.SUMMARY))
        }
    }

    private fun setSlams(action: AddRoundAction.SetSlams) {
        _state.update { it.copy(slams = action.slams) }

        _effects.tryEmit(AddRoundEffect.NextScreen(AddRoundScreen.SUMMARY))
    }

    private fun setBidAchieved(action: AddRoundAction.SetBidAchieved) {
        _state.update { it.copy(bidAchieved = action.bidAchieved) }

        _effects.tryEmit(AddRoundEffect.NextScreen(AddRoundScreen.SUMMARY))
    }

    private fun navigateToPreviousScreen() {
        _effects.tryEmit(AddRoundEffect.PreviousScreen)
    }
}

private enum class WinCondition {
    SLAMS,
    BID_ACHIEVED,
    NONE
}

private val RoundType.winCondition: WinCondition
    get() = when (this) {
        Pass -> WinCondition.NONE

        Abandonce,
        AbandonceInTrump,
        Misere,
        OpenMisere,
        SoloSlim -> WinCondition.BID_ACHIEVED

        Regular,
        Treble -> WinCondition.SLAMS

    }