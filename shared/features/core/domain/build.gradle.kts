plugins {
    id("kmp-library")
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.ksp.get().pluginId) // must precede test: kotest's wasm wiring checks for it
    id("kmp-test")
    id(libs.plugins.kover.get().pluginId)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.core.domain"
    }

    sourceSets {
        commonMain.dependencies {
            // not in the extracted compose set: this module applies no Compose plugin
            implementation(libs.compose.runtime)
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}
