import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

// Precompiled scripts can't use the type-safe libs accessor; the extension
// reads the consuming project's catalog.
val libs = versionCatalogs.named("libs")

fun version(name: String) = libs.findVersion(name).get().getRequiredVersion()

val kotlin = extensions.getByType<KotlinMultiplatformExtension>()

// The android target is AGP's target type; namespace stays per-module.
kotlin.targets.getByName("android").let { it as KotlinMultiplatformAndroidLibraryTarget }
    .apply {
        compileSdk = version("android-compileSdk").toInt()
        minSdk = version("android-minSdk").toInt()
        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
        androidResources { enable = true }
    }

//    listOf(
//        iosArm64(),
//        iosSimulatorArm64()
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "Shared"
//            isStatic = true
//        }
//    }

kotlin.jvm()

@OptIn(ExperimentalWasmDsl::class)
kotlin.wasmJs {
    nodejs()
    compilerOptions { optIn.add("kotlin.js.ExperimentalWasmJsInterop") }
}
