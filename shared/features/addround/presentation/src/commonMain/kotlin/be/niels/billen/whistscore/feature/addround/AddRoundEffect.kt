package be.niels.billen.whistscore.feature.addround

sealed interface AddRoundEffect {
    data object NextScreen : AddRoundEffect

    data object PreviousScreen : AddRoundEffect
}
