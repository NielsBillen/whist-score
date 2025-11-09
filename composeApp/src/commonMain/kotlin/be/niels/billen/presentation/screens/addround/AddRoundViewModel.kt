package be.niels.billen.presentation.screens.addround

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import be.niels.billen.domain.RoundType
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.RoundsRepository
import kotlinx.coroutines.flow.*

class AddRoundViewModel(playersRepository: PlayerRepository) :
    ViewModel() {
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
            is AddRoundAction.SetPlayers -> onAction(action)
            is AddRoundAction.SetSlams -> onAction(action)
            is AddRoundAction.SetBid -> onAction(action)
            is AddRoundAction.SetBidAchieved -> onAction(action)
            is AddRoundAction.PreviousScreen -> onAction(action)
            is AddRoundAction.SetPassRound -> onAction(action)
        }
    }

    fun onAction(action: AddRoundAction.SetRoundType) {
        _state.update {
            val next = it.setRoundType(action.roundType)
            next.setScreen(next.nextScreen)
        }
    }

    fun onAction(action: AddRoundAction.SetPlayers) {
        _state.update { it.setPlayers(action.players).setScreen(it.nextScreen) }
    }

    fun onAction(action: AddRoundAction.SetSlams) {
        _state.update { it.setSlams(action.slams).setScreen(it.nextScreen) }
    }

    fun onAction(action: AddRoundAction.SetBid) {
        _state.update { it.setBid(action.bid).setScreen(it.nextScreen) }
    }

    fun onAction(action: AddRoundAction.SetBidAchieved) {
        _state.update { it.setBidAchieved(action.bidAchieved).setScreen(it.nextScreen) }
    }

    fun onAction(action: AddRoundAction.SetPassRound) {
        _state.update { it.setPassRound(action.passRound).setScreen(it.nextScreen) }
    }

    fun onAction(action: AddRoundAction.PreviousScreen) {
        _state.update { it.copy(screen = it.previousScreen) }
    }
}

private val AddRoundState.nextScreen: AddRoundScreen
    get() = roundType.let { roundType ->
        if (roundType == null)
            AddRoundScreen.SELECT_ROUND_TYPE
        else {
            val index = roundType.screens.indexOf(screen)
            roundType.screens[(index + 1).coerceIn(roundType.screens.indices)]
        }
    }

private val AddRoundState.previousScreen: AddRoundScreen
    get() = roundType.let { roundType ->
        if (roundType == null)
            AddRoundScreen.SELECT_ROUND_TYPE
        else {
            val index = roundType.screens.indexOf(screen)
            roundType.screens[(index - 1).coerceIn(roundType.screens.indices)]
        }
    }

private val RoundType.screens: List<AddRoundScreen>
    get() = when (this) {
        RoundType.Regular, RoundType.Treble -> listOf(
            AddRoundScreen.SELECT_ROUND_TYPE,
            AddRoundScreen.SELECT_PLAYERS,
            AddRoundScreen.SELECT_SLAMS,
            AddRoundScreen.SUMMARY
        )

        RoundType.Abandonce, RoundType.AbandonceInTrump, RoundType.Misere, RoundType.OpenMisere, RoundType.SoloSlim -> listOf(
            AddRoundScreen.SELECT_ROUND_TYPE,
            AddRoundScreen.SELECT_PLAYERS,
            AddRoundScreen.SELECT_BID_ACHIEVED,
            AddRoundScreen.SUMMARY
        )

    }