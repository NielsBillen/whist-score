import dev.detekt.gradle.Detekt

plugins {
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    alias(libs.plugins.kotlinMultiplatform)
    id("kmp-kover")
}

ktlint {
    ktlintVersion = libs.versions.ktlint.asProvider().get()
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
    // default src/{main,test}/{java,kotlin} don't exist in KMP modules;
    // match lazily, targets are declared after this script
    source.from(kotlin.sourceSets.matching { true }.map { it.kotlin })
}

// The plain `detekt` task (light) is already wired to `check`; the full-mode
// compilation tasks (detektMainJvm, detektTestJvm, ...) are not — wire them.
// Deferred: configuring `check` inside a task-creation callback is disallowed.
project.afterEvaluate {
    tasks.named("check").configure {
        dependsOn(
            tasks.withType<Detekt>().matching {
                it.name != "detekt" && !it.name.endsWith("SourceSet")
            },
        )
    }
}

dependencies {
    detektPlugins(libs.composeRules)
}
