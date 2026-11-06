import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp")
    id("compose")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp) // must precede test: kotest's wasm wiring checks for it
    id("test")
    alias(libs.plugins.kover)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.ui.presentation"
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        // not in the kotlin script: skiko wasm cannot load under Node
        nodejs {
            testTask {
                enabled = false
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.nav3.ui)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.kotlinx.collections.immutable)
            implementation(project(":shared:features:core:presentation"))
        }
        commonTest.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
        }
    }
}
