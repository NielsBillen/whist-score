plugins {
    id("kmp-library")
    id("compose")
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.ksp.get().pluginId)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.editplayers.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(projects.shared.features.core.domain)
            implementation(projects.shared.features.core.presentation)
            implementation(projects.shared.features.players.domain)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.ui.presentation)
        }
    }
}
