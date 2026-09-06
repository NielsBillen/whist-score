package be.niels.billen.di

import be.niels.billen.data.repository.DefaultPlayerRepository
import be.niels.billen.data.repository.DefaultGameRepository
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.GameRepository
import be.niels.billen.presentation.app.AppViewModel
import be.niels.billen.presentation.screens.addround.AddRoundViewModel
import be.niels.billen.presentation.screens.editplayers.EditPlayersViewModel
import be.niels.billen.presentation.screens.overview.players.PlayersViewModel
import be.niels.billen.presentation.screens.overview.rounds.RoundsViewModel
import be.niels.billen.presentation.screens.overview.OverviewViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val commonModule = module {
    singleOf(::DefaultPlayerRepository).bind<PlayerRepository>()
    singleOf(::DefaultGameRepository).bind<GameRepository>()

    viewModelOf(::PlayersViewModel)
    viewModelOf(::AppViewModel)
    viewModelOf(::AddRoundViewModel)
    viewModelOf(::RoundsViewModel)
    viewModelOf(::OverviewViewModel)
    viewModelOf(::EditPlayersViewModel)
}

val appModule = module {
    includes(commonModule)
    includes(platformModule)
}