package be.niels.billen.whistscore.feature.app

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule =
    module {
        // lambda form, not viewModelOf(::AppViewModel): the reified inline overload emits an
        // $$inlined class whose SourceFile points into koin, which the dependency-analysis
        // plugin can't map back to a source set (upstream bug #1951).
        viewModel { AppViewModel(get()) }
    }
