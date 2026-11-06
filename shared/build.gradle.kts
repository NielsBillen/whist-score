plugins {
    id("kmp")
    id("compose")
    // ksp must precede kotest: kotest's wasm wiring checks for it when the wasmJs
    // target is created by the kotlin script, i.e. before later plugins are applied
    alias(libs.plugins.ksp)
    id(libs.plugins.kotest.get().pluginId)
}

kotlin {
    android {
        namespace = "be.niels.billen.shared"
    }
}
