plugins {
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.kover.get().pluginId)
    id(libs.plugins.ksp.get().pluginId) // must precede test: kotest's wasm wiring checks for it
    id("kmp-library")
    id("kmp-test")
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.game.domain"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.compose.runtime.annotation)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.shared.features.core.domain)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.engine)
        }
    }
}
