package be.niels.billen.di
import be.niels.billen.data.repository.DefaultPlayerRepository
import be.niels.billen.data.repository.DefaultRoundsRepository
import be.niels.billen.domain.repository.PlayerRepository
import be.niels.billen.domain.repository.RoundsRepository
import be.niels.billen.presentation.addround.AddRoundViewModel
import be.niels.billen.presentation.app.AppViewModel
import be.niels.billen.presentation.players.PlayersViewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { DefaultPlayerRepository() }.bind<PlayerRepository>()
    single { DefaultRoundsRepository() }.bind<RoundsRepository>()
    //single { DefaultGameRepository(get(), get()) }.bind<GameRepository>()
    single { PlayersViewModel(get(),get()) }
    single { AppViewModel() }
    single { AddRoundViewModel(get(), get()) }
}
