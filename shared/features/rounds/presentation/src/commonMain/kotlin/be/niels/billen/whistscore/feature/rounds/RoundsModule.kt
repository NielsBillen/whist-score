package be.niels.billen.whistscore.feature.rounds

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val roundsModule = module {
    viewModelOf(::RoundsViewModel)
}
