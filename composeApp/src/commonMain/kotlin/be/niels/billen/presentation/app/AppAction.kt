package be.niels.billen.presentation.app

enum class AppScreen {
    OVERVIEW,
    ADD_ROUND
}


sealed interface AppAction {
    data class Navigate(val screen: AppScreen) : AppAction
}