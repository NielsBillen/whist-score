import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = versionCatalogs.named("libs")
val sharedComposeDeps = listOf(
    "compose-runtime",
    "compose-foundation",
    "compose-ui",
    "compose-material3",
    "compose-components-resources",
    "compose-uiToolingPreview",
)

extensions.configure<KotlinMultiplatformExtension> {
    sourceSets.named("commonMain") {
        dependencies {
            sharedComposeDeps.forEach {
                implementation(libs.findLibrary(it).get())
            }
        }
    }
}
