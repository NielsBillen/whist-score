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
            implementation(libs.androidx.compose.runtime.saveable)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.androidx.nav3.ui)
            implementation(libs.compose.animation)
            implementation(libs.compose.foundation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.koin)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core.viewmodel)
            implementation(projects.shared.features.addround.presentation)
            implementation(projects.shared.features.core.domain)
            implementation(projects.shared.features.core.presentation)
            implementation(projects.shared.features.editplayers.presentation)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.overview.presentation)
            implementation(projects.shared.features.ui.presentation)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.engine)
            implementation(projects.shared.features.game.domain)
        }
        // UI tests only run on the jvm target: Compose MP does not put skiko's
        // native runtime on a plain KMP test classpath, so the desktop library
        // must be declared here (upstream: JetBrains/compose-multiplatform#1352).
        jvmTest.dependencies {
            implementation(libs.compose.desktop)
            implementation(libs.compose.ui.test)
            implementation(libs.koin)
            implementation(libs.skiko.awt.runtime.linux.x64)
            implementation(projects.shared.features.players.domain)
            implementation(projects.shared.features.players.presentation)
            implementation(projects.shared.features.rounds.presentation)
        }
        androidMain.dependencies {
            runtimeOnly(libs.kotlinx.coroutines.android)
        }
    }
}
