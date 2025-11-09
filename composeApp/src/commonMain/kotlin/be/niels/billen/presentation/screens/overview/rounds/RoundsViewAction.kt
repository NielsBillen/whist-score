package be.niels.billen.presentation.screens.overview.rounds

sealed interface RoundsViewAction {
    data class DeleteRound(val index: Int) : RoundsViewAction
}