package be.niels.billen.whistscore.feature.players

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val playersDataModule = module {
    singleOf(::DefaultPlayerRepository).bind<PlayerRepository>()
}
