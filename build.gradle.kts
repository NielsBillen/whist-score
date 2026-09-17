plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kover)
}

dependencies {
    kover(projects.shared.features.core.domain)
    kover(projects.shared.features.game.domain)
    kover(projects.shared.features.addround.presentation)
    kover(projects.shared.features.app.presentation)
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "be.niels.billen.whistscore.feature.addround.AddRoundViewKt",
                    "be/niels/billen/whistscore/feature/addround/RoundTypeInputScreenKt",
                    "be.niels.billen.whistscore.feature.addround.SlamInputScreenKt"
                )
            }
        }
    }
}