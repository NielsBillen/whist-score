package be.niels.billen.whistscore.feature.overview

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val overviewModule = module {
    viewModelOf(::OverviewViewModel)
}
