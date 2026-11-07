plugins {
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.ksp.get().pluginId)
    id("kmp-library")
    id("compose")
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.overview.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.compose.foundation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(projects.shared.features.core.domain)
            implementation(projects.shared.features.core.presentation)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.players.presentation)
            implementation(projects.shared.features.rounds.presentation)
            implementation(projects.shared.features.ui.presentation)
        }
    }
}
