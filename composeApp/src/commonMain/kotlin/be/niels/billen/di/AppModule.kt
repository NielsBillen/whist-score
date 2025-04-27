package be.niels.billen.di
import be.niels.billen.data.repository.DefaultGameRepository
import be.niels.billen.domain.repository.GameRepository
import be.niels.billen.presentation.addround.AddRoundViewModel
import be.niels.billen.presentation.app.AppViewModel
import be.niels.billen.presentation.players.PlayersViewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { DefaultGameRepository() }.bind<GameRepository>()
    single { PlayersViewModel(get()) }
    single { AppViewModel() }
    single { AddRoundViewModel() }
}
