import dev.detekt.gradle.Detekt
import dev.detekt.gradle.DetektCreateBaselineTask

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
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
}

// Full-mode tasks analyze Kotlin compilations, which carry generated sources
// (compose resources, svg icons) — never lint generated code.
tasks.withType<Detekt>().configureEach {
    exclude("**/generated/**")
}
tasks.withType<DetektCreateBaselineTask>().configureEach {
    exclude("**/generated/**")
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
