plugins {
    id("kmp-root")
}

// Fail on every issue, except the api-vs-implementation rule: this project uses
// implementation(...) everywhere and never wants to be told to use api(...).
dependencyAnalysis {
    issues {
        all {
            onAny { severity("fail") }
            onIncorrectConfiguration { severity("ignore") }
        }
    }

    structure {
        // Compose Multiplatform ships distinct per-platform artifacts (ui-android,
        // ui-desktop, ui-graphics, ...), so bundle each artifact with its variants.
        bundle("compose-animation") { include(kmpBundle("org.jetbrains.compose.animation:animation")) }
        bundle("compose-components-resources") { include(kmpBundle("org.jetbrains.compose.components:components-resources")) }
        bundle("compose-desktop") { include(kmpBundle("org.jetbrains.compose.desktop:desktop")) }
        bundle("compose-foundation") { include(kmpBundle("org.jetbrains.compose.foundation:foundation")) }
        bundle("compose-material3") { include(kmpBundle("org.jetbrains.compose.material3:material3")) }
        bundle("compose-runtime") { include(kmpBundle("org.jetbrains.compose.runtime:runtime")) }
        bundle("compose-ui") { include(kmpBundle("org.jetbrains.compose.ui:ui")) }
        bundle("compose-ui-test") { include(kmpBundle("org.jetbrains.compose.ui:ui-test")) }
        bundle("compose-ui-tooling-preview") { include(kmpBundle("org.jetbrains.compose.ui:ui-tooling-preview")) }
        bundle("koin") { include(kmpBundle("io.insert-koin:koin-core")) }

        // kotlinx artifacts follow the core/-jvm/-android scheme, which
        // includeDependency auto-covers.
        bundle("coroutines") {
            includeDependency("org.jetbrains.kotlinx:kotlinx-coroutines-core")
            includeDependency("org.jetbrains.kotlinx:kotlinx-coroutines-swing")
            includeDependency("org.jetbrains.kotlinx:kotlinx-coroutines-test")
        }
        bundle("serialization") { includeDependency("org.jetbrains.kotlinx:kotlinx-serialization-core") }
        bundle("collections-immutable") { includeDependency("org.jetbrains.kotlinx:kotlinx-collections-immutable") }
        bundle("multiplatform-settings") {
            includeDependency("com.russhwolf:multiplatform-settings")
            includeDependency("com.russhwolf:multiplatform-settings-serialization")
        }
    }
}
