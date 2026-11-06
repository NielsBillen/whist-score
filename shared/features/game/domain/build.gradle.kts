plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp) // must precede test: kotest's wasm wiring checks for it
    id("kmp")
    id("test")
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.game.domain"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.collections.immutable)
            // not in the extracted compose set: this module applies no Compose plugin
            implementation(libs.compose.runtime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.shared.features.core.domain)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.collections.immutable)
        }
    }
}
