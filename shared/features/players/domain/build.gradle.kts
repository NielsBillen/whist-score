plugins {
    id("kmp")
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp) // must precede kotest: its wasm wiring checks for it
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
