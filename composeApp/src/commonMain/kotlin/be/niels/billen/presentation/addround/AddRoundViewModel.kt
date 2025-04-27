package be.niels.billen.presentation.addround

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddRoundViewModel : ViewModel() {
    private val _state = MutableStateFlow(AddRoundState())
    val state: StateFlow<AddRoundState> = _state.asStateFlow()

    fun onAction(action: AddRoundAction) {
        when(action) {
            is AddRoundAction.SetRoundType -> onAction(action)
        }
    }

    fun onAction(action: AddRoundAction.SetRoundType) {
        _state.update { it.setRoundType(action.roundType) }
    }
}