rootProject.name = "whist-score"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    // Dependency analysis — auto-applies to every project; configured in the root build file
    id("com.autonomousapps.build-health") version "3.20.1"

    // Classloader requirement: the Kotlin/Android plugins must load in the same
    // (or parent) classloader as the analysis plugin, or analysis fails with
    // TypeNotPresentException. Versions must match the modules.
    id("org.jetbrains.kotlin.multiplatform") version "2.4.21" apply false
    id("org.jetbrains.kotlin.android") version "2.4.21" apply false
    id("com.android.application") version "9.3.3" apply false
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":androidApp")
include(":desktopApp")
include(":shared")
include(":webApp")

include(":shared:features:core:domain")
include(":shared:features:core:presentation")
include(":shared:features:app:presentation")
include(":shared:features:game:domain")
include(":shared:features:game:data")
include(":shared:features:players:domain")
include(":shared:features:players:data")
include(":shared:features:players:presentation")
include(":shared:features:rounds:presentation")
include(":shared:features:editplayers:presentation")
include(":shared:features:addround:presentation")
include(":shared:features:overview:presentation")
include(":shared:features:ui:presentation")