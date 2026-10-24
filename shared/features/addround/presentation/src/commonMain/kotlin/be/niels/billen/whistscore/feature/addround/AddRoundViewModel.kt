package be.niels.billen.whistscore.feature.addround

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.whistscore.feature.core.DEFAULT_PLAYERS
import be.niels.billen.whistscore.feature.core.PlayerId
import be.niels.billen.whistscore.feature.core.RoundType
import be.niels.billen.whistscore.feature.core.RoundType.*
import be.niels.billen.whistscore.feature.game.Game
import be.niels.billen.whistscore.feature.game.GameRepository
import be.niels.billen.whistscore.feature.players.PlayerRepository
import kotlinx.collections.immutable.minus
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.plus
import kotlinx.coroutines.flow.*

class AddRoundViewModel(
    gameRepository: GameRepository,
    playerRepository: PlayerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddRoundState())
    val state: StateFlow<AddRoundState> = _state.asStateFlow()

    private val _effects = MutableSharedFlow<AddRoundEffect>(extraBufferCapacity = 1)
    val effects: SharedFlow<AddRoundEffect> = _effects.asSharedFlow()

    val game = gameRepository.game.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Game.DEFAULT,
    )

    val players = playerRepository.players.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = DEFAULT_PLAYERS,
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
                true -> if (singlePlayer) persistentSetOf(playerId) else oldState.players + playerId
                false -> oldState.players - playerId
            }

            if (singlePlayer) {
                advanceFromPlayersScreen(oldState.roundType)
            }

            oldState.copy(players = newPlayers)
        }
    }

    private fun setRoundType(action: AddRoundAction.SetRoundType) {
        _state.update {
            it.copy(
                roundType = action.roundType,
                players = if (action.roundType.singlePlayer != it.roundType?.singlePlayer) persistentSetOf() else it.players
            )
        }

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
            WinCondition.BID_ACHIEVED -> _effects.tryEmit(AddRoundEffect.NextScreen(AddRoundScreen.SELECT_BID_ACHIEVED))
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