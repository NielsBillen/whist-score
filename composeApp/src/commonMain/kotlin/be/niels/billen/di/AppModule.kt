package be.niels.billen.di
import be.niels.billen.data.repository.DefaultPlayerRepository
import be.niels.billen.data.repository.DefaultRoundsRepository
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.RoundsRepository
import be.niels.billen.presentation.app.AppViewModel
import be.niels.billen.presentation.screens.addround.AddRoundViewModel
import be.niels.billen.presentation.screens.overview.players.PlayersViewModel
import be.niels.billen.presentation.screens.overview.rounds.RoundsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

private val commonModule = module {
    single { DefaultPlayerRepository() }.bind<PlayerRepository>()
    single { DefaultRoundsRepository(settings = get()) }.bind<RoundsRepository>()

    viewModel { PlayersViewModel(get(),get()) }
    viewModel { AppViewModel(get()) }
    viewModel { AddRoundViewModel(get(), get()) }
    viewModel { RoundsViewModel(get(), get()) }
}

val appModule = module {
    includes(commonModule)
    includes(platformModule)
}