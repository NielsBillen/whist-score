plugins {
    id("kmp-library")
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.ksp.get().pluginId)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.players.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)
            implementation(projects.shared.features.core.domain)
            implementation(projects.shared.features.game.data)
            implementation(projects.shared.features.players.domain)
        }
    }
}
