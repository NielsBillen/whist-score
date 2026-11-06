import org.jetbrains.compose.ComposePlugin

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotest)
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
        }

        // The jvm target is declared later, by the kmp script; match lazily.
        matching { it.name == "jvmTest" }.configureEach {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}

// The compose plugin is applied by the module after this script, so hook in
// lazily. compose.dependencies is the object the compose plugin adds to the
// dependencies extensions; desktop.currentOs is JVM-only.
plugins.withId("org.jetbrains.compose") {
    val composeDeps = dependencies.extensions.findByName("compose") as? ComposePlugin.Dependencies
    if (composeDeps != null) {
        kotlin.sourceSets.matching { it.name == "jvmTest" }.all {
            dependencies {
                implementation(composeDeps.desktop.currentOs)
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
