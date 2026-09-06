package be.niels.billen.whistscore.presentation.screens.overview.rounds

sealed interface RoundsViewAction {
    data class DeleteRound(val index: Int) : RoundsViewAction
}