plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version embeddedKotlinVersion
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal() // the kotest plugin jar is only published here
}
