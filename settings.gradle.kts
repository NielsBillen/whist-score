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
include(":shared:features:rounds:domain")
include(":shared:features:rounds:presentation")
include(":shared:features:editplayers:presentation")
include(":shared:features:addround:presentation")
include(":shared:features:overview:presentation")