import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    id("kmp-lint")
    id("kmp-test")
}

kotlin {
    compilerOptions {
        allWarningsAsErrors = true
    }

    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }

    jvm()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        compilerOptions { optIn.add("kotlin.js.ExperimentalWasmJsInterop") }
    }
}
