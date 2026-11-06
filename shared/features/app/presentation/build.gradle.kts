import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
    alias(libs.plugins.ksp) // must precede test: kotest's wasm wiring checks for it
    id("compose")
    id("kmp")
    id("test")
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.app.presentation"
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
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.androidx.nav3.ui)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(projects.shared.features.addround.presentation)
            implementation(projects.shared.features.core.domain)
            implementation(projects.shared.features.core.presentation)
            implementation(projects.shared.features.editplayers.presentation)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.overview.presentation)
            implementation(projects.shared.features.ui.presentation)
        }
        commonTest.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.test)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.players.domain)
            implementation(projects.shared.features.players.presentation)
            implementation(projects.shared.features.rounds.presentation)
        }
    }
}
