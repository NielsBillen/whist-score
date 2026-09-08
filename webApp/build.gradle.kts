import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared.features.app.presentation)
            implementation(projects.shared.features.game.data)
            implementation(projects.shared.features.players.data)
            implementation(projects.shared.features.overview.presentation)
            implementation(projects.shared.features.addround.presentation)
            implementation(projects.shared.features.editplayers.presentation)
            implementation(projects.shared.features.players.presentation)
            implementation(projects.shared.features.rounds.presentation)

            implementation(compose.ui)
            implementation(libs.koin)
            implementation(libs.koin.compose)
            implementation(libs.multiplatform.settings)
        }
    }
}
