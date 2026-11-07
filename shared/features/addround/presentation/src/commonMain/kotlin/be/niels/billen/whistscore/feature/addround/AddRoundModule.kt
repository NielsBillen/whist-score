package be.niels.billen.whistscore.feature.addround

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val addRoundModule =
    module {
        viewModel { AddRoundViewModel(get(), get()) }
    }
