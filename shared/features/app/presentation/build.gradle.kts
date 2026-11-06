import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotest)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.app.presentation"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
        androidResources { enable = true }
    }
//    (iOS block stays commented out, same as shared)
    jvm()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        compilerOptions { optIn.add("kotlin.js.ExperimentalWasmJsInterop") }
    }
    sourceSets {
        commonMain.dependencies {
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.navigation3)
            implementation(libs.androidx.nav3.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
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
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.players.domain)
            implementation(projects.shared.features.players.presentation)
            implementation(projects.shared.features.rounds.presentation)
        }
        // JVM-only artifacts: commonTest would resolve them against the wasm test classpath.
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotest.runner.junit5)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
