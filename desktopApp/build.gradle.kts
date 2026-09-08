import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
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

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutines.swing)

    implementation(libs.koin)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.multiplatform.settings)
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
