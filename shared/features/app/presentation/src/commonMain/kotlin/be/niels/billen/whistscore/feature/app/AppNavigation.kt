package be.niels.billen.whistscore.feature.app

import androidx.navigation3.runtime.NavKey

sealed interface AppNavigation : NavKey {
    data object OverviewRoute : AppNavigation

    data object AddRoundRoute : AppNavigation

    data object EditPlayersRoute : AppNavigation
}
