package be.niels.billen.whistscore.feature.addround

import androidx.navigation3.runtime.NavKey

sealed interface AddRoundNavigation : NavKey {
    data object SelectRoundTypeRoute : AddRoundNavigation

    data object SelectPlayersRoute : AddRoundNavigation

    data object SelectSlamsRoute : AddRoundNavigation

    data object SelectBidAchievedRoute : AddRoundNavigation

    data object SummaryRoute : AddRoundNavigation
}
