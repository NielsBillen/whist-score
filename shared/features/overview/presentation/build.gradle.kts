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
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.compose.foundation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.koin)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.players.presentation)
            implementation(projects.shared.features.rounds.presentation)
            implementation(projects.shared.features.ui.presentation)
        }
    }
}
