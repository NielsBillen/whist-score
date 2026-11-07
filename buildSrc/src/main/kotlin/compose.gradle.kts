import com.skydoves.compose.stability.gradle.StabilityCheckTask
import com.skydoves.compose.stability.gradle.StabilityDumpTask

plugins {
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.stabilityAnalyzer)
}

composeStabilityAnalyzer {
    stabilityValidation {
        failOnStabilityChange.set(true)
        ignoreNonRegressiveChanges.set(true)
        allowMissingBaseline.set(true)
        unstableOnly.set(true)
    }
}

afterEvaluate {
    // The single JVM compile is named compileKotlinJvm in KMP modules but compileKotlin in
    // JVM-only apps (desktopApp); scope the stability check to whichever one exists.
    val jvmCompile = tasks.findByName("compileKotlinJvm") ?: tasks.findByName("compileKotlin")
    if (jvmCompile == null) return@afterEvaluate

    val jvmStabilityInfo = layout.buildDirectory.file("stability/${jvmCompile.name}/stability-info.json")
    tasks.named<StabilityCheckTask>("stabilityCheck").configure {
        setDependsOn(listOf(jvmCompile))
        stabilityInputFiles.setFrom(jvmStabilityInfo)
    }
    tasks.named<StabilityDumpTask>("stabilityDump").configure {
        setDependsOn(listOf(jvmCompile))
        stabilityInputFiles.setFrom(jvmStabilityInfo)
    }
}