import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id(libs.plugins.kotlinJvm.get().pluginId)
    id("compose")
}


dependencies {
    implementation(projects.shared.features.app.presentation)
    implementation(projects.shared.features.overview.presentation)
    implementation(projects.shared.features.addround.presentation)
    implementation(projects.shared.features.editplayers.presentation)
    implementation(projects.shared.features.players.presentation)
    implementation(projects.shared.features.rounds.presentation)
    implementation(projects.shared.features.game.data)
    implementation(projects.shared.features.players.data)

    implementation(libs.compose.ui)
    implementation(libs.compose.runtime)
    implementation(libs.kotlinx.coroutines.swing)

    implementation(libs.koin)
    implementation(libs.multiplatform.settings)
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "be.niels.billen.whistscore.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "be.niels.billen"
            packageVersion = "1.0.0"
        }
    }
}
