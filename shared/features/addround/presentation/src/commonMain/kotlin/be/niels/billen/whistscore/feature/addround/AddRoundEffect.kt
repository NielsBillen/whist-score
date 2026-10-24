package be.niels.billen.whistscore.feature.addround

sealed interface AddRoundEffect {
    data class NextScreen(val screen: AddRoundScreen): AddRoundEffect

    data object PreviousScreen : AddRoundEffect
}
