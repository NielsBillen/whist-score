plugins {
    id("kmp-library")
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.ksp.get().pluginId)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.game.data"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.kotlinx.collections.immutable)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.core.domain)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.serialization)
            implementation(libs.koin)
        }
    }
}
