import com.google.devtools.ksp.gradle.KspAATask

plugins {
    id("kmp-library")
    id("compose")
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.ksp.get().pluginId) // already on the classpath via the kmp-test script
    alias(libs.plugins.svg2compose)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.core.presentation"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.kotlinx.collections.immutable)
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

        create("icons") {
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
