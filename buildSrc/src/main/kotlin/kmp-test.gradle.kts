plugins {
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotest)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
