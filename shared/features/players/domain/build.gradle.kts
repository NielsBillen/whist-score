plugins {
    id("kmp-library")
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.ksp.get().pluginId) // must precede kotest: its wasm wiring checks for it
    id(libs.plugins.kotest.get().pluginId)
}

kotlin {
    android {
        namespace = "be.niels.billen.whistscore.feature.players.domain"
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.kotlinx.coroutines.core)
            implementation(projects.shared.features.core.domain)
            implementation(projects.shared.features.game.domain)
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
