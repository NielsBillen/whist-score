package be.niels.billen.whistscore.feature.addround

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val addRoundModule = module {
    viewModelOf(::AddRoundViewModel)
}
