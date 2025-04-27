package be.niels.billen.presentation.app

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel : ViewModel() {
    private val _screen = MutableStateFlow(AppScreen.OVERVIEW)
    val screen: StateFlow<AppScreen> = _screen.asStateFlow()

    fun onAction(action: AppAction) {
        when (action) {
            is AppAction.Navigate -> navigate(action)
        }
    }

    private fun navigate(action: AppAction.Navigate) {
        println("Navigate ${action.screen}")
        _screen.update { action.screen }
    }
}