import com.google.devtools.ksp.gradle.KspAATask
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
        namespace = "be.niels.billen.whistscore.feature.core.presentation"
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
            implementation(libs.compose.foundation)
            implementation(libs.compose.ui)
            implementation(libs.compose.material3)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
        }
    }
}

svgToCompose {
    processor {
        common {
            optimize(true)
            recursive()
            icons {
                minify()
                noPreview()
            }
        }

        val icons by creating {
            from(layout.projectDirectory.dir("src/commonMain/composeResources/drawable"))
            destinationPackage("be.niels.billen.whistscore.feature.core.icons")
        }
    }
}

// svg2compose wires dependsOn(parseSvgToComposeIcon) only onto Kotlin compile tasks, but KSP
// also reads the generated commonMain sources, so it needs the same dependency.
tasks.withType<KspAATask>().configureEach {
    dependsOn(tasks.named("parseSvgToComposeIcon"))
}