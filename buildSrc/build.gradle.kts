plugins {
    `kotlin-dsl` // version is pinned to the running Gradle release; never specified
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal() // the kotest plugin jar is only published here
}

dependencies {
    // Jars backing the plugin ids the precompiled scripts apply. Versions come
    // from the imported catalog (gradle/libs.versions.toml).
    implementation(group = "org.jetbrains.kotlin", name = "kotlin-gradle-plugin", version = libs.versions.kotlin.get())
    implementation(group = "com.android.tools.build", name = "gradle", version = libs.versions.agp.get())
    implementation(group = "org.jetbrains.compose", name = "compose-gradle-plugin", version = libs.versions.composeMultiplatform.get())
    // not pulled in transitively by compose-gradle-plugin; versions track Kotlin
    implementation(group = "org.jetbrains.kotlin", name = "compose-compiler-gradle-plugin", version = libs.versions.kotlin.get())
    implementation(group = "io.kotest", name = "kotest-framework-plugin-gradle", version = libs.versions.kotest.get())
}
