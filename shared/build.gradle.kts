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
    alias(libs.plugins.svg2compose)
}

kotlin {
    android {
        namespace = "be.niels.billen.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
        androidResources {
            enable = true
        }
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

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        compilerOptions { optIn.add("kotlin.js.ExperimentalWasmJsInterop") }
    }
}

//svgToCompose {
//    processor {
//        val icons by creating {
//            from(layout.projectDirectory.dir("features/core/presentation/src/commonMain/composeResources/drawable"))
//            destinationPackage("be.niels.billen.whistscore.feature.core.icons")
//            optimize(true)
//            recursive()
//            icons {
//                theme("be.niels.billen.whistscore.feature.core.theme")
//                minify()
//                //exclude(".*_exclude\\.svg".toRegex())
////                mapIconNameTo { iconName ->
////                    iconName.replace("_filled", "")
////                }
//                // Optional: customize generated code with a template
//                // templateFile(layout.projectDirectory.file("s2c.template.toml"))
//            }
//        }
//    }
//}

