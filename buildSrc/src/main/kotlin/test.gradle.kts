import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("io.kotest")
}

val libs = versionCatalogs.named("libs")

val kotlin = extensions.getByType<KotlinMultiplatformExtension>()

kotlin.sourceSets.getByName("commonTest").dependencies {
    implementation(libs.findLibrary("kotest-framework-engine").get())
    implementation(libs.findLibrary("kotest-assertions-core").get())
}

// JVM-only artifact: commonTest would resolve it against the wasm test classpath.
kotlin.sourceSets.getByName("jvmTest").dependencies {
    implementation(libs.findLibrary("kotest-runner-junit5").get())
}

// compose.dependencies is the object the compose plugin adds to the
// dependencies extensions; it is absent when the compose plugin is not applied.
val composeDeps = dependencies.extensions.findByName("compose") as? ComposePlugin.Dependencies
if (composeDeps != null) {
    kotlin.sourceSets.getByName("jvmTest").dependencies {
        implementation(composeDeps.desktop.currentOs)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
