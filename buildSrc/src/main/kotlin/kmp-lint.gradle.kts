plugins {
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
    id("kmp-kover")
}

ktlint {

}

detekt {
    buildUponDefaultConfig = true
    allRules = true
}
