plugins {
    id("kmp")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp) // must precede test: kotest's wasm wiring checks for it
    id("test")
    alias(libs.plugins.kover)
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
