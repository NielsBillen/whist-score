//dependencyResolutionManagement {
//    versionCatalogs {
//        // Import the main build's catalog so buildSrc/build.gradle.kts can
//        // pin plugin versions from the same single source of truth.
//        create("libs") {
//            from(files("../gradle/libs.versions.toml"))
//        }
//    }
//}

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

plugins {
    id("dev.panuszewski.typesafe-conventions") version "0.12.1"
}