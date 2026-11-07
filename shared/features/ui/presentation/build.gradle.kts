import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-library")
    id("compose")
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.ksp.get().pluginId) // must precede test: kotest's wasm wiring checks for it
    id("kmp-test")
    id(libs.plugins.kover.get().pluginId)
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
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.collections.immutable)
            implementation(project(":shared:features:core:presentation"))
        }
        commonTest.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.engine)
        }
        androidMain.dependencies {
            runtimeOnly(libs.kotlinx.coroutines.android)
        }
    }
}
