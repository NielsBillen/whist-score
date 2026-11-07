import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("kmp")
    alias(libs.plugins.androidMultiplatformLibrary)
}


kotlin {
    android {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
    }
}
