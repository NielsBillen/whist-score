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
            implementation(libs.androidx.compose.runtime.annotation)
            implementation(libs.kotlinx.collections.immutable)
        }
        commonTest.dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.engine)
        }
        androidMain.dependencies {
            runtimeOnly(libs.kotlinx.coroutines.android)
        }
    }
}
