import com.google.devtools.ksp.gradle.KspAATask

plugins {
    id("kmp")
    id("compose")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.svg2compose)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.core.presentation"
    }

    sourceSets {
        commonMain.dependencies {
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
