package be.niels.billen.whistscore.feature.editplayers

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val editPlayersModule = module {
    viewModelOf(::EditPlayersViewModel)
}
