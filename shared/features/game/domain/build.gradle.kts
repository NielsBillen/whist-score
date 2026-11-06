import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id(libs.plugins.kotlinMultiplatform.get().pluginId)
    id(libs.plugins.androidMultiplatformLibrary.get().pluginId)
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.kotest.get().pluginId)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.game.domain"
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
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.compose.runtime)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.shared.features.core.domain)

        }
        commonTest.dependencies {
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
        }
        jvmTest.dependencies {
            implementation(libs.kotest.runner.junit5)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
