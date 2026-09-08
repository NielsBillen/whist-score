import org.jetbrains.compose.ExperimentalComposeLibrary
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
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.androidx.nav3.ui)
            implementation(projects.shared.features.core.domain)
            implementation(projects.shared.features.core.presentation)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.overview.presentation)
            implementation(projects.shared.features.addround.presentation)
            implementation(projects.shared.features.editplayers.presentation)
        }
        jvmTest.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(projects.shared.features.game.domain)
            implementation(projects.shared.features.players.domain)
            implementation(projects.shared.features.players.presentation)
            implementation(projects.shared.features.rounds.presentation)
            implementation(libs.kotest.runner.junit5)
        }
        commonTest.dependencies {
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
