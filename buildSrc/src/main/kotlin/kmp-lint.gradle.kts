plugins {
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    id("kmp-kover")
}

ktlint {
    ktlintVersion = libs.versions.ktlint.asProvider().get()
}

detekt {
    buildUponDefaultConfig = true
    allRules = true
}

dependencies {
    detektPlugins(libs.composeRules)
}
