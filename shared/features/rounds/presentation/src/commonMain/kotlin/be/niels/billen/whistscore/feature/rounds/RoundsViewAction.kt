package be.niels.billen.whistscore.feature.rounds

sealed interface RoundsViewAction {
    data class DeleteRound(val index: Int) : RoundsViewAction
}