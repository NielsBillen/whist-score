package be.niels.billen.whistscore.feature.game

import org.koin.dsl.bind
import org.koin.dsl.module

val gameDataModule =
    module {
        // lambda form, not singleOf(::DefaultGameRepository): the reified inline overload emits an
        // $$inlined class whose SourceFile points into koin, which the dependency-analysis
        // plugin can't map back to a source set (upstream bug #1951).
        single { DefaultGameRepository(get()) }.bind<GameRepository>()
    }
