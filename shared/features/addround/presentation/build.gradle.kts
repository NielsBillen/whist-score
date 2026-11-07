import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.kover.get().pluginId)
    id(libs.plugins.ksp.get().pluginId) // must precede test: kotest's wasm wiring checks for it
    id("kmp-library")
    id("compose")
    id("kmp-test")
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.addround.presentation"
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
            implementation(libs.androidx.compose.runtime.annotation)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.nav3.ui)
            implementation(libs.compose.animation)
            implementation(libs.compose.foundation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.koin)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.kotlinx.collections.immutable)
            implementation(projects.shared.features.core.domain)
            implementation(projects.shared.features.core.presentation)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.players.domain)
            implementation(projects.shared.features.ui.presentation)
        }
        commonTest.dependencies {
            implementation(libs.compose.ui.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.engine)
            implementation(libs.koin)
            implementation(libs.koin.core.viewmodel)
            implementation(libs.turbine)
        }
        androidMain.dependencies {
            runtimeOnly(libs.kotlinx.coroutines.android)
        }
    }
}
