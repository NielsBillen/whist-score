package be.niels.billen.whistscore.feature.game

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val gameDataModule = module {
    singleOf(::DefaultGameRepository).bind<GameRepository>()
}
