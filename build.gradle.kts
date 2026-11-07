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
            // These are added by the compose.desktop and stability-analyzer plugins,
            // so they cannot be declared in a build file. koin-compose-viewmodel is
            // needed to compile (koinViewModel), but its API is inline, so no bytecode
            // reference survives and the analyzer sees it as unused.
            onUnusedDependencies {
                exclude(
                    "com.github.skydoves:compose-stability-runtime",
                    "io.insert-koin:koin-compose-viewmodel",
                    "org.jetbrains.compose.desktop:desktop-jvm-linux-x64",
                    "org.jetbrains.compose.hot-reload:hot-reload-runtime-api",
                )
            }
            // Project dependencies are declared with implementation(...) everywhere. The analyzer
            // records project-dep usages under IncludedBuild coordinates whose variant caps
            // never match the empty-caps project declarations, so it emits phantom
            // "add this dependency" advice for deps that are already declared.
            onUsedTransitiveDependencies {
                excludeRegex(":shared:features:.+")
            }
        }

        // webApp's sources live in src/webMain, which is not a registered source
        // set, so the plugin sees no classes at all and flags every dependency.
        project(":webApp") {
            onAny { severity("ignore") }
        }
    }

    structure {
        // Compose ships per-platform artifacts in three groups (org.jetbrains.compose.*,
        // androidx.*, org.jetbrains.androidx.*), so each family is bundled across all
        // of them.
        bundle("compose-animation") { include(".*compose\\.animation:animation.*") }
        bundle("compose-components-resources") { include(".*compose\\.components:components-resources.*") }
        bundle("compose-desktop") { include(".*compose\\.desktop:desktop.*") }
        bundle("compose-foundation") { include(".*compose\\.foundation:foundation.*") }
        bundle("compose-lifecycle") { include("^(androidx|org\\.jetbrains\\.androidx)\\.lifecycle:lifecycle-.*") }
        bundle("compose-material3") { include(".*compose\\.material3:material3.*") }
        bundle("compose-navigation3") { include("^(androidx|org\\.jetbrains\\.androidx)\\.navigation3:navigation3-.*") }
        bundle("compose-runtime") { include(".*compose\\.runtime:runtime.*") }
        bundle("compose-ui") { include(".*compose\\.ui:ui.*") }
        bundle("compose-ui-test") { include(".*compose\\.ui:ui-test.*") }
        bundle("compose-ui-tooling-preview") { include(".*compose\\.ui:ui-tooling-preview.*") }

        // koin's KMP artifacts all live under io.insert-koin:koin-*.
        bundle("koin") { include("io\\.insert-koin:koin-.*") }

        // kotlinx artifacts follow the core/-jvm/-android/-debug scheme.
        bundle("coroutines") { include("org\\.jetbrains\\.kotlinx:kotlinx-coroutines-.*") }
        bundle("serialization") { include("org\\.jetbrains\\.kotlinx:kotlinx-serialization-.*") }
        bundle("collections-immutable") { include("org\\.jetbrains\\.kotlinx:kotlinx-collections-immutable.*") }
        bundle("multiplatform-settings") { include("com\\.russhwolf:multiplatform-settings-.*") }

        // kotest splits into assertions-core, assertions-shared, common,
        // framework-engine, runner-junit5 and property.
        bundle("kotest") { include("io\\.kotest:kotest-.*") }
    }
}
