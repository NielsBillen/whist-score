package be.niels.billen.whistscore.feature.players

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val playersModule = module {
    viewModelOf(::PlayersViewModel)
}
