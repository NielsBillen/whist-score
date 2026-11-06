import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    // these come from buildSrc's implementation classpath (no version — a
    // versioned request would clash with the already-present plugin);
    // hoisting them here avoids duplicate loads in each subproject's classloader
    id(libs.plugins.androidApplication.get().pluginId) apply false
    id(libs.plugins.androidMultiplatformLibrary.get().pluginId) apply false
    id(libs.plugins.composeMultiplatform.get().pluginId) apply false
    id(libs.plugins.composeCompiler.get().pluginId) apply false
    id(libs.plugins.kotlinJvm.get().pluginId) apply false
    id(libs.plugins.kotlinMultiplatform.get().pluginId) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
}

// Treat every Kotlin compiler warning as an error in all modules.
subprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<KotlinMultiplatformExtension> {
            compilerOptions {
                allWarningsAsErrors = true
            }
        }
    }
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<KotlinJvmProjectExtension> {
            compilerOptions {
                allWarningsAsErrors = true
            }
        }
    }
}

dependencies {
    kover(projects.shared.features.core.domain)
    kover(projects.shared.features.game.domain)
    kover(projects.shared.features.addround.presentation)
    kover(projects.shared.features.app.presentation)
    detektPlugins(libs.composeRules)
}

kover {
    reports {
        filters {
            excludes {
                // *Kt = file-facade classes (top-level declarations, e.g. composables).
                classes("*Kt")
            }
        }
    }
}


detekt {
    buildUponDefaultConfig = true
    allRules = true
}
